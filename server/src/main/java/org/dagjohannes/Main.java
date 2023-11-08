package org.dagjohannes;

import java.io.IOException;
import java.net.Socket;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.tinylog.Logger;


public class Main {
    public static void main(String[] args) throws IOException {
        var port = 15990; // TODO read this from somewhere or smth
        Logger.debug("Server started, awaiting connection on -> {}", port);

        // Start listening on localhost
        try {
            var socket = new Socket("127.0.0.1", port);
            var input = socket.getInputStream();
            var output = socket.getOutputStream();

            // see https://github.com/eclipse-lsp4j/lsp4j/blob/main/documentation/README.md
            var server = new Server();
            Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, input, output);
            LanguageClient client = launcher.getRemoteProxy();
            server.connect(client);
            Logger.debug("Connected to client, listening...");
            launcher.startListening();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}