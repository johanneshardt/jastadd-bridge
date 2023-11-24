package org.dagjohannes;

import codeprober.AstInfo;
import codeprober.ast.AstNode;
import codeprober.metaprogramming.AstNodeApiStyle;
import codeprober.metaprogramming.TypeIdentificationStyle;
import codeprober.protocol.PositionRecoveryStrategy;
import codeprober.util.ASTProvider;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.dagjohannes.util.NodesAtPosition;
import org.dagjohannes.util.Properties;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class TextDocumentAndWorkspaceImpl implements TextDocumentService, WorkspaceService {
    private static Configuration config;
    // maybe cache like the last 3 edits? Since you often undo/redo and there is a version number
    @NonNull
    private Optional<Document> cachedDoc = Optional.empty();

    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        Logger.debug("Hovering at {}", params.getPosition());
        // check if we have a cached document, load it otherwise
        // this is ONLY empty() if parsing the ast throws NullPointerException
        cachedDoc = cachedDoc
                .filter(d -> d.location.toString()
                        .equals(params.getTextDocument().getUri()))
                .or(() -> Document.loadFile(params.getTextDocument().getUri()));
        var content = cachedDoc.flatMap(d -> {
            var nodes = NodesAtPosition.get(d.info, d.rootNode, params.getPosition(), d.location);
            return nodes.stream().findFirst().flatMap(Properties::hover); // try to invoke the hover attribute
        }).orElse("*Hover not available.* You might need to define an attribute ```syn String ASTNode.hover()``` in your compiler.");
        var hover = new Hover(new MarkupContent(MarkupKind.MARKDOWN, content));

        return CompletableFuture.completedFuture(hover);
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        cachedDoc = Document.loadFile(params.getTextDocument().getUri());
        Logger.info("opened");
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        cachedDoc
                .filter(d -> d.isSame(params.getTextDocument().getUri()))
                .ifPresentOrElse(Document::refresh, () -> Document.loadFile(params.getTextDocument().getUri()));
        cachedDoc = cachedDoc.flatMap(Document::refresh); // TODO support multiple files
        Logger.info("changed");
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        cachedDoc = Optional.empty();
        Logger.info("closed {}");
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        Logger.info("saved");
    }

    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams params) {
        var settings = ((JsonObject) params.getSettings()).getAsJsonObject("jastaddBridge");
        var compiler = settings.getAsJsonObject("compiler");
        var compilerPath = compiler.get("path").getAsString();
        var compilerArgs = compiler.getAsJsonArray("arguments");
        var cacheStrategy = settings.get("cacheStrategy").getAsString();
        boolean purgeCache = switch (cacheStrategy) {
            case "partial" -> false;
            case "purge" -> true;
            default -> {
                Logger.error("Invalid configuration option '{}' for setting 'Cache Strategy'", cacheStrategy);
                yield false;
            }
        };
        Logger.info("Received configuration: compiler path={}, compiler args={}. cache strategy={}", compilerPath, compilerArgs, cacheStrategy);
        TextDocumentAndWorkspaceImpl.config = new Configuration(compilerPath, compilerArgs.asList().stream().map(JsonElement::getAsString).toList(), purgeCache);
    }

    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
        Logger.error("Not implemented yet!");
    }

    private record Document(URI location, AstNode rootNode, AstInfo info) {
        private static Optional<Document> parse(URI location) {
            try {
                var path = new File(location).getAbsoluteFile().toString();
                if (config.purgeCache) ASTProvider.purgeCache();
                var rootNode = new AstNode(ASTProvider.parseAst(config.compilerPath(), new String[]{path}).rootNode); // TODO pass compiler args?
                var info = new AstInfo(rootNode, PositionRecoveryStrategy.ALTERNATE_PARENT_CHILD, AstNodeApiStyle.BEAVER_PACKED_BITS, TypeIdentificationStyle.REFLECTION);
                return Optional.of(new Document(location, rootNode, info));
            } catch (NullPointerException e) {
                Logger.error(e);
                return Optional.empty();
            }
        }

        public static Optional<Document> loadFile(String location) {
            Logger.debug("Loading file: ", location);
            var uri = resolveURI(location);
            return Document.parse(uri);
        }

        public boolean isSame(String otherURI) {
            try {
                return Files.isSameFile(new File(location).getAbsoluteFile().toPath(), Paths.get(resolveURI(otherURI)));
            } catch (IOException e) {
                Logger.error(e);
                return false; // TODO could be better
            }
        }

        private static URI resolveURI(String documentURI) {
            try {
                return new URI(documentURI);
            } catch (URISyntaxException e) {
                throw new RuntimeException("Couldn't resolve path with URI: " + documentURI);
            }
        }

        public Optional<Document> refresh() {
            return Document.parse(this.location);
        }
    }

    private record Configuration(String compilerPath, List<String> compilerArgs, boolean purgeCache) {
    }
}
