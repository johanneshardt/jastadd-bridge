package org.dagjohannes.util;

import codeprober.ast.AstNode;
import org.tinylog.Logger;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Optional;

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
        return invoke0(node, String.class, prefix + "hover");
    }


    /**
     * The attribute {@code java.nio.Path ASTNode.lsp_document_path()} is implemented when a compiler
     * should support multi-file builds.
     *
     * @param node - the AstNode to look up the path of
     * @return - An empty {@link Optional} represents that the compiler doesn't support multiple compilation units.
     */
    public static Optional<Path> documentPath(AstNode node) {
        return invoke0(node, Path.class, prefix + "document_path");
    }

    private static <T> Optional<T> invoke0(AstNode node, Class<T> type, String methodName) {
        try {
            return Optional.of(type.cast(node.underlyingAstNode.getClass().getMethod(methodName).invoke(node.underlyingAstNode)));

        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            Logger.error("No property '" + methodName + "' found!");
            return Optional.empty();
        }
    }
}
