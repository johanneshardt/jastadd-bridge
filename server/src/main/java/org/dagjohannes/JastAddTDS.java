package org.dagjohannes;

import codeprober.AstInfo;
import codeprober.ast.AstNode;
import codeprober.metaprogramming.AstNodeApiStyle;
import codeprober.metaprogramming.TypeIdentificationStyle;
import codeprober.protocol.PositionRecoveryStrategy;
import codeprober.util.ASTProvider;

import org.dagjohannes.util.DiagnosticHandler;
import org.dagjohannes.util.NodesAtPosition;
import org.dagjohannes.util.Properties;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class JastAddTDS implements TextDocumentService {
    // private static String compilerPath;
    private static Server server;
    // maybe cache like the last 3 edits? Since you often undo/redo and there is a version number
    @NonNull
    private Optional<Document> cachedDoc;
    private DiagnosticHandler diagnosticsHandler;

    public JastAddTDS(Server server) {
        JastAddTDS.server = server;
        cachedDoc = Optional.empty();
        diagnosticsHandler = new DiagnosticHandler(server);
    }

    // Saving here: refresh doc
    // cachedDoc
    //     .filter(d -> d.isSame(params.getTextDocument().getUri()))
    //     .ifPresentOrElse(Document::refresh, () -> Document.loadFile(params.getTextDocument().getUri()));
    // cachedDoc = cachedDoc.flatMap(Document::refresh); // TODO support multiple files

    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        Logger.debug("Hovering at {}", params.getPosition());
        // check if we have a cached document, load it otherwise
        // this is ONLY empty() if parsing the ast throws NullPointerException
        cachedDoc = cachedDoc
                .filter(d -> d.location.toString()
                        .equals(params.getTextDocument().getUri())).map(d -> {
                    System.out.println("IT MATCHES");
                    return d;
                })
                .or(() -> Document.loadFile(params.getTextDocument().getUri()));
        var content = cachedDoc.flatMap(d -> {
            // line, col are 1-indexed. Each line is 2^12 == 4096 long.
            var nodes = NodesAtPosition.get(d.info, d.rootNode, params.getPosition(), d.location); // Rewrite our own TODO
            return nodes.stream().findFirst().flatMap(Properties::hover); // try to invoke the hover attribute
        }).orElse("*Hover not available.* You might need to define an attribute ```syn String ASTNode.hover()``` in your compiler.");
        var hover = new Hover(new MarkupContent(MarkupKind.MARKDOWN, content));

        return CompletableFuture.completedFuture(hover);
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        diagnosticsHandler.refresh();
        Logger.info("opened");
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        // only update diagnostics on save
        Logger.info("changed");
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        cachedDoc = Optional.empty();
        var uri = params.getTextDocument().getUri();
        diagnosticsHandler.clear(uri);
        Logger.info("closed");
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        diagnosticsHandler.refresh();
        Logger.info("saved");
    }

    @Override
    public CompletableFuture<DocumentDiagnosticReport> diagnostic(DocumentDiagnosticParams params) {
        return Document
            .loadFile(params.getTextDocument().getUri())
            .flatMap(doc -> Properties
                .getDiagnostics(doc.rootNode())
                .map(DiagnosticHandler::report))
            .orElseGet(DiagnosticHandler::emptyReport);
    };

    // känns som att denna behöver decouplas/omarbetas ganska rejält
    private record Document(URI location, AstNode rootNode, AstInfo info) {
        // TODO add option that calls purgeCache() before parseAst()
        private static Optional<Document> parse(URI location) {
            try {
                var path = new File(location).getAbsoluteFile().toString();
                // TODO pass compiler args?

                var rootNode = new AstNode(
                    ASTProvider.parseAst(server.getCompilerPath(), 
                    new String[]{path}).rootNode
                ); 

                var info = new AstInfo(
                    rootNode, 
                    PositionRecoveryStrategy.ALTERNATE_PARENT_CHILD, 
                    AstNodeApiStyle.BEAVER_PACKED_BITS, 
                    TypeIdentificationStyle.REFLECTION
                );

                return Optional.of(new Document(location, rootNode, info));
            } catch (NullPointerException e) {
                Logger.error(e);
                return Optional.empty();
            }
        }

        public static Optional<Document> loadFile(String location) {
            Logger.debug("Loading file: ", location);
            var uri = resolveURI(location);
            return Document.parse(uri);
        }

        public boolean isSame(String otherURI) {
            var other = resolveURI(otherURI);
            try {
                return Files.isSameFile(
                    new File(location).getAbsoluteFile().toPath(), 
                    new File(otherURI).getAbsoluteFile().toPath()
                );
            } catch (IOException e) {
                Logger.error(e);
                return false; // TODO could be better
            }
        }

        private static URI resolveURI(String documentURI) {
            try {
                return new URI(documentURI);
            } catch (URISyntaxException e) {
                throw new RuntimeException("Couldn't resolve path with URI: " + documentURI);
            }
        }

        public Optional<Document> refresh() {
            return Document.parse(this.location);
        }
    }
}
