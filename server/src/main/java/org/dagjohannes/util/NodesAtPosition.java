/** BSD 3-Clause License

Copyright (c) 2022, Anton Risberg Alaküla
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */


package org.dagjohannes.util;

import codeprober.AstInfo;
import codeprober.ast.AstNode;
import codeprober.locator.Span;
import codeprober.metaprogramming.InvokeProblem;
import codeprober.metaprogramming.Reflect;
import org.eclipse.lsp4j.Position;
import org.tinylog.Logger;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NodesAtPosition {

    public static List<AstNode> get(AstInfo info, AstNode astNode, Position p, Path document) {
        var pos = ((p.getLine() + 1) << 12) + p.getCharacter() + 1;
        List<AstNode> found = new ArrayList<>();
        try {
            getTo(found, info, astNode, pos, document);

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

        // TODO explain this better
        // if ASTNode.lsp_document_path() is implemented
        // - but is wrong (such as always returning "abc") -
        // then hover wont work due to this check
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