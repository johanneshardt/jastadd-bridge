package org.dagjohannes;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseError;
import org.eclipse.lsp4j.services.*;
import org.tinylog.Logger;

import java.util.concurrent.CompletableFuture;

import static org.eclipse.lsp4j.jsonrpc.messages.ResponseErrorCode.ServerNotInitialized;

public class Server implements LanguageServer, LanguageClientAware {

    private LanguageClient client;
    private TextDocumentService textDocument;
    private WorkspaceService workSpace;
    private String compilerPath;
    private boolean initialized = false;

    public LanguageClient getClient() {
        return client;
    }

    public String getCompilerPath() {
        return compilerPath;
    }

    public Server(String compilerPath) {
        // TODO implement these interfaces
        this.workSpace = null;
        this.compilerPath = compilerPath;
        this.textDocument = new JastAddTDS(this);
    }

    private CompletableFuture<Object> notInitializedError() {
        var res = new CompletableFuture<>();
        var error = new ResponseError(ServerNotInitialized, "Server is not running!", null);
        res.completeExceptionally(new ResponseErrorException(error));
        return res;
    }


    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        Logger.debug("Initializing...");
        
        var clientCapabilities = params.getCapabilities(); // TODO handle these
        var serverCapabilities = new ServerCapabilities();
        var res = new InitializeResult(serverCapabilities);

        var tdcc = new TextDocumentClientCapabilities();
        tdcc.setDiagnostic(new DiagnosticCapabilities(true, true));
        clientCapabilities.setTextDocument(tdcc);

        serverCapabilities.setHoverProvider(true);
        serverCapabilities.setTextDocumentSync(TextDocumentSyncKind.Full);
        serverCapabilities.setDiagnosticProvider(new DiagnosticRegistrationOptions("jab"));

        client.showMessage(new MessageParams(MessageType.Warning, "HELLO HI HELLO WOW"));
        this.initialized = true;
        return CompletableFuture.supplyAsync(() -> res);
    }


    @Override
    public CompletableFuture<Object> shutdown() {
        if (!initialized) {
            return notInitializedError();
        }
        return CompletableFuture.completedFuture(null); // TODO error if we can't shutdown for some reason
    }

    @Override
    public void exit() {
        Logger.debug("Server exiting.");
        System.exit(0);
    }

    @Override
    public TextDocumentService getTextDocumentService() {
        return this.textDocument;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return this.workSpace;
    }

    @Override
    public void connect(LanguageClient client) {
        this.client = client;
        client.logMessage(new MessageParams(MessageType.Info, "Connected to server!"));
    }
}
