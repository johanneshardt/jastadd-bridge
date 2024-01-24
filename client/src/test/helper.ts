/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */

import * as vscode from "vscode";
import * as path from "path";

export let doc: vscode.TextDocument;
export let editor: vscode.TextEditor;
export let documentEol: string;
export let platformEol: string;

export const EXTENSION_NAME = "jastadd-bridge";
/**
 * Activates the vscode.lsp-sample extension
 */
export async function activate(docUri: vscode.Uri) {
  const ext = vscode.extensions.getExtension(
    "temp-publisher-change-this.jastadd-bridge"
  )!;
  const config = vscode.workspace.getConfiguration("jastaddBridge");
  const calcRAGCompiler = path.resolve(
    __dirname,
    "../../../examples/CalcRAG/compiler.jar"
  );
  await config.update(
    "compiler.path",
    calcRAGCompiler,
    vscode.ConfigurationTarget.Workspace
  );

  await ext.activate();
  try {
    doc = await vscode.workspace.openTextDocument(docUri);
    editor = await vscode.window.showTextDocument(doc);
    await sleep(2000); // Wait for server activation
  } catch (e) {
    console.error(e);
  }
}

async function sleep(ms: number) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

export const getDocPath = (p: string) => {
  return path.resolve(__dirname, "../../testFiles", p);
};
export const getDocUri = (p: string) => {
  return vscode.Uri.file(getDocPath(p));
};

export async function setTestContent(content: string): Promise<boolean> {
  const all = new vscode.Range(
    doc.positionAt(0),
    doc.positionAt(doc.getText().length)
  );
  return editor.edit((eb) => eb.replace(all, content));
}
