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
    Optional<Document> currentDocument = Optional.empty();


    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        var v = currentDocument.map(d -> {
            Logger.debug(d.ident);
            return d.ident;
        }).orElse(new VersionedTextDocumentIdentifier(params.getTextDocument().getUri(), 1));
        Logger.debug("Hovering at {}", params.getPosition());
        currentDocument = Document.loadFile(currentDocument, v);
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
        currentDocument = Document.loadFile(currentDocument, params.getTextDocument());
//        currentDocumentVersion = new VersionedTextDocumentIdentifier(params.getTextDocument().getUri(), params.getTextDocument().getVersion());
        DiagnosticHandler.refresh();
        Logger.info("opened");
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        // TODO implement document change handling
        currentDocument = Document.loadFile(currentDocument, params.getTextDocument());
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
        currentDocument = Document.loadFile(params.getTextDocument());
        return currentDocument
                .flatMap(doc -> Properties
                        .getDiagnostics(doc.rootNode)
                        .map(DiagnosticHandler::report))
                .orElseGet(DiagnosticHandler::emptyReport);
    }

    private boolean inRange(Range outer, Range inner) {
        return outer.getStart().getLine() <= inner.getStart().getLine()
                && outer.getStart().getCharacter() <= inner.getStart().getCharacter()
                && outer.getEnd().getLine() >= inner.getEnd().getLine()
                && outer.getEnd().getCharacter() >= inner.getEnd().getCharacter();
    }

    @Override
    public CompletableFuture<List<Either<Command, CodeAction>>> codeAction(CodeActionParams params) {
        currentDocument = Document.loadFile(params.getTextDocument());

        List<Either<Command, CodeAction>> actions = currentDocument.flatMap(doc -> Properties
                .getCodeActions(doc.rootNode, doc.ident)
                .map(codeActions -> {
                    Logger.info("Code actions: {}", codeActions);
                    return codeActions
                            .stream()
                            .filter(d -> inRange(d.getDiagnostics().get(0).getRange(), params.getRange()))
                            .map(Either::<Command, CodeAction>forRight)
                            .toList();
                })
        ).orElse(List.of());
        return CompletableFuture.completedFuture(actions);
    }

    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(DefinitionParams params) {
        currentDocument = Document.loadFile(params.getTextDocument());
        List<LocationLink> loc = currentDocument.flatMap(doc -> {
            var node = NodesAtPosition.get(doc.info, doc.rootNode, params.getPosition(), doc.documentPath).stream().findFirst().get();
            return Properties
                .getDefinition(node, params.getPosition(), params.getTextDocument().getUri())
                .map(ll -> List.of(ll));
        }).orElse(List.of());

        return CompletableFuture.completedFuture(Either.forRight(loc));
    }
}