package org.dagjohannes;

import codeprober.AstInfo;
import codeprober.ast.AstNode;
import codeprober.metaprogramming.AstNodeApiStyle;
import codeprober.metaprogramming.TypeIdentificationStyle;
import codeprober.protocol.PositionRecoveryStrategy;
import codeprober.util.ASTProvider;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.tinylog.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.List;


public class Main {
    public static void printAst(String prefix, AstInfo info, AstNode currentNode) {
        System.out.println(prefix + currentNode);
        for (var child : currentNode.getChildren(info)) {
            printAst(prefix + "  ", info, child);
        }
    }
    // TODO improve argument handling
    public static void main(String[] args) throws IOException {
        Logger.debug("Args received:\n  " + String.join("\n  ", args));
        var port = Integer.parseInt((args[args.length-1]).split("=")[1]); // port is always last argument
        var compilerPath = args[0];
        var compilerArgs = List.of(args).subList(1, args.length-1); // all other arguments are passed to the compiler
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
            var a = ASTProvider.parseAst(compilerPath, compilerArgs.toArray(String[]::new));
            var rootNode = new AstNode(a.rootNode);
            var info = new AstInfo(
                    rootNode,
                    PositionRecoveryStrategy.ALTERNATE_PARENT_CHILD,
                    AstNodeApiStyle.BEAVER_PACKED_BITS,
                    TypeIdentificationStyle.REFLECTION
            );

            printAst("  ", info, rootNode);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}