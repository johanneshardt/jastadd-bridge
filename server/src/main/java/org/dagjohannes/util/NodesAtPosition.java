package org.dagjohannes.util;

import codeprober.AstInfo;
import codeprober.ast.AstNode;
import codeprober.locator.Span;
import codeprober.metaprogramming.InvokeProblem;
import codeprober.metaprogramming.Reflect;
import org.eclipse.lsp4j.Position;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NodesAtPosition {

    public static List<AstNode> get(AstInfo info, AstNode astNode, Position p, URI document) {
        var pos = ((p.getLine() + 1) << 12) + p.getCharacter() + 1;
        List<AstNode> found = new ArrayList<>();
        try {
            getTo(found, info, astNode, pos, new File(document).getAbsoluteFile().toPath());

        } catch (IOException e) {
            Logger.error(e, "Couldn't load path: ", document); // TODO good enough error handling?
        }
        Collections.reverse(found); // Narrowest/smallest node first in the list
        return found;
    }

    private static void getTo(List<AstNode> out, AstInfo info, AstNode astNode, int pos, Path documentPath) throws IOException {
        final Span nodePos;
        try {
            nodePos = astNode.getRecoveredSpan(info);
        } catch (InvokeProblem e) {
            Logger.error(e);
            return;
        }

        var p = Properties.documentPath(astNode);
        if (p.isPresent() && Files.isSameFile(p.get(), documentPath)) { // TODO see if this handles common cases?
            return;
        }

        boolean includeNode = out.isEmpty() || (nodePos.start == 0 || nodePos.end == 0 || (nodePos.start <= pos && (nodePos.end + 1) >= pos));
        if (includeNode) {
            // Default false for List/Opt, they are very rarely useful
            boolean show = !astNode.isList() && !astNode.isOpt();
            final Boolean override = astNode.showInNodeList(info);
            if (override != null ? override : show) {
                out.add(astNode);
            }
        }
        if (astNode == info.ast) {
            // Root node, maybe skip ahead
            Object next = null;
            try {
                next = Reflect.invoke0(astNode.underlyingAstNode, "pastaVisibleNextAfterRoot"); // do we need this? TODO
            } catch (InvokeProblem e) {
                // OK, this is an optional attribute
            }
            if (next != null) {
                getTo(out, info, new AstNode(next), pos, documentPath);
                return;
            }
        }
        if (includeNode) {
            for (AstNode child : astNode.getChildren(info)) {
                getTo(out, info, child, pos, documentPath);
            }
        }
    }
}