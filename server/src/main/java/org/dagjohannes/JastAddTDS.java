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
import org.eclipse.lsp4j.services.TextDocumentService;
import org.tinylog.Logger;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

public class JastAddTDS implements TextDocumentService {
    private String compilerPath;

    public JastAddTDS(String compilerPath) {
        this.compilerPath = compilerPath;
    }

    private String astWSpan(String prefix, AstInfo info, AstNode currentNode, StringBuilder sb) {
        sb.append(prefix + currentNode.underlyingAstNode + " @ " + currentNode.getRecoveredSpan(info) + "\n");
        for (var child : currentNode.getChildren(info)) astWSpan(prefix + "  ", info, child, sb);
        return sb.toString();
    }

    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        Logger.debug("Hovering at position: {}", params.getPosition());
        return CompletableFutures.computeAsync(c -> {
            c.checkCanceled();
            try {
                // resolve URI
                var path = new File(new URI(params.getTextDocument().getUri())).getAbsoluteFile().toString();
                var rootNode = new AstNode(ASTProvider.parseAst(compilerPath, new String[]{path}).rootNode); // TODO pass compiler args?
                var info = new AstInfo(
                        rootNode,
                        PositionRecoveryStrategy.ALTERNATE_PARENT_CHILD,
                        AstNodeApiStyle.BEAVER_PACKED_BITS,
                        TypeIdentificationStyle.REFLECTION
                );
                //                    line starts at 1         2^12 bits per line          column stars at one
                var offset = (params.getPosition().getLine() + 1) * (1 << 12) + params.getPosition().getCharacter() + 1;
                var nodes = NodesAtPosition.get(info, rootNode, offset);
                var content = new MarkupContent(
                        MarkupKind.MARKDOWN,
                        nodes.stream()
                                .findFirst()
                                .map(n -> "**Type**: " + n.result.type)
                                .orElse("*Type not available.*"));
                return new Hover(content);
            } catch (URISyntaxException e) {
                throw new RuntimeException("Couldn't resolve path with URI: " + params.getTextDocument().getUri());
            } catch (NullPointerException e) {
                Logger.warn(e);
                return new Hover(new MarkupContent(MarkupKind.MARKDOWN, "*Type not available.*"));
            }
        });
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        Logger.info("opened");
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        Logger.info("changed");
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        Logger.info("closed");
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        Logger.info("saved");
    }

}
