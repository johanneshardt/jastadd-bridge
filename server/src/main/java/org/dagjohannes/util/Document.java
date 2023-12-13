package org.dagjohannes.util;

import codeprober.AstInfo;
import codeprober.ast.AstNode;
import codeprober.metaprogramming.AstNodeApiStyle;
import codeprober.metaprogramming.TypeIdentificationStyle;
import codeprober.protocol.PositionRecoveryStrategy;
import codeprober.util.ASTProvider;
import codeprober.util.MagicStdoutMessageParser;
import org.eclipse.lsp4j.*;
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
    public static List<Diagnostic> parseErrors = new ArrayList<>();

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

    public static Optional<Document> loadFile(Optional<Document> old, TextDocumentIdentifier id) {
        if (config == null) {
            return Optional.empty();
        } else {
            return old.flatMap(d -> {
                if (d.ident.getUri().equals(id.getUri())) {
                    Logger.debug("{} was already cached!", id.getUri());
                    return Optional.of(d);
                } else {
                    return Optional.empty();
                }
            }).or(() -> {
                Logger.debug("Loading document: {}", id.getUri());
                return parse(id.getUri());
            });
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

    public void refresh() {
        parse(this.ident.getUri()).ifPresent(d -> {
            this.ident = d.ident;
            this.documentPath = d.documentPath;
            this.info = d.info;
            this.rootNode = d.rootNode;
        });
    }


    private static Optional<Document> parse(String uri) {
        Document.parseErrors = new ArrayList<>(); // Clear parsing errors each time
        if (config.purgeCache()) ASTProvider.purgeCache();
        var documentPath = resolve(uri);

        var arguments = new ArrayList<>(List.of(documentPath.toFile().getAbsolutePath()));
        arguments.addAll(config.compilerArgs()); // User arguments are passed after the file path
        var ast = ASTProvider.parseAst(config.compilerPath(), arguments.toArray(String[]::new));
        try {
            var rootNode = new AstNode(ast.rootNode);
            var info = new AstInfo(
                    rootNode,
                    PositionRecoveryStrategy.ALTERNATE_PARENT_CHILD,
                    AstNodeApiStyle.BEAVER_PACKED_BITS,
                    TypeIdentificationStyle.REFLECTION
            );
            // Is version == 0 valid? TODO
            return Optional.of(new Document(new VersionedTextDocumentIdentifier(uri, 0), documentPath, rootNode, info));
        } catch (NullPointerException e) {
            // Parsing failed, check if we can calculate a diagnostic
            // See codeprober.requesthandler.listnodeshandler
            for (var line : ast.captures) {
                switch (line.type) {
                    case stdout: {
                        final var diagnostic = MagicStdoutMessageParser.parse(line.asStdout());
                        if (diagnostic != null) {
                            var d = new org.eclipse.lsp4j.Diagnostic(
                                    bitsToRange(diagnostic.start, diagnostic.end),
                                    diagnostic.msg
                            );
                            Logger.error("Parsing error: {}", d);
                            parseErrors.add(d);
                        }
                        break;
                    }

                    case stderr:
                        final var diagnostic = MagicStdoutMessageParser.parse(line.asStderr());
                        if (diagnostic != null) {
                            var d = new org.eclipse.lsp4j.Diagnostic(
                                    bitsToRange(diagnostic.start, diagnostic.end),
                                    diagnostic.msg
                            );
                            Logger.error("Parsing error: {}", d);
                            parseErrors.add(d);
                        }
                        break;

                    default:
                        break;
                }
            }
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

    /**
     * Converts the code prober packed bits representation to a LSP4J range
     *
     * @param start - packed representation of startLine, startCol
     * @param end   - packed representation of endLine, endCol
     */
    private static Range bitsToRange(int start, int end) {
        var startCol = start & ((1 << 12) - 1);
        var startLine = start >> 12;
        var endCol = end & ((1 << 12) - 1);
        var endLine = end >> 12;
        return new Range(new Position(startLine - 1, startCol - 1), new Position(endLine - 1, endCol));
    }
}