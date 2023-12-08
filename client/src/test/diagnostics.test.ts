/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */

import * as vscode from "vscode";
import * as assert from "assert";
import { getDocUri, activate, EXTENSION_NAME } from "./helper";

suite("Should get diagnostics", () => {
  test("Functions should be lower cased", async () => {
    await testDiagnostics(getDocUri("diagnostic_formatting.jastadd"), [
      {
        message: "names should be lower cased!",
        range: toRange(0, 4, 0, 8),
        severity: vscode.DiagnosticSeverity.Information,
        source: EXTENSION_NAME,
      },
    ]);
  });
  test("Number of arguments should match function definition", async () => {
    await testDiagnostics(getDocUri("diagnostic_args_no.jastadd"), [
      {
        message: "the number of arguments passed to 'oneArg' is 0, should be 1",
        range: toRange(2, 4, 2, 12),
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
