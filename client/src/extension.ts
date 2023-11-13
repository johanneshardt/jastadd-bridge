import LocateJavaHome from "locate-java-home";
import {
  IJavaHomeInfo,
  ILocateJavaHome,
} from "locate-java-home/js/es5/lib/interfaces";
import * as path from "path";
import {
  workspace,
  ExtensionContext,
  WorkspaceConfiguration,
  window,
  commands,
} from "vscode";

import {
  LanguageClient,
  LanguageClientOptions,
  ServerOptions,
  TransportKind,
} from "vscode-languageclient/node";

let client: LanguageClient;

export function activate(context: ExtensionContext) {
  function fetchJavaInstallations(): Promise<IJavaHomeInfo[]> {
    return new Promise((resolve, reject) => {
      LocateJavaHome(
        {
          version: ">= 17.0",
          mustBeJDK: true,
        },
        function (error, installations) {
          if (error) {
            reject(error);
          } else if (installations.length === 0) {
            reject();
          } else {
            resolve(installations);
          }
        }
      );
    });
  }

  fetchJavaInstallations()
    .then((installations) => {
      const p = installations[0];
      console.log(`Using java installation at: ${p}`);
      // load settings
      const settings = workspace.getConfiguration("jastaddBridge");
      launchServer(p.executables.java, settings);
    })
    .catch(() =>
      window
        .showErrorMessage(
          `No Java installation found (JDK 17 or higher required).
          You might need to setup the $JAVA_HOME environment variable.
          Alternatively, specify a path in the extension settings.`,
          "Change path to Java"
        )
        .then((selection) => {
          commands.executeCommand(
            "workbench.action.openWorkspaceSettings",
            "jastaddBridge.compiler.path"
          ); // TODO provide option to reload plugin here?
        })
    );

  function launchServer(javaPath: string, settings: WorkspaceConfiguration) {
    console.log(settings);
    const serverModule = context.asAbsolutePath(
      path.join("server", "server.jar")
    );

    const port = 15990; // TODO determine port dynamically

    const serverOptions: ServerOptions = {
      run: {
        command: javaPath,
        args: [
          "-jar",
          serverModule,
          settings.get("compiler.path"),
          settings.get("compiler.arguments"),
        ],
        transport: {
          kind: TransportKind.socket,
          port: port,
        },
      },
      debug: {
        command: javaPath,
        args: [
          "-jar",
          serverModule,
          settings.get("compiler.path"),
          settings.get("compiler.arguments"),
        ],
        transport: {
          kind: TransportKind.socket,
          port: port,
        },
      },
    };

    // Options to control the language client
    const clientOptions: LanguageClientOptions = {
      // Register the server for plain text documents
      documentSelector: [{ scheme: "file", language: "jastadd" }], // TODO figure out extension
      synchronize: {
        // Notify the server about file changes to '.clientrc files contained in the workspace
        fileEvents: workspace.createFileSystemWatcher("**/.clientrc"),
      },
    };

    // Create the language client and start the client.
    client = new LanguageClient(
      "jastadd-bridge",
      "JastAdd Bridge",
      serverOptions,
      clientOptions
    );

    // Start the client. This will also launch the server
    client.start();
  }
}

export function deactivate(): Thenable<void> | undefined {
  if (!client) {
    return undefined;
  }
  return client.stop();
}
