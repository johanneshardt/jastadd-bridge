package org.dagjohannes;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.tinylog.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.List;


public class Main {
    // TODO improve argument handling
    public static void main(String[] args) throws IOException {
        Logger.info("Args received:  {}", String.join("\n  ", args));
        var port = Integer.parseInt((args[args.length - 1]).split("=")[1]); // port is always last argument
        var compilerPath = args[0];
        var compilerArgs = List.of(args).subList(1, args.length - 1); // all other arguments are passed to the compiler
        Logger.info("Server started, awaiting connection on ->" + port);

        // Start listening on localhost
        try {
            var socket = new Socket("127.0.0.1", port);
            var input = socket.getInputStream();
            var output = socket.getOutputStream();

            // see https://github.com/eclipse-lsp4j/lsp4j/blob/main/documentation/README.md
            var server = new Server(compilerPath);
            Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, input, output);
            LanguageClient client = launcher.getRemoteProxy();
            server.connect(client);
            Logger.info("Connected to client, listening...");
            launcher.startListening();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}