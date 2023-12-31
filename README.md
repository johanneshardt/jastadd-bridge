# JastAdd Bridge

A reusable VSCode extention built on the language server protocol, allowing JastAdd tools to interface with LSP interactions.

## Extension usage

The extension launches when it detects a related file. By default it uses the `.jastadd` file associations, but you can add your own. See "file associations" in vscode preferences.

To use JastAdd Bridge with your compiler, you need to provide an absolute path to it in the settings page, which you find by searching for "jastadd". All related settings are grouped here. The compiler you provide should be a standalone jar file, with the executable permission set (if you are on linux).
![Alt text](docs/settings_page.png)

The first command line argument your compiler takes should be a path to the text file that is to be compiled. Arguments following that are not reserved, and can be set in the settings page (see JastAdd Bridge > Compiler: Arguments).

## Features

`JastAdd Bridge` provides a link between your compiler and the VS Code development environment. By implementing methods with specific names (all prefixed with `lsp_`), you can enable integrated editor features such as hover (text showing up when your mouse is above some specific object) and diagnostic reports (squiggly lines when the compiler reports an error or warning)

### Hover

A synthesized method `lsp_hover()` returning a `String` on some `ASTNode` will provide hover text for that node. The text is in Markdown, meaning it supports [links](https://assets.petco.com/petco/image/upload/f_auto,q_auto/green-tree-frog-care-sheet-hero), **bold** and *italic* and `code`.

### Diagnostics

To provide diagnostic reports, the extension first assumes there is some synthesized method `lsp_diagnostics()`. This method must return a `Set` of some user-defined `Diagnostic` (most conveniently done using a `coll` attribute). This `Diagnostic` type can be named anything, but must contain the following methods:

- `String message()` (The message to be displayed)
- `int severity()` (Diagnostic type. 1 = error, 2 = warning, 3 = info, 4 = hint)
- `int startLine()`
- `int startColumn()`
- `int endLine()`
- `int endColumn()`

The methods `startLine`, `startColumn`, `endLine` and `endColumn` are used to determine where the diagnostic should be displayed. It's important to note that there is a difference between how `JastAdd` and `LSP` (language server protocol) handles these ranges, some some off-by-one tweaking might be necessary in order to get everything working as intended.

### Quick fixes

*Quick fixes* are changes to code suggested by your compiler. In our implementation, they are closely coupled to diagnostics. A quickfix is meant to resolve a diagnostic message, may it be an error, anti-pattern or bad formatting. Supporting quick fixes means implementing an additional field `fixes` on your `Diagnostic` type.

Fields on `Diagnostic`:

- `Set<Edit> fixes()` (The edits that could resolve a problem indicated by that diagnostic)
- `String codeActionTitle()` (What the quick fix should be called)

The class here called `Edit` is meant to represent a single change to a document, that should help resolve the problem indicated by the associated diagnostic.

Fields on `Edit`:

- `int startLine()`
- `int startColumn()`
- `int endLine()`
- `int endColumn()`
- `String replacement()`
  
An `Edit` is applied to the document by replacing all text between start-end with the `replacement()` string. In the case that you simply want to insert text, you want **start** to equal **end** (`startLine == endLine && startLolumn == endColumn`). `replacement()` will then be inserted at that position, not overwriting other content.

### Go to definition

- Not implemented yet

### "Run" button

- Not implemented yet

### Syntax highlighting

- Not implemented yet

### Inlay hints

- Not implemented yet

### Code completion

- Not implemented yet

## Building

### Prerequisites

- Node.js
- VSCode version 1.75 or higher
- JDK 17 (with $JAVA_HOME set)

### Setup

1. `git clone git@bitbucket.org:edan70/jastaddlsp-dag-johannes.git`
2. `cd jastaddlsp-dag-johannes`
3. `npm ci` (clean install)

### Workflow

To run the language server press f5, or run the task "Launch Client" (located in the Run & Debug tab). Doing this will build the server as well. When the build is done a new window will open, with the extension running. Output can be viewed in the "Output" tab, when you select "JastAdd Bridge" in the dropdown.

It is helpful to install the extension, since you can then change its settings in the menu before launching it, and configure a file association. This requires the packaging tool `vsce`, which can be installed with `npm install -g @vscode/vsce` (or run with npx vsce package).

**Steps to install:**

1. `vsce package` (generates a .vsix file)
2. Install the generated .vsix file by going to the "extensions" tab, opening the kebab menu in the top right, and selecting "install from VSIX". ![Alt text](docs/extension_install.png)

While debugging the extension, it may help to enable "verbose" logging in the extension settings. This shows both the LSP payloads sent between client and server, as well as all traces printed by the server, along with stacktraces if an exception occurs.

## License

This repository is covered by the license BSD 2-clause, see the `LICENCE` file.

## Acknowledgements

- [CodeProber](https://github.com/lu-cs-sde/codeprober/) used both as library, and modified in [NodesAtPosition.java](server/src/main/java/org/dagjohannes/util/NodesAtPosition.java)
- [JastAdd](https://jastadd.cs.lth.se/web/)
- [LSP4J](https://github.com/eclipse-lsp4j/lsp4j)
- [vscode-languageclient](https://www.npmjs.com/package/vscode-languageclient)
- [tinylog](https://tinylog.org/v2/)
- [run-script-os](https://www.npmjs.com/package/run-script-os)
- [locate-java-home](https://www.npmjs.com/package/locate-java-home)
