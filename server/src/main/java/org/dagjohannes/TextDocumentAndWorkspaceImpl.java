package org.dagjohannes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.dagjohannes.util.DiagnosticHandler;
import org.dagjohannes.util.Document;
import org.dagjohannes.util.NodesAtPosition;
import org.dagjohannes.util.Properties;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.tinylog.Logger;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class TextDocumentAndWorkspaceImpl implements TextDocumentService, WorkspaceService {
    public static Configuration config;
    Optional<Document> doc = Optional.empty();


    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        var v = doc.map(d -> {
            Logger.debug(d.ident);
            return d.ident;
        }).orElse(new VersionedTextDocumentIdentifier(params.getTextDocument().getUri(), 1));
        Logger.debug("Hovering at {}", params.getPosition());
        doc = Document.loadFile(doc, v);
        return CompletableFutures.computeAsync(c -> {
            c.checkCanceled();
            return doc.flatMap(d -> NodesAtPosition
                    .get(d.info, d.rootNode, params.getPosition(), d.documentPath)
                    .stream()
                    .findFirst()
                    .flatMap(Properties::hover) // try to invoke the hover attribute
                    .map(h -> new Hover(new MarkupContent(MarkupKind.MARKDOWN, h)))
            ).orElse(null);
        });
        // var hover = new Hover(new MarkupContent(MarkupKind.MARKDOWN, response));

        // return CompletableFuture.completedFuture(response);
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        doc = Document.loadFile(doc, params.getTextDocument());
//        currentDocumentVersion = new VersionedTextDocumentIdentifier(params.getTextDocument().getUri(), params.getTextDocument().getVersion());
        DiagnosticHandler.refresh();
        Logger.info("opened");
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        // TODO implement document change handling
        doc = Document.loadFile(doc, params.getTextDocument());
        // only update diagnostics on save
        Logger.info("changed");
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        doc = Optional.empty();
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
        doc.ifPresent(d -> {
            var uris = params
                    .getChanges()
                    .stream()
                    .map(FileEvent::getUri)
                    .toList();
            if (uris.contains(d.ident.getUri())) d.refresh();
        });
    }

    @Override
    public CompletableFuture<DocumentDiagnosticReport> diagnostic(DocumentDiagnosticParams params) {
        doc = Document.loadFile(doc, params.getTextDocument());
        return doc
                .flatMap(d -> Properties
                        .getDiagnostics(d.rootNode)
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
        doc = Document.loadFile(doc, params.getTextDocument());

        List<Either<Command, CodeAction>> actions = doc.flatMap(d -> Properties
                .getCodeActions(d.rootNode, d.ident)
                .map(codeActions -> {
                    Logger.info("Code actions: {}", codeActions);
                    return codeActions
                            .stream()
                            .filter(a -> inRange(a.getDiagnostics().get(0).getRange(), params.getRange()))
                            .map(Either::<Command, CodeAction>forRight)
                            .toList();
                })
        ).orElse(List.of());
        return CompletableFuture.completedFuture(actions);
    }

    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(DefinitionParams params) {
        doc = Document.loadFile(doc, params.getTextDocument());
        List<LocationLink> loc = doc.flatMap(d -> {
            var node = NodesAtPosition.get(d.info, d.rootNode, params.getPosition(), d.documentPath).stream().findFirst().get();
            return Properties
                    .getDefinition(node, params.getPosition(), params.getTextDocument().getUri())
                    .map(List::of);
        }).orElse(List.of());

        return CompletableFuture.completedFuture(Either.forRight(loc));
    }

    @Override
    public CompletableFuture<List<? extends CodeLens>> codeLens(CodeLensParams params) {
        doc = Document.loadFile(doc, params.getTextDocument());
        List<? extends CodeLens> lenses = doc.flatMap(doc -> Properties
                .getRunLens(doc.rootNode)
                .map(r -> List.of(r))
        ).orElse(List.of());
        return CompletableFuture.completedFuture(lenses);
    }

    @Override
    public CompletableFuture<Object> executeCommand(ExecuteCommandParams params) {
        doc.ifPresent(d -> {
            Properties.run(d.rootNode);
        });
        return CompletableFuture.completedFuture(null);

    }
}