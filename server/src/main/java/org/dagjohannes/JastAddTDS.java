package org.dagjohannes;

import codeprober.AstInfo;
import codeprober.ast.AstNode;
import codeprober.locator.ApplyLocator;
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
import java.lang.reflect.InvocationTargetException;
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

    static String allProperties(AstInfo info, AstNode node, StringBuilder s) {
        for (var ch : node.getChildren(info)) {
            s.append(String.join(", ", ch.propertyListShow(info)));
            allProperties(info, ch, s);
        }
        return s.toString();
    }

    private String astWSpan(String prefix, AstInfo info, AstNode currentNode, StringBuilder sb) {
        sb.append(prefix + currentNode.underlyingAstNode + " @ " + currentNode.getRecoveredSpan(info) + "\n");
        for (var child : currentNode.getChildren(info)) astWSpan(prefix + "  ", info, child, sb);
        return sb.toString();
    }

    private <T> Optional<T> invoke(AstNode node, Class<T> type, String methodName, Object... args) {
        try {
            return Optional.of(type.cast(node.underlyingAstNode.getClass().getMethod(methodName).invoke(node.underlyingAstNode, args)));

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Logger.error(e);
            return Optional.empty();
        }
    }

    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        Logger.debug("Hovering at {}", params.getPosition());
        return CompletableFutures.computeAsync(c -> {
            c.checkCanceled();
            // check if we have a cached document, load it otherwise
            // this is only empty() if parsing the ast throws nullpointerexeption
            cachedDoc = cachedDoc.or(() -> Document.loadFile(params.getTextDocument().getUri()));
            var content = cachedDoc.flatMap(d -> {
                // line, col are 1-indexed. Each line is 2^12 == 4096 long.
                var offset = (params.getPosition().getLine() + 1) * (1 << 12) + params.getPosition().getCharacter() + 1;
                var nodes = NodesAtPosition.get(d.info, d.rootNode, offset);
                var node = nodes.stream().findFirst().map(n -> ApplyLocator.toNode(d.info, n).node);
                return node.flatMap(n -> invoke(n, String.class, "hover")); // try to invoke the hover attribute
            }).orElse("*Hover not available.* You might need to define an attribute ```syn String ASTNode.hover()``` in your compiler.");
            return new Hover(new MarkupContent(MarkupKind.MARKDOWN, content));
        });
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        cachedDoc = Document.loadFile(params.getTextDocument().getUri());
        Logger.info("opened");
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        cachedDoc = cachedDoc.flatMap(Document::refresh);
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
        private static Optional<Document> parse(URI location) {
            try {
                var path = new File(location).getAbsoluteFile().toString();
                var rootNode = new AstNode(ASTProvider.parseAst(compilerPath, new String[]{path}).rootNode); // TODO pass compiler args?
                var info = new AstInfo(rootNode, PositionRecoveryStrategy.ALTERNATE_PARENT_CHILD, AstNodeApiStyle.BEAVER_PACKED_BITS, TypeIdentificationStyle.REFLECTION);
                return Optional.of(new Document(location, rootNode, info));
            } catch (NullPointerException e) {
                Logger.error(e);
                return Optional.empty();
            }

        }

        public static Optional<Document> loadFile(String location) {
            try {
                Logger.debug("Loading file: ", location);
                var uri = new URI(location);
                return Document.parse(uri);
            } catch (URISyntaxException e) {
                throw new RuntimeException("Couldn't resolve path with URI: " + location);
            }
        }

        public Optional<Document> refresh() {
            return Document.parse(this.location);
        }
    }
}
