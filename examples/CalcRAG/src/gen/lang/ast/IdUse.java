/* This file was generated with JastAdd2 (http://jastadd.org) version 2.3.6 */
package lang.ast;
import jastaddBridge.interop.*;
import java.io.PrintStream;
import java.util.Set;
import java.util.TreeSet;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
/**
 * @ast node
 * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\calc.ast:9
 * @astdecl IdUse : Expr ::= <ID:String>;
 * @production IdUse : {@link Expr} ::= <span class="component">&lt;ID:{@link String}&gt;</span>;

 */
public class IdUse extends Expr implements Cloneable {
  /**
   * @aspect PrettyPrint
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\PrettyPrint.jrag:31
   */
  public void prettyPrint(PrintStream out, String ind) {
		out.print(getID());
	}
  /**
   * @declaredat ASTNode:1
   */
  public IdUse() {
    super();
  }
  /**
   * Initializes the child array to the correct size.
   * Initializes List and Opt nta children.
   * @apilevel internal
   * @ast method
   * @declaredat ASTNode:10
   */
  public void init$Children() {
  }
  /**
   * @declaredat ASTNode:12
   */
  @ASTNodeAnnotation.Constructor(
    name = {"ID"},
    type = {"String"},
    kind = {"Token"}
  )
  public IdUse(String p0) {
    setID(p0);
  }
  /**
   * @declaredat ASTNode:20
   */
  public IdUse(beaver.Symbol p0) {
    setID(p0);
  }
  /** @apilevel low-level 
   * @declaredat ASTNode:24
   */
  protected int numChildren() {
    return 0;
  }
  /** @apilevel internal 
   * @declaredat ASTNode:28
   */
  public void flushAttrCache() {
    super.flushAttrCache();
    lsp_definition_reset();
    decl_reset();
    isCircular_reset();
    lookup_String_reset();
    inExprOf_IdDecl_reset();
  }
  /** @apilevel internal 
   * @declaredat ASTNode:37
   */
  public void flushCollectionCache() {
    super.flushCollectionCache();

  }
  /** @apilevel internal 
   * @declaredat ASTNode:42
   */
  public IdUse clone() throws CloneNotSupportedException {
    IdUse node = (IdUse) super.clone();
    return node;
  }
  /** @apilevel internal 
   * @declaredat ASTNode:47
   */
  public IdUse copy() {
    try {
      IdUse node = (IdUse) clone();
      node.parent = null;
      if (children != null) {
        node.children = (ASTNode[]) children.clone();
      }
      return node;
    } catch (CloneNotSupportedException e) {
      throw new Error("Error: clone not supported for " + getClass().getName());
    }
  }
  /**
   * Create a deep copy of the AST subtree at this node.
   * The copy is dangling, i.e. has no parent.
   * @return dangling copy of the subtree at this node
   * @apilevel low-level
   * @deprecated Please use treeCopy or treeCopyNoTransform instead
   * @declaredat ASTNode:66
   */
  @Deprecated
  public IdUse fullCopy() {
    return treeCopyNoTransform();
  }
  /**
   * Create a deep copy of the AST subtree at this node.
   * The copy is dangling, i.e. has no parent.
   * @return dangling copy of the subtree at this node
   * @apilevel low-level
   * @declaredat ASTNode:76
   */
  public IdUse treeCopyNoTransform() {
    IdUse tree = (IdUse) copy();
    if (children != null) {
      for (int i = 0; i < children.length; ++i) {
        ASTNode child = (ASTNode) children[i];
        if (child != null) {
          child = child.treeCopyNoTransform();
          tree.setChild(child, i);
        }
      }
    }
    return tree;
  }
  /**
   * Create a deep copy of the AST subtree at this node.
   * The subtree of this node is traversed to trigger rewrites before copy.
   * The copy is dangling, i.e. has no parent.
   * @return dangling copy of the subtree at this node
   * @apilevel low-level
   * @declaredat ASTNode:96
   */
  public IdUse treeCopy() {
    IdUse tree = (IdUse) copy();
    if (children != null) {
      for (int i = 0; i < children.length; ++i) {
        ASTNode child = (ASTNode) getChild(i);
        if (child != null) {
          child = child.treeCopy();
          tree.setChild(child, i);
        }
      }
    }
    return tree;
  }
  /**
   * Replaces the lexeme ID.
   * @param value The new value for the lexeme ID.
   * @apilevel high-level
   */
  public IdUse setID(String value) {
    tokenString_ID = value;
    return this;
  }
  /** @apilevel internal 
   */
  protected String tokenString_ID;
  /**
   */
  public int IDstart;
  /**
   */
  public int IDend;
  /**
   * JastAdd-internal setter for lexeme ID using the Beaver parser.
   * @param symbol Symbol containing the new value for the lexeme ID
   * @apilevel internal
   */
  public IdUse setID(beaver.Symbol symbol) {
    if (symbol.value != null && !(symbol.value instanceof String))
    throw new UnsupportedOperationException("setID is only valid for String lexemes");
    tokenString_ID = (String)symbol.value;
    IDstart = symbol.getStart();
    IDend = symbol.getEnd();
    return this;
  }
  /**
   * Retrieves the value for the lexeme ID.
   * @return The value for the lexeme ID.
   * @apilevel high-level
   */
  @ASTNodeAnnotation.Token(name="ID")
  public String getID() {
    return tokenString_ID != null ? tokenString_ID : "";
  }
/** @apilevel internal */
protected boolean lsp_definition_visited = false;
  /** @apilevel internal */
  private void lsp_definition_reset() {
    lsp_definition_computed = false;
    
    lsp_definition_value = null;
    lsp_definition_visited = false;
  }
  /** @apilevel internal */
  protected boolean lsp_definition_computed = false;

  /** @apilevel internal */
  protected IdDecl lsp_definition_value;

  /**
   * @attribute syn
   * @aspect LSP
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\LSP.jrag:7
   */
  @ASTNodeAnnotation.Attribute(kind=ASTNodeAnnotation.Kind.SYN)
  @ASTNodeAnnotation.Source(aspect="LSP", declaredAt="C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\LSP.jrag:7")
  public IdDecl lsp_definition() {
    ASTState state = state();
    if (lsp_definition_computed) {
      return lsp_definition_value;
    }
    if (lsp_definition_visited) {
      throw new RuntimeException("Circular definition of attribute IdUse.lsp_definition().");
    }
    lsp_definition_visited = true;
    state().enterLazyAttribute();
    state().trace().computeBegin("LSP", this, "IdUse.lsp_definition()", "", "");
    lsp_definition_value = decl();
    state().trace().computeEnd("LSP", this, "IdUse.lsp_definition()", "", lsp_definition_value);
    lsp_definition_computed = true;
    state().leaveLazyAttribute();
    lsp_definition_visited = false;
    return lsp_definition_value;
  }
/** @apilevel internal */
protected boolean decl_visited = false;
  /** @apilevel internal */
  private void decl_reset() {
    decl_computed = false;
    
    decl_value = null;
    decl_visited = false;
  }
  /** @apilevel internal */
  protected boolean decl_computed = false;

  /** @apilevel internal */
  protected IdDecl decl_value;

  /**
   * @attribute syn
   * @aspect NameAnalysis
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\NameAnalysis.jrag:2
   */
  @ASTNodeAnnotation.Attribute(kind=ASTNodeAnnotation.Kind.SYN)
  @ASTNodeAnnotation.Source(aspect="NameAnalysis", declaredAt="C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\NameAnalysis.jrag:2")
  public IdDecl decl() {
    ASTState state = state();
    if (decl_computed) {
      return decl_value;
    }
    if (decl_visited) {
      throw new RuntimeException("Circular definition of attribute IdUse.decl().");
    }
    decl_visited = true;
    state().enterLazyAttribute();
    state().trace().computeBegin("NameAnalysis", this, "IdUse.decl()", "", "");
    decl_value = lookup(getID());
    state().trace().computeEnd("NameAnalysis", this, "IdUse.decl()", "", decl_value);
    decl_computed = true;
    state().leaveLazyAttribute();
    decl_visited = false;
    return decl_value;
  }
/** @apilevel internal */
protected boolean isCircular_visited = false;
  /** @apilevel internal */
  private void isCircular_reset() {
    isCircular_computed = false;
    isCircular_visited = false;
  }
  /** @apilevel internal */
  protected boolean isCircular_computed = false;

  /** @apilevel internal */
  protected boolean isCircular_value;

  /**
   * @attribute syn
   * @aspect CircularDefinitions
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\NameAnalysis.jrag:32
   */
  @ASTNodeAnnotation.Attribute(kind=ASTNodeAnnotation.Kind.SYN)
  @ASTNodeAnnotation.Source(aspect="CircularDefinitions", declaredAt="C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\NameAnalysis.jrag:32")
  public boolean isCircular() {
    ASTState state = state();
    if (isCircular_computed) {
      return isCircular_value;
    }
    if (isCircular_visited) {
      throw new RuntimeException("Circular definition of attribute IdUse.isCircular().");
    }
    isCircular_visited = true;
    state().enterLazyAttribute();
    state().trace().computeBegin("CircularDefinitions", this, "IdUse.isCircular()", "", "");
    isCircular_value = inExprOf(decl());
    state().trace().computeEnd("CircularDefinitions", this, "IdUse.isCircular()", "", isCircular_value);
    isCircular_computed = true;
    state().leaveLazyAttribute();
    isCircular_visited = false;
    return isCircular_value;
  }
  /**
   * @attribute inh
   * @aspect NameAnalysis
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\NameAnalysis.jrag:3
   */
  @ASTNodeAnnotation.Attribute(kind=ASTNodeAnnotation.Kind.INH)
  @ASTNodeAnnotation.Source(aspect="NameAnalysis", declaredAt="C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\NameAnalysis.jrag:3")
  public IdDecl lookup(String name) {
    Object _parameters = name;
    if (lookup_String_visited == null) lookup_String_visited = new java.util.HashSet(4);
    if (lookup_String_values == null) lookup_String_values = new java.util.HashMap(4);
    ASTState state = state();
    if (lookup_String_values.containsKey(_parameters)) {
      return (IdDecl) lookup_String_values.get(_parameters);
    }
    if (lookup_String_visited.contains(_parameters)) {
      throw new RuntimeException("Circular definition of attribute IdUse.lookup(String).");
    }
    lookup_String_visited.add(_parameters);
    state().enterLazyAttribute();
    state().trace().computeBegin("NameAnalysis", this, "IdUse.lookup(String)", _parameters, "");
    IdDecl lookup_String_value = getParent().Define_lookup(this, null, name);
    state().trace().computeEnd("NameAnalysis", this, "IdUse.lookup(String)", _parameters, lookup_String_value);
    lookup_String_values.put(_parameters, lookup_String_value);
    state().leaveLazyAttribute();
    lookup_String_visited.remove(_parameters);
    return lookup_String_value;
  }
/** @apilevel internal */
protected java.util.Set lookup_String_visited;
  /** @apilevel internal */
  private void lookup_String_reset() {
    lookup_String_values = null;
    lookup_String_visited = null;
  }
  /** @apilevel internal */
  protected java.util.Map lookup_String_values;

  /**
   * @attribute inh
   * @aspect CircularDefinitions
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\NameAnalysis.jrag:33
   */
  @ASTNodeAnnotation.Attribute(kind=ASTNodeAnnotation.Kind.INH)
  @ASTNodeAnnotation.Source(aspect="CircularDefinitions", declaredAt="C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\NameAnalysis.jrag:33")
  public boolean inExprOf(IdDecl decl) {
    Object _parameters = decl;
    if (inExprOf_IdDecl_visited == null) inExprOf_IdDecl_visited = new java.util.HashSet(4);
    if (inExprOf_IdDecl_values == null) inExprOf_IdDecl_values = new java.util.HashMap(4);
    ASTState state = state();
    if (inExprOf_IdDecl_values.containsKey(_parameters)) {
      return (Boolean) inExprOf_IdDecl_values.get(_parameters);
    }
    if (inExprOf_IdDecl_visited.contains(_parameters)) {
      throw new RuntimeException("Circular definition of attribute IdUse.inExprOf(IdDecl).");
    }
    inExprOf_IdDecl_visited.add(_parameters);
    state().enterLazyAttribute();
    state().trace().computeBegin("CircularDefinitions", this, "IdUse.inExprOf(IdDecl)", _parameters, "");
    boolean inExprOf_IdDecl_value = getParent().Define_inExprOf(this, null, decl);
    state().trace().computeEnd("CircularDefinitions", this, "IdUse.inExprOf(IdDecl)", _parameters, inExprOf_IdDecl_value);
    inExprOf_IdDecl_values.put(_parameters, inExprOf_IdDecl_value);
    state().leaveLazyAttribute();
    inExprOf_IdDecl_visited.remove(_parameters);
    return inExprOf_IdDecl_value;
  }
/** @apilevel internal */
protected java.util.Set inExprOf_IdDecl_visited;
  /** @apilevel internal */
  private void inExprOf_IdDecl_reset() {
    inExprOf_IdDecl_values = null;
    inExprOf_IdDecl_visited = null;
  }
  /** @apilevel internal */
  protected java.util.Map inExprOf_IdDecl_values;

  /** @apilevel internal */
  protected void collect_contributors_Program_errors(Program _root, java.util.Map<ASTNode, java.util.Set<ASTNode>> _map) {
    // @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\Errors.jrag:34
    if (decl().isUnknown()) {
      {
        Program target = (Program) (program());
        java.util.Set<ASTNode> contributors = _map.get(target);
        if (contributors == null) {
          contributors = new java.util.LinkedHashSet<ASTNode>();
          _map.put((ASTNode) target, contributors);
        }
        contributors.add(this);
      }
    }
    // @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\Errors.jrag:42
    if (isCircular()) {
      {
        Program target = (Program) (program());
        java.util.Set<ASTNode> contributors = _map.get(target);
        if (contributors == null) {
          contributors = new java.util.LinkedHashSet<ASTNode>();
          _map.put((ASTNode) target, contributors);
        }
        contributors.add(this);
      }
    }
    super.collect_contributors_Program_errors(_root, _map);
  }
  /** @apilevel internal */
  protected void collect_contributors_Program_lsp_diagnostics(Program _root, java.util.Map<ASTNode, java.util.Set<ASTNode>> _map) {
    // @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\LSP.jrag:24
    if (decl().isUnknown()) {
      {
        Program target = (Program) (program());
        java.util.Set<ASTNode> contributors = _map.get(target);
        if (contributors == null) {
          contributors = new java.util.LinkedHashSet<ASTNode>();
          _map.put((ASTNode) target, contributors);
        }
        contributors.add(this);
      }
    }
    // @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\LSP.jrag:32
    if (isCircular()) {
      {
        Program target = (Program) (program());
        java.util.Set<ASTNode> contributors = _map.get(target);
        if (contributors == null) {
          contributors = new java.util.LinkedHashSet<ASTNode>();
          _map.put((ASTNode) target, contributors);
        }
        contributors.add(this);
      }
    }
    super.collect_contributors_Program_lsp_diagnostics(_root, _map);
  }
  /** @apilevel internal */
  protected void contributeTo_Program_errors(Set<ErrorMessage> collection) {
    super.contributeTo_Program_errors(collection);
    if (decl().isUnknown()) {
      collection.add(error("symbol '" + getID() + "' is not declared"));
    }
    if (isCircular()) {
      collection.add(error("the definition of symbol '" + getID() + "' is circular"));
    }
  }
  /** @apilevel internal */
  protected void contributeTo_Program_lsp_diagnostics(Set<Diagnostic> collection) {
    super.contributeTo_Program_lsp_diagnostics(collection);
    if (decl().isUnknown()) {
      collection.add(diagnostic("symbol '" + getID() + "' is not declared", Severity.Error));
    }
    if (isCircular()) {
      collection.add(diagnostic("the definition of symbol '" + getID() + "' is circular", Severity.Warning));
    }
  }

}
