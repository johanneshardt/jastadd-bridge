package org.dagjohannes;

import codeprober.AstInfo;
import codeprober.ast.AstNode;
import codeprober.metaprogramming.AstNodeApiStyle;
import codeprober.metaprogramming.TypeIdentificationStyle;
import codeprober.protocol.PositionRecoveryStrategy;
import codeprober.util.ASTProvider;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.dagjohannes.util.DiagnosticHandler;
import org.dagjohannes.util.NodesAtPosition;
import org.dagjohannes.util.Properties;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class TextDocumentAndWorkspaceImpl implements TextDocumentService, WorkspaceService {
    private static Configuration config;
    // maybe cache like the last 3 edits? Since you often undo/redo and there is a version number
    @NonNull
    private Optional<Document> cachedDoc = Optional.empty();

    // Saving here: refresh doc
    // cachedDoc
    //     .filter(d -> d.isSame(params.getTextDocument().getUri()))
    //     .ifPresentOrElse(Document::refresh, () -> Document.loadFile(params.getTextDocument().getUri()));
    // cachedDoc = cachedDoc.flatMap(Document::refresh); // TODO support multiple files


    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        Logger.debug("Hovering at {}", params.getPosition());
        Logger.info("hej");
        // check if we have a cached document, load it otherwise
        // this is ONLY empty() if parsing the ast throws NullPointerException
        // cachedDoc = cachedDoc
        //         .filter(d -> d.location.toString()
        //                 .equals(params.getTextDocument().getUri()))
        //         .or(() -> Document.loadFile(params.getTextDocument().getUri()));
        cachedDoc = Document.loadFile(params.getTextDocument().getUri());
        cachedDoc.ifPresent(d -> Logger.info("Loaded document: {}", d.location));
        var response = cachedDoc.flatMap(d -> NodesAtPosition
            .get(d.info, d.rootNode, params.getPosition(), d.location)
            .stream()
            .findFirst()
            .flatMap(Properties::hover) // try to invoke the hover attribute
            .map(h -> new Hover(new MarkupContent(MarkupKind.MARKDOWN, h)))
        ).orElse(null);
        // var hover = new Hover(new MarkupContent(MarkupKind.MARKDOWN, response));

        return CompletableFuture.completedFuture(response);
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        DiagnosticHandler.refresh();
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
        DiagnosticHandler.clear(uri);
        Logger.info("closed");
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        DiagnosticHandler.refresh();
        Logger.info("saved");
    }

    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams params) {
        var settings = ((JsonObject) params.getSettings()).getAsJsonObject("jastaddBridge");
        var compiler = settings.getAsJsonObject("compiler");
        var compilerPath = compiler.get("path").getAsString();
        var compilerArgs = compiler.getAsJsonArray("arguments");
        var cacheStrategy = settings.get("cacheStrategy").getAsString();
        boolean purgeCache = switch (cacheStrategy) {
            case "partial" -> false;
            case "purge" -> true;
            default -> {
                Logger.error("Invalid configuration option '{}' for setting 'Cache Strategy'", cacheStrategy);
                yield false;
            }
        };
        Logger.info("Received configuration: compiler path={}, compiler args={}. cache strategy={}", compilerPath, compilerArgs, cacheStrategy);
        TextDocumentAndWorkspaceImpl.config = new Configuration(compilerPath, compilerArgs.asList().stream().map(JsonElement::getAsString).toList(), purgeCache);
    }

    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
        Logger.error("Not implemented yet!");
    }

    @Override
    public CompletableFuture<DocumentDiagnosticReport> diagnostic(DocumentDiagnosticParams params) {
        return Document
                .loadFile(params.getTextDocument().getUri())
                .flatMap(doc -> Properties
                    .getDiagnostics(doc.rootNode())
                    .map(DiagnosticHandler::report))
                .orElseGet(DiagnosticHandler::emptyReport);
    }

    @Override
    public CompletableFuture<List<Either<Command, CodeAction>>> codeAction(CodeActionParams params) {
        cachedDoc = Document.loadFile(params.getTextDocument().getUri());

        CodeAction action = new CodeAction("thing");
        action.setKind(CodeActionKind.QuickFix);
        action.setDiagnostics(params.getContext().getDiagnostics());
        action.setIsPreferred(true);

        TextDocumentEdit textEdit = new TextDocumentEdit();
        textEdit.setEdits(
            List.of(
                new TextEdit(new Range(new Position(1, 1), new Position(1, 2)), 
                "hej")
            )
        );
        WorkspaceEdit edit = new WorkspaceEdit(List.of(Either.forLeft(textEdit)));
        action.setEdit(edit);

        return CompletableFuture.completedFuture(List.of(Either.forRight(action)));
    };

    // känns som att denna behöver decouplas/omarbetas ganska rejält
    private record Document(URI location, AstNode rootNode, AstInfo info) {
        // TODO add option that calls purgeCache() before parseAst()
        private static Optional<Document> parse(URI location) {
            try {
                var path = new File(location).getAbsoluteFile().toString();
                if (config.purgeCache) ASTProvider.purgeCache();
                // TODO pass compiler args?

                var arguments = new ArrayList<>(List.of(path));
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
            try {
                return Files.isSameFile(new File(location).getAbsoluteFile().toPath(), Paths.get(resolveURI(otherURI)));
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

    private record Configuration(String compilerPath, List<String> compilerArgs, boolean purgeCache) {
    }
}
