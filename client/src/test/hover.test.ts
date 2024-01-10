/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */

import * as vscode from "vscode";
import * as assert from "assert";
import { getDocUri, activate } from "./helper";

suite("Should get hover text", () => {
  test("Variables should be declared before use", async () => {
    await testHover(
      getDocUri("hover.jastadd"),
      new vscode.Position(0, 1),
      "Let"
    );
  });
});

function toRange(sLine: number, sChar: number, eLine: number, eChar: number) {
  const start = new vscode.Position(sLine, sChar);
  const end = new vscode.Position(eLine, eChar);
  return new vscode.Range(start, end);
}

async function testHover(
  docUri: vscode.Uri,
  position: vscode.Position,
  expectedHoverText: string
) {
  await activate(docUri);

  // Executing the command `vscode.executeCompletionItemProvider` to simulate triggering completion
  const actualHoverList = (await vscode.commands.executeCommand(
    "vscode.executeHoverProvider",
    docUri,
    position
  )) as Array<vscode.Hover>;
  console.log("HEEEEJ");
  console.log(actualHoverList[0].contents);

  assert.ok(actualHoverList.length === 1);
  assert.equal(
    (actualHoverList[0].contents[0] as vscode.MarkdownString).value,
    expectedHoverText
  );
}
