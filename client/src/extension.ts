/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */

import { Socket } from 'node:net';
import * as path from 'path';
import { workspace, ExtensionContext } from 'vscode';

import {
	LanguageClient,
	LanguageClientOptions,
	ServerOptions,
	TransportKind,
} from 'vscode-languageclient/node';

let client: LanguageClient;

export function activate(context: ExtensionContext) {
	// Path to generated JAR (TODO)
	const serverModule = context.asAbsolutePath(
		path.join('server', 'build', 'libs', 'server-1.0-SNAPSHOT.jar')
	);

	const port = 15990;

	const serverOptions: ServerOptions = {
		run: { 
			command: "java", 
			args: ['-jar', serverModule],
			transport: {
				kind: TransportKind.socket,
				port: port
			} 
		},
		debug: {
			command: "java",
			args: ['-jar', serverModule],
			transport: {
				kind: TransportKind.socket,
				port: port
			} 
		}
	};

	// Options to control the language client
	const clientOptions: LanguageClientOptions = {
		// Register the server for plain text documents
		documentSelector: [{ scheme: 'file', language: 'jastadd' }], // TODO figure out extension
		synchronize: {
			// Notify the server about file changes to '.clientrc files contained in the workspace
			fileEvents: workspace.createFileSystemWatcher('**/.clientrc')
		}
	};

	// function serverOptions(): Promise<StreamInfo> {
	// 	const port = 3333;
	// 	const socket = new Socket();
	// 	const client = socket.connect(port);
	// 	return Promise.resolve({
	// 		writer: client,
	// 		reader: client
	// 	});
	// }


	// Create the language client and start the client.
	client = new LanguageClient(
		'jastadd-bridge',
		'JastAdd Bridge',
		serverOptions,
		clientOptions
	);

	// Start the client. This will also launch the server
	client.start();
}

export function deactivate(): Thenable<void> | undefined {
	if (!client) {
		return undefined;
	}
	return client.stop();
}
