/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */

import * as vscode from "vscode";
import * as assert from "assert";
import { getDocUri, activate, EXTENSION_NAME } from "./helper";

suite("Should get diagnostics", () => {
  test("Variables should be declared before use", async () => {
    await testDiagnostics(getDocUri("not_declared.jastadd"), [
      {
        message: "symbol 'me' is not declared",
        range: toRange(1, 7, 1, 9),
        severity: vscode.DiagnosticSeverity.Error,
        source: EXTENSION_NAME,
      },
    ]);
  });
  test("Variables cannot be redeclared", async () => {
    await testDiagnostics(getDocUri("redeclared.jastadd"), [
      {
        message: "symbol 'pi' is already declared!",
        range: toRange(2, 1, 2, 3),
        severity: vscode.DiagnosticSeverity.Error,
        source: EXTENSION_NAME,
      },
    ]);
  });
});

function toRange(sLine: number, sChar: number, eLine: number, eChar: number) {
  const start = new vscode.Position(sLine, sChar);
  const end = new vscode.Position(eLine, eChar);
  return new vscode.Range(start, end);
}

async function testDiagnostics(
  docUri: vscode.Uri,
  expectedDiagnostics: vscode.Diagnostic[]
) {
  await activate(docUri);
  const actualDiagnostics = vscode.languages.getDiagnostics(docUri);

  assert.equal(actualDiagnostics.length, expectedDiagnostics.length);

  expectedDiagnostics.forEach((expectedDiagnostic, i) => {
    const actualDiagnostic = actualDiagnostics[i];
    assert.equal(actualDiagnostic.message, expectedDiagnostic.message);
    assert.deepEqual(actualDiagnostic.range, expectedDiagnostic.range);
    assert.equal(actualDiagnostic.severity, expectedDiagnostic.severity);
  });
}
