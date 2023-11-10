# Packaging & Publishing

Produce package file with `vsce package`, it can be published to the store with `vsce publish`. Files that should be ignored can be specified in `.vscodeignore`.
See <https://code.visualstudio.com/api/working-with-extensions/publishing-extension>. There are some prerequisites, such as setting up an organization that owns the package in Azure Devops.

Before publishing, we should test whether the `vscode:prepublish` script works!
