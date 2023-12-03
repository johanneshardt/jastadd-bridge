package org.dagjohannes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.dagjohannes.util.DiagnosticHandler;
import org.dagjohannes.util.Document;
import org.dagjohannes.util.NodesAtPosition;
import org.dagjohannes.util.Properties;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.tinylog.Logger;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class TextDocumentAndWorkspaceImpl implements TextDocumentService, WorkspaceService {
    public static Configuration config;
    // maybe cache like the last 3 edits? Since you often undo/redo and there is a version number

    // Saving here: refresh doc
    // cachedDoc
    //     .filter(d -> d.isSame(params.getTextDocument().getUri()))
    //     .ifPresentOrElse(Document::refresh, () -> Document.loadFile(params.getTextDocument().getUri()));
    // cachedDoc = cachedDoc.flatMap(Document::refresh); // TODO support multiple files

    Optional<Document> currentDocument = Optional.empty();


    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        Logger.debug("Hovering at {}", params.getPosition());
        currentDocument = currentDocument.flatMap(d -> d.loadFile(params.getTextDocument()));
        var response = currentDocument.flatMap(d -> NodesAtPosition
                .get(d.info, d.rootNode, params.getPosition(), d.documentPath)
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
        currentDocument = currentDocument.flatMap(d -> d.loadFile(params.getTextDocument()));
//        currentDocumentVersion = new VersionedTextDocumentIdentifier(params.getTextDocument().getUri(), params.getTextDocument().getVersion());
        DiagnosticHandler.refresh();
        Logger.info("opened");
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        // TODO implement document change handling
        currentDocument = currentDocument.flatMap((d -> d.loadFile(params.getTextDocument())));
        // only update diagnostics on save
        Logger.info("changed");
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        currentDocument = Optional.empty();
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
        return currentDocument
                .flatMap(d -> d.loadFile(params.getTextDocument()))
                .flatMap(doc -> Properties
                        .getDiagnostics(doc.rootNode)
                        .map(DiagnosticHandler::report))
                .orElseGet(DiagnosticHandler::emptyReport);
    }

    @Override
    public CompletableFuture<List<Either<Command, CodeAction>>> codeAction(CodeActionParams params) {
        currentDocument = Document.loadFile(currentDocument, params.getTextDocument());

        var response = currentDocument.map(d -> {
            var textEdit = new TextDocumentEdit(
                    d.ident,
                    List.of(
                            new TextEdit(new Range(new Position(1, 1), new Position(1, 2)), "hej")
                    ));

            var edit = new WorkspaceEdit(List.of(Either.forLeft(textEdit)));
            CodeAction action = new CodeAction("thing");
            action.setKind(CodeActionKind.QuickFix);
            action.setDiagnostics(params.getContext().getDiagnostics());
            action.setIsPreferred(true);
            action.setEdit(edit);
            return List.<Either<Command, CodeAction>>of(Either.forRight(action));
        }).orElse(List.<Either<Command, CodeAction>>of());

        return CompletableFuture.completedFuture(response); // TODO fix
    }
}