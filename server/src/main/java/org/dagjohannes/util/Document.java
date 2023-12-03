package org.dagjohannes.util;

import codeprober.AstInfo;
import codeprober.ast.AstNode;
import codeprober.metaprogramming.AstNodeApiStyle;
import codeprober.metaprogramming.TypeIdentificationStyle;
import codeprober.protocol.PositionRecoveryStrategy;
import codeprober.util.ASTProvider;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.VersionedTextDocumentIdentifier;
import org.tinylog.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.dagjohannes.TextDocumentAndWorkspaceImpl.config;


public class Document {
    public VersionedTextDocumentIdentifier ident;
    public Path documentPath;
    public AstNode rootNode;
    public AstInfo info;

    private Document(VersionedTextDocumentIdentifier ident, Path documentPath, AstNode rootNode, AstInfo info) {
        this.ident = ident;
        this.documentPath = documentPath;
        this.rootNode = rootNode;
        this.info = info;
    }

    public static Optional<Document> loadFile(Optional<Document> old, VersionedTextDocumentIdentifier vid) {
        if (config == null) {
            return Optional.empty();
        }
        return loadCached(old, vid);
    }

    public static Optional<Document> loadFile(Optional<Document> old, TextDocumentItem item) {
        if (config == null) {
            return Optional.empty();
        }
        return loadCached(old, new VersionedTextDocumentIdentifier(item.getUri(), item.getVersion()));
    }

    public static Optional<Document> loadFile(TextDocumentIdentifier id) {
        if (config == null) {
            return Optional.empty();
        } else {
            return parse(id.getUri());
        }
    }

    private static Optional<Document> loadCached(Optional<Document> old, VersionedTextDocumentIdentifier vid) {
        return old.flatMap(d -> {
            if (d.ident.equals(vid)) {
                Logger.debug("{} was already cached!", vid.getUri());
                return Optional.of(d);
            } else {
                return Optional.empty();
            }
        }).or(() -> {
            Logger.debug("Loading document: {}", vid.getUri());
            return parse(vid.getUri());
        });
    }


    private static Optional<Document> parse(String uri) {
        try {
            if (config.purgeCache()) ASTProvider.purgeCache();

            var documentPath = resolve(uri);
            var arguments = new ArrayList<>(List.of(documentPath.toFile().getAbsolutePath()));
            arguments.addAll(config.compilerArgs()); // User arguments are passed after the file path

            var rootNode = new AstNode(
                    ASTProvider.parseAst(config.compilerPath(),
                            arguments.toArray(String[]::new)).rootNode
            );

            var info = new AstInfo(
                    rootNode,
                    PositionRecoveryStrategy.ALTERNATE_PARENT_CHILD,
                    AstNodeApiStyle.BEAVER_PACKED_BITS,
                    TypeIdentificationStyle.REFLECTION
            );

            // Is version == 0 valid? TODO
            return Optional.of(new Document(new VersionedTextDocumentIdentifier(uri, 0), documentPath, rootNode, info));
        } catch (NullPointerException e) {
            Logger.error(e, "Couldn't load document at '{}'", uri);
            return Optional.empty();
        }
    }


    private static Path resolve(String documentURI) {
        try {
            return Paths.get(new URI(documentURI));
        } catch (URISyntaxException e) {
            throw new RuntimeException("Couldn't resolve path with URI: " + documentURI); // TODO better error handling?
        }
    }
}