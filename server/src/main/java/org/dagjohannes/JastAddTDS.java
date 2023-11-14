package org.dagjohannes;

import codeprober.AstInfo;
import codeprober.ast.AstNode;
import codeprober.locator.NodesAtPosition;
import codeprober.metaprogramming.AstNodeApiStyle;
import codeprober.metaprogramming.TypeIdentificationStyle;
import codeprober.protocol.PositionRecoveryStrategy;
import codeprober.util.ASTProvider;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.tinylog.Logger;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class JastAddTDS implements TextDocumentService {
    private static String compilerPath;
    // maybe cache like the last 3 edits? Since you often undo/redo and there is a version number
    @NonNull
    private Optional<Document> cachedDoc;

    public JastAddTDS(String compilerPath) {
        JastAddTDS.compilerPath = compilerPath;
        cachedDoc = Optional.empty();
    }

    private String astWSpan(String prefix, AstInfo info, AstNode currentNode, StringBuilder sb) {
        sb.append(prefix + currentNode.underlyingAstNode + " @ " + currentNode.getRecoveredSpan(info) + "\n");
        for (var child : currentNode.getChildren(info)) astWSpan(prefix + "  ", info, child, sb);
        return sb.toString();
    }

    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        Logger.debug("Hovering at {}", params.getPosition());
        return CompletableFutures.computeAsync(c -> {
            c.checkCanceled();
            try {
                var doc = cachedDoc.orElse(Document.loadFile(params.getTextDocument().getUri()));
                //                    line starts at 1         2^12 bits per line          column stars at one
                var offset = (params.getPosition().getLine() + 1) * (1 << 12) + params.getPosition().getCharacter() + 1;
                var nodes = NodesAtPosition.get(doc.info, doc.rootNode, offset);
                var content = new MarkupContent(
                        MarkupKind.MARKDOWN,
                        nodes.stream()
                                .findFirst()
                                .map(n -> "**Type**: " + n.result.type)
                                .orElse("*Type not available.*"));
                return new Hover(content);
            } catch (NullPointerException e) {
                Logger.warn(e);
                return new Hover(new MarkupContent(MarkupKind.MARKDOWN, "*Type not available.*"));
            }
        });
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        cachedDoc = Optional.of(Document.loadFile(params.getTextDocument().getUri()));
        Logger.info("opened");
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        cachedDoc = cachedDoc.map(Document::refresh);
        Logger.info("changed");
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        cachedDoc = Optional.empty();
        Logger.info("closed");
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        Logger.info("saved");
    }

    private record Document(URI location, AstNode rootNode, AstInfo info) {
        private static Document load(URI location) {
            var path = new File(location).getAbsoluteFile().toString();
            var rootNode = new AstNode(ASTProvider.parseAst(compilerPath, new String[]{path}).rootNode); // TODO pass compiler args?
            var info = new AstInfo(
                    rootNode,
                    PositionRecoveryStrategy.ALTERNATE_PARENT_CHILD,
                    AstNodeApiStyle.BEAVER_PACKED_BITS,
                    TypeIdentificationStyle.REFLECTION
            );
            return new Document(location, rootNode, info);
        }

        public static Document loadFile(String location) {
            try {
                var uri = new URI(location);
                return Document.load(uri);
            } catch (URISyntaxException e) {
                throw new RuntimeException("Couldn't resolve path with URI: " + location);
            }
        }

        public Document refresh() {
            return Document.load(this.location);
        }
    }
}
