# Implementation notes

## Packaging & publishing

Produce package file with `vsce package`, it can be published to the store with `vsce publish`. Files that should be ignored can be specified in `.vscodeignore`.
See <https://code.visualstudio.com/api/working-with-extensions/publishing-extension>. There are some prerequisites, such as setting up an organization that owns the package in Azure Devops. The extension should be bundled to increase performance.

Before publishing, we should test whether the `vscode:prepublish` script works!

## File associations

- maybe popup when no associations are registered, that links to the correct place where you add them

## Logging

Introduce setting that controls level of logging and send info to server. - DONE

## Settings

- Get initial settings in server with workspace/configuration request - DONE
- Add a handler for workspace/didChangeConfiguration in server - DONE
- Refresh extension after changing java home - DONE

## Caching

- Diagnostics are not cleared correctly in some cases
- Can't rename jastadd files, they seem to be locked by the extension

## Restarts

Do we need to restart the server ever?
If we do, it's possible like this: <https://groups.google.com/g/golang-codereviews/c/OT-W5ukXbPM>
