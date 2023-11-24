package org.dagjohannes.util;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.tinylog.Logger;

import codeprober.ast.AstNode;

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


    public static Optional<List<Diagnostic>> getDiagnostics(AstNode rootNode) {
        var raw = invoke0(rootNode.underlyingAstNode, Set.class, prefix + "diagnostics");
        try {
            return raw.map(s -> {
                List<Diagnostic> list = new ArrayList<Diagnostic>();
                for (Object diag : s) {
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
                    list.add(new Diagnostic(range, message, severityEnum, "jastadd-bridge"));
                }
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
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            Logger.error(e, "No property '" + methodName + "' found!");
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
