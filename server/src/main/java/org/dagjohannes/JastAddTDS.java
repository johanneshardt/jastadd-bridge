package org.dagjohannes;

import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.tinylog.Logger;

import codeprober.AstInfo;
import codeprober.ast.AstNode;
import codeprober.locator.ApplyLocator;
import codeprober.locator.NodesAtPosition;
import codeprober.metaprogramming.AstNodeApiStyle;
import codeprober.metaprogramming.TypeIdentificationStyle;
import codeprober.protocol.PositionRecoveryStrategy;
import codeprober.util.ASTProvider;

public class JastAddTDS implements TextDocumentService {
	private String compilerPath;

	public JastAddTDS(String compilerPath) {
		this.compilerPath = compilerPath;
	}

	private String astWSpan(String prefix, AstInfo info, AstNode currentNode, StringBuilder sb) {
		sb.append(prefix + currentNode.underlyingAstNode + " @ " + currentNode.getRecoveredSpan(info) + "\n");
		for (var child : currentNode.getChildren(info)) astWSpan(prefix + "  ", info, child, sb);
		return sb.toString();
	}

	@Override
	public CompletableFuture<Hover> hover(HoverParams params) {
		Logger.info("hovering");
		Logger.info(params);
		return CompletableFutures.computeAsync(c -> {
			c.checkCanceled();
			var uri = params.getTextDocument().getUri().substring(7); // remove "file://"
			var rootNode = new AstNode(ASTProvider.parseAst(compilerPath, new String[] { uri }).rootNode);
			var info = new AstInfo(
					rootNode,
					PositionRecoveryStrategy.ALTERNATE_PARENT_CHILD,
					AstNodeApiStyle.BEAVER_PACKED_BITS,
					TypeIdentificationStyle.REFLECTION);


			Logger.info("\n" + astWSpan("", info, rootNode, new StringBuilder()));
			int offset = params.getPosition().getLine() << 12 + params.getPosition().getCharacter();
			Logger.info(offset);
			var nodes = NodesAtPosition.get(info, rootNode, offset);
			StringBuilder sb = new StringBuilder();
			for (var node : nodes)
				sb.append(node.toJSON().toString() + "\n");
			var content = new MarkupContent(MarkupKind.PLAINTEXT, sb.toString());
			var hover = new Hover(content);
			return hover;
		});
	}

	@Override
	public void didOpen(DidOpenTextDocumentParams params) {
		Logger.info("opened");
	}

	@Override
	public void didChange(DidChangeTextDocumentParams params) {
		Logger.info("changed");
	}

	@Override
	public void didClose(DidCloseTextDocumentParams params) {
		Logger.info("closed");
	}

	@Override
	public void didSave(DidSaveTextDocumentParams params) {
		Logger.info("saved");
	}

}
