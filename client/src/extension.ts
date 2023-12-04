import { ExecException, exec } from "child_process";
import { constants } from "fs";
import { access } from "fs/promises";
import LocateJavaHome from "locate-java-home";
import common = require("mocha/lib/interfaces/common");
import * as path from "path";
import {
  workspace,
  ExtensionContext,
  WorkspaceConfiguration,
  window,
  commands,
  ConfigurationChangeEvent,
} from "vscode";

import {
  Executable,
  LanguageClient,
  LanguageClientOptions,
  ServerOptions,
  TransportKind,
} from "vscode-languageclient/node";

let client: LanguageClient;

export async function activate(context: ExtensionContext) {
  let client = await validateConfigAndLaunch(context);
  context.subscriptions.push(
    workspace.onDidChangeConfiguration(async (e) => {
      if (!client) {
        return;
      }
      if (e.affectsConfiguration("jastaddBridge.overrideJavaHome")) {
        await client
          .stop()
          .catch(() => console.error("Failed to stop extension!"));
        client?.diagnostics?.clear();
        client = await validateConfigAndLaunch(context);
      }
    })
  );
}

export async function validateConfigAndLaunch(
  context: ExtensionContext
): Promise<LanguageClient | undefined> {
  // load settings
  const settings = workspace.getConfiguration("jastaddBridge");
  try {
    const javaPath = await fetchJavaInstallations(settings);
    console.log(`Using java installation at: ${path}`);

    try {
      // Check if a jastadd compiler is specified in settings
      await access(
        settings.get("compiler.path"),
        constants.F_OK | constants.R_OK | constants.X_OK
      );
      return launchServer(javaPath, settings, context);
    } catch (err) {
      console.error(`Error reading file: ${err}`);
      window
        .showErrorMessage(
          `No JastAdd compiler is defined. 
        Either there isn't a path defined or it is invalid. 
        See log for details.`,
          "Configure"
        )
        .then((_selection) =>
          commands.executeCommand(
            "workbench.action.openSettings",
            "jastaddBridge.compiler.path"
          )
        );
    }
  } catch (err) {
    console.error(err);
    window
      .showErrorMessage(
        `No Java installation found (JDK 17 or higher required).
      You might need to setup the $JAVA_HOME environment variable.
      Alternatively, specify a path in the extension settings.
      If you have already provided it, it may be incorrect.`,
        "Change path to Java"
      )
      .then((_selection) => {
        commands.executeCommand(
          "workbench.action.openSettings",
          "jastaddBridge.overrideJavaHome"
        );
      });
  }
}

function launchServer(
  javaPath: string,
  settings: WorkspaceConfiguration,
  context: ExtensionContext
): LanguageClient {
  const server = context.asAbsolutePath(path.join("server", "server.jar"));

  const port = 15990; // TODO determine port dynamically

  const commonOptions: Executable = {
    command: javaPath,
    args: [
      "-Djava.security.manager=allow", // TODO remove this
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

  console.log(`Log level: ${settings.get("trace.server")}`);
  // Options to control the language client
  const clientOptions: LanguageClientOptions = {
    // Register the server for plain text documents
    documentSelector: [{ scheme: "file", language: "jastadd" }], // TODO figure out extension
    synchronize: {
      // Notify the server about file changes to '.clientrc files contained in the workspace
      // fileEvents: workspace.createFileSystemWatcher("**/.clientrc"),
      configurationSection: "jastaddBridge",
    },
  };

  console.debug(`Starting server with arguments: ${serverOptions.run}`);

  // Create the language client and start the client.
  client = new LanguageClient(
    "jastaddBridge", // has to match the package name in the settings
    "JastAdd Bridge",
    serverOptions,
    clientOptions
  );

  // Start the client. This will also launch the server
  client.start();
  return client;
}

function fetchJavaInstallations(
  settings: WorkspaceConfiguration
): Promise<string> {
  const userProvidedJava: string = settings.get("overrideJavaHome");

  if (userProvidedJava.length > 0) {
    const javaBinary = userProvidedJava + "/bin/java";
    return new Promise((resolve, reject) => {
      exec(
        `"${javaBinary}" --version`, // TODO this is probably very fragile
        (error: ExecException, _stdout: string, _stderr: string) => {
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
            resolve(installations[0].executables.java);
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
