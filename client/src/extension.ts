import { ExecException, exec } from "child_process";
import { PathLike, constants } from "fs";
import { access } from "fs/promises";
import LocateJavaHome from "locate-java-home";
import {
  IJavaHomeInfo,
  ILocateJavaHome,
} from "locate-java-home/js/es5/lib/interfaces";
import common = require("mocha/lib/interfaces/common");
import * as path from "path";
import {
  workspace,
  ExtensionContext,
  WorkspaceConfiguration,
  window,
  commands,
} from "vscode";

import {
  Executable,
  LanguageClient,
  LanguageClientOptions,
  ServerOptions,
  TransportKind,
} from "vscode-languageclient/node";

let client: LanguageClient;

export function activate(context: ExtensionContext) {
  // load settings
  const settings = workspace.getConfiguration("jastaddBridge");

  fetchJavaInstallations(settings)
    .then((path) => {
      console.log(`Using java installation at: ${path}`);

      // Check if a jastadd compiler is specified in settings
      access(
        settings.get("compiler.path"),
        constants.F_OK | constants.R_OK | constants.X_OK
      )
        .then(() => launchServer(path, settings, context))
        .catch((err) => {
          console.error(`Error reading file: ${err}`);
          window
            .showErrorMessage(
              `No JastAdd compiler is defined. 
              Either there isn't a path defined or it is invalid. 
              See log for details.`,
              "Configure"
            )
            .then((selection) =>
              commands.executeCommand(
                "workbench.action.openWorkspaceSettings",
                "jastaddBridge.compiler.path"
              )
            );
        });
    })
    .catch((err) => {
      console.error(err);
      window
        .showErrorMessage(
          `No Java installation found (JDK 17 or higher required).
          You might need to setup the $JAVA_HOME environment variable.
          Alternatively, specify a path in the extension settings.
          If you have already provided it, it may be incorrect.`,
          "Change path to Java"
        )
        .then((selection) => {
          commands.executeCommand(
            "workbench.action.openWorkspaceSettings",
            "jastaddBridge.compiler.path"
          ); // TODO provide option to reload plugin here?
        });
    });
}

function launchServer(
  javaPath: string,
  settings: WorkspaceConfiguration,
  context: ExtensionContext
) {
  console.log(settings);
  const server = context.asAbsolutePath(path.join("server", "server.jar"));

  const port = 15990; // TODO determine port dynamically

  const commonOptions: Executable = {
    command: javaPath,
    args: [
      "-Djava.security.manager=allow",
      "-jar",
      server,
      settings.get("compiler.path"),
      settings.get("compiler.arguments"),
    ],
    transport: {
      kind: TransportKind.socket,
      port: port,
    },
  };
  const serverOptions: ServerOptions = {
    run: commonOptions,
    debug: commonOptions,
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

function fetchJavaInstallations(
  settings: WorkspaceConfiguration
): Promise<string> {
  const userProvidedJava: string =
    settings.get("overrideJavaHome") + "/bin/java";

  if (userProvidedJava.length > 0) {
    return new Promise((resolve, reject) => {
      exec(
        `"${userProvidedJava}" --version`, // TODO this is probably very fragile
        (error: ExecException, stdout: string, stderr: string) => {
          if (error) {
            reject(`Java install found at: "${userProvidedJava}" is invalid.`);
          } else {
            resolve(userProvidedJava);
          }
        }
      );
    });
  } else {
    // If no override is provided we try to locate a java install
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
            installations[0].executables.java;
          }
        }
      );
    });
  }
}

export function deactivate(): Thenable<void> | undefined {
  if (!client) {
    return undefined;
  }
  console.log("Shutting down extension.");
  return client.stop();
}
