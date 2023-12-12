package org.dagjohannes.util;

import codeprober.AstInfo;
import codeprober.ast.AstNode;
import codeprober.metaprogramming.AstNodeApiStyle;
import codeprober.metaprogramming.TypeIdentificationStyle;
import codeprober.protocol.PositionRecoveryStrategy;
import org.eclipse.lsp4j.*;
import org.tinylog.Logger;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.*;

public class Properties {
    private static final String prefix = "lsp_";

    /**
     * For accessing hover information provided by the {@code String ASTNode.lsp_hover()} attribute.
     * Text can be styled through GitHub-flavored markdown.
     *
     * @param node - the AstNode for which to get hover information
     * @return - {@link Optional#empty()} represents that the attribute is not implemented for that node
     */
    public static Optional<String> hover(AstNode node) {
        return invoke0(node.underlyingAstNode, String.class, prefix + "hover");
    }

    /**
     * The attribute {@code java.nio.Path ASTNode.lsp_document_path()} is implemented when a compiler
     * should support multi-file builds.
     *
     * @param node - the AstNode to look up the path of
     * @return - An empty {@link Optional} represents that the compiler doesn't support multiple compilation units.
     */
    public static Optional<Path> documentPath(AstNode node) {
        return invoke0(node.underlyingAstNode, Path.class, prefix + "document_path");
    }

    // syn ASTNode Program.lsp_main() = lookup("main");
    // void Program.lsp_run() {
    //   eval();
    // }

    public static Optional<CodeLens> getRunLens(AstNode node) {
        var raw = invoke0(node.underlyingAstNode, Object.class, prefix + "main");
        return raw.map(o -> {
            CodeLens codeLens = new CodeLens();
            Command cmd = new Command("Run ▶", "jastaddBridge.run");
            codeLens.setCommand(cmd);
            AstNode main = new AstNode(o);
            AstInfo info = new AstInfo(
                    main,
                    PositionRecoveryStrategy.ALTERNATE_PARENT_CHILD,
                    AstNodeApiStyle.BEAVER_PACKED_BITS,
                    TypeIdentificationStyle.REFLECTION
            );
            var span = main.getRecoveredSpan(info);
            var line = new Position(span.getStartLine() - 1, span.getEndLine() - 1);
            var col = new Position(span.getStartColumn() - 1, span.getEndColumn() - 1);
            codeLens.setRange(new Range(line, col));
            return codeLens;
        });
    }

    public static void run(AstNode node) {
        try {
            node.underlyingAstNode.getClass().getMethod(prefix + "run").invoke(node.underlyingAstNode);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            Logger.error("No property '" + prefix + "run' found!");
        }
    }

    private static Diagnostic extractDiagnostic(Object diag) {
        String message = invoke0(diag, String.class, "message").get();
        int startLine = invoke0(diag, Integer.class, "startLine").get();
        int startCol = invoke0(diag, Integer.class, "startCol").get();
        int endLine = invoke0(diag, Integer.class, "endLine").get();
        int endCol = invoke0(diag, Integer.class, "endCol").get();
        int severity = invoke0(diag, Integer.class, "severity").get();

        var start = new Position(startLine, startCol);
        var end = new Position(endLine, endCol);
        var range = new Range(start, end);
        var severityEnum = switch (severity) {
            case 1 -> DiagnosticSeverity.Error;
            case 2 -> DiagnosticSeverity.Warning;
            case 3 -> DiagnosticSeverity.Information;
            case 4 -> DiagnosticSeverity.Hint;
            default -> DiagnosticSeverity.Error;
        };

        return new Diagnostic(range, message, severityEnum, "jastadd-bridge");
    }

    public static Optional<LocationLink> getDefinition(AstNode rootNode, Position pos, String uri, AstInfo info) {
        var raw = invoke0(rootNode.underlyingAstNode, Object.class, prefix + "definition");
        try {
            return raw.map(d -> {
                LocationLink loclink = new LocationLink();
                var destNode = new AstNode(d);
                Logger.info(destNode);
                Logger.info(destNode.underlyingAstNode);
                var span = destNode.getRecoveredSpan(info);
                Logger.info(span);
                Range range = new Range(
                    new Position(
                        span.getStartLine() - 1, 
                        span.getStartColumn() - 1
                    ), 
                    new Position(
                        span.getEndLine() - 1, 
                        span.getEndColumn()
                    )
                );
                loclink.setTargetRange(range);
                loclink.setTargetSelectionRange(range);
                loclink.setTargetUri(uri); // TODO maybe multifile support
                return loclink;
            });
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }

    public static Optional<List<CodeAction>> getCodeActions(AstNode rootNode, VersionedTextDocumentIdentifier docId) {
        var raw = invoke0(rootNode.underlyingAstNode, Set.class, prefix + "diagnostics");
        try {
            return raw.map(s -> {
                // Logger.info(s);
                List<CodeAction> list = new ArrayList<>();
                for (Object diag : s) {
                    var title = invoke0(diag, String.class, "codeActionTitle");
                    CodeAction action = new CodeAction(title.orElse(""));
                    var diagnostic = extractDiagnostic(diag);
                    // Logger.info("Diagnostic: " + diagnostic);
                    action.setDiagnostics(List.of(diagnostic));
                    var potentialFixes = invoke0(diag, Set.class, "fixes");
                    // Logger.info("Potential fixes: " + potentialFixes);
                    // Logger.info(Optional.<Set>empty());
                    if (potentialFixes.isEmpty()) return List.of();
                    var edits = potentialFixes.get();

                    // Only add fixes that actually do something
                    if (!edits.isEmpty()) {
                        list.add(action);
                    }

                    List<TextEdit> textEdits = new ArrayList<>();
                    for (Object edit : edits) {
                        int startLine = invoke0(edit, Integer.class, "startLine").get();
                        int startCol = invoke0(edit, Integer.class, "startCol").get();
                        int endLine = invoke0(edit, Integer.class, "endLine").get();
                        int endCol = invoke0(edit, Integer.class, "endCol").get();
                        String replacementText = invoke0(edit, String.class, "replacement").get();
                        var start = new Position(startLine, startCol);
                        var end = new Position(endLine, endCol);
                        var range = new Range(start, end);
                        textEdits.add(new TextEdit(range, replacementText));
                        // Logger.info("textEdits " + textEdits);
                    }
                    action.setKind(CodeActionKind.QuickFix);
                    action.setIsPreferred(true);
                    Map<String, List<TextEdit>> changes = new HashMap<>();
                    for (TextEdit edit : textEdits) changes.put(docId.getUri(), List.of(edit));
                    WorkspaceEdit workspaceedit = new WorkspaceEdit(changes);
                    action.setEdit(workspaceedit);
                }
                return list;
            });
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }

    public static Optional<List<Diagnostic>> getDiagnostics(AstNode rootNode) {
        var raw = invoke0(rootNode.underlyingAstNode, Set.class, prefix + "diagnostics");
        try {
            return raw.map(s -> {
                List<Diagnostic> list = new ArrayList<>();
                for (Object diag : s) list.add(extractDiagnostic(diag));
                return list;
            });
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }


    private static <T> Optional<T> invoke0(Object obj, Class<T> type, String methodName) {
        try {
            return Optional.of(type.cast(obj.getClass().getMethod(methodName).invoke(obj)));

        } catch (IllegalAccessException | InvocationTargetException e) {
            // TODO handle wrong cast type gracefully
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            Logger.error(e, "No property '" + methodName + "' found on '" + obj + "'");
            return Optional.empty();
        }
    }

    private static <T> Optional<T> invokeN(Object obj, Class<T> type, String methodName, Class<?>[] argTypes, Object[] argValues) {
        try {
            return Optional.of(type.cast(obj.getClass().getMethod(methodName, argTypes).invoke(obj, argValues)));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            Logger.error("No property '" + methodName + "' found!");
            return Optional.empty();
        }
    }
}
