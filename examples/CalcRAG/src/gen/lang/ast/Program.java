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
 * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\calc.ast:1
 * @astdecl Program : ASTNode ::= Expr;
 * @production Program : {@link ASTNode} ::= <span class="component">{@link Expr}</span>;

 */
public class Program extends ASTNode<ASTNode> implements Cloneable {
  /**
   * @aspect LSP
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\LSP.jrag:10
   */
  public void lsp_run() {
    System.out.println("Hello, world!");
  }
  /**
   * @declaredat ASTNode:1
   */
  public Program() {
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
    children = new ASTNode[1];
  }
  /**
   * @declaredat ASTNode:13
   */
  @ASTNodeAnnotation.Constructor(
    name = {"Expr"},
    type = {"Expr"},
    kind = {"Child"}
  )
  public Program(Expr p0) {
    setChild(p0, 0);
  }
  /** @apilevel low-level 
   * @declaredat ASTNode:22
   */
  protected int numChildren() {
    return 1;
  }
  /** @apilevel internal 
   * @declaredat ASTNode:26
   */
  public void flushAttrCache() {
    super.flushAttrCache();
    lsp_main_reset();
    unknownDecl_reset();
  }
  /** @apilevel internal 
   * @declaredat ASTNode:32
   */
  public void flushCollectionCache() {
    super.flushCollectionCache();
    Program_errors_visited = false;
    Program_errors_computed = false;
    
    Program_errors_value = null;
    Program_lsp_diagnostics_visited = false;
    Program_lsp_diagnostics_computed = false;
    
    Program_lsp_diagnostics_value = null;
    contributorMap_Program_errors = null;
    contributorMap_Program_lsp_diagnostics = null;
  }
  /** @apilevel internal 
   * @declaredat ASTNode:46
   */
  public Program clone() throws CloneNotSupportedException {
    Program node = (Program) super.clone();
    return node;
  }
  /** @apilevel internal 
   * @declaredat ASTNode:51
   */
  public Program copy() {
    try {
      Program node = (Program) clone();
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
   * @declaredat ASTNode:70
   */
  @Deprecated
  public Program fullCopy() {
    return treeCopyNoTransform();
  }
  /**
   * Create a deep copy of the AST subtree at this node.
   * The copy is dangling, i.e. has no parent.
   * @return dangling copy of the subtree at this node
   * @apilevel low-level
   * @declaredat ASTNode:80
   */
  public Program treeCopyNoTransform() {
    Program tree = (Program) copy();
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
   * @declaredat ASTNode:100
   */
  public Program treeCopy() {
    Program tree = (Program) copy();
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
   * Replaces the Expr child.
   * @param node The new node to replace the Expr child.
   * @apilevel high-level
   */
  public Program setExpr(Expr node) {
    setChild(node, 0);
    return this;
  }
  /**
   * Retrieves the Expr child.
   * @return The current node used as the Expr child.
   * @apilevel high-level
   */
  @ASTNodeAnnotation.Child(name="Expr")
  public Expr getExpr() {
    return (Expr) getChild(0);
  }
  /**
   * Retrieves the Expr child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the Expr child.
   * @apilevel low-level
   */
  public Expr getExprNoTransform() {
    return (Expr) getChildNoTransform(0);
  }
  /**
   * @aspect <NoAspect>
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\Errors.jrag:26
   */
  /** @apilevel internal */
protected java.util.Map<ASTNode, java.util.Set<ASTNode>> contributorMap_Program_errors = null;

  /** @apilevel internal */
  protected void survey_Program_errors() {
    if (contributorMap_Program_errors == null) {
      contributorMap_Program_errors = new java.util.IdentityHashMap<ASTNode, java.util.Set<ASTNode>>();
      collect_contributors_Program_errors(this, contributorMap_Program_errors);
    }
  }

  /**
   * @aspect <NoAspect>
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\LSP.jrag:16
   */
  /** @apilevel internal */
protected java.util.Map<ASTNode, java.util.Set<ASTNode>> contributorMap_Program_lsp_diagnostics = null;

  /** @apilevel internal */
  protected void survey_Program_lsp_diagnostics() {
    if (contributorMap_Program_lsp_diagnostics == null) {
      contributorMap_Program_lsp_diagnostics = new java.util.IdentityHashMap<ASTNode, java.util.Set<ASTNode>>();
      collect_contributors_Program_lsp_diagnostics(this, contributorMap_Program_lsp_diagnostics);
    }
  }

/** @apilevel internal */
protected boolean lsp_main_visited = false;
  /** @apilevel internal */
  private void lsp_main_reset() {
    lsp_main_computed = false;
    
    lsp_main_value = null;
    lsp_main_visited = false;
  }
  /** @apilevel internal */
  protected boolean lsp_main_computed = false;

  /** @apilevel internal */
  protected ASTNode lsp_main_value;

  /**
   * @attribute syn
   * @aspect LSP
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\LSP.jrag:9
   */
  @ASTNodeAnnotation.Attribute(kind=ASTNodeAnnotation.Kind.SYN)
  @ASTNodeAnnotation.Source(aspect="LSP", declaredAt="C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\LSP.jrag:9")
  public ASTNode lsp_main() {
    ASTState state = state();
    if (lsp_main_computed) {
      return lsp_main_value;
    }
    if (lsp_main_visited) {
      throw new RuntimeException("Circular definition of attribute Program.lsp_main().");
    }
    lsp_main_visited = true;
    state().enterLazyAttribute();
    state().trace().computeBegin("LSP", this, "Program.lsp_main()", "", "");
    lsp_main_value = getChild(0);
    state().trace().computeEnd("LSP", this, "Program.lsp_main()", "", lsp_main_value);
    lsp_main_computed = true;
    state().leaveLazyAttribute();
    lsp_main_visited = false;
    return lsp_main_value;
  }
/** @apilevel internal */
protected boolean unknownDecl_visited = false;
  /** @apilevel internal */
  private void unknownDecl_reset() {
    unknownDecl_computed = false;
    
    unknownDecl_value = null;
    unknownDecl_visited = false;
  }
  /** @apilevel internal */
  protected boolean unknownDecl_computed = false;

  /** @apilevel internal */
  protected UnknownDecl unknownDecl_value;

  /**
   * @attribute syn
   * @aspect UnknownDecl
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\UnknownDecl.jrag:2
   */
  @ASTNodeAnnotation.Attribute(kind=ASTNodeAnnotation.Kind.SYN, isNTA=true)
  @ASTNodeAnnotation.Source(aspect="UnknownDecl", declaredAt="C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\UnknownDecl.jrag:2")
  public UnknownDecl unknownDecl() {
    ASTState state = state();
    if (unknownDecl_computed) {
      return unknownDecl_value;
    }
    if (unknownDecl_visited) {
      throw new RuntimeException("Circular definition of attribute Program.unknownDecl().");
    }
    unknownDecl_visited = true;
    state().enterLazyAttribute();
    state().trace().computeBegin("UnknownDecl", this, "Program.unknownDecl()", "", "");
    unknownDecl_value = new UnknownDecl("<unknown>");
    state().trace().computeEnd("UnknownDecl", this, "Program.unknownDecl()", "", unknownDecl_value);
    unknownDecl_value.setParent(this);
    unknownDecl_computed = true;
    state().leaveLazyAttribute();
    unknownDecl_visited = false;
    return unknownDecl_value;
  }
  /**
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\Errors.jrag:28
   * @apilevel internal
   */
  public Program Define_program(ASTNode _callerNode, ASTNode _childNode) {
    int childIndex = this.getIndexOfChild(_callerNode);
    return this;
  }
  /**
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\Errors.jrag:28
   * @apilevel internal
   * @return {@code true} if this node has an equation for the inherited attribute program
   */
  protected boolean canDefine_program(ASTNode _callerNode, ASTNode _childNode) {
    return true;
  }
  /**
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\NameAnalysis.jrag:10
   * @apilevel internal
   */
  public IdDecl Define_lookup(ASTNode _callerNode, ASTNode _childNode, String name) {
    int childIndex = this.getIndexOfChild(_callerNode);
    return unknownDecl();
  }
  /**
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\NameAnalysis.jrag:10
   * @apilevel internal
   * @return {@code true} if this node has an equation for the inherited attribute lookup
   */
  protected boolean canDefine_lookup(ASTNode _callerNode, ASTNode _childNode, String name) {
    return true;
  }
  /**
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\NameAnalysis.jrag:34
   * @apilevel internal
   */
  public boolean Define_inExprOf(ASTNode _callerNode, ASTNode _childNode, IdDecl decl) {
    int childIndex = this.getIndexOfChild(_callerNode);
    return false;
  }
  /**
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\NameAnalysis.jrag:34
   * @apilevel internal
   * @return {@code true} if this node has an equation for the inherited attribute inExprOf
   */
  protected boolean canDefine_inExprOf(ASTNode _callerNode, ASTNode _childNode, IdDecl decl) {
    return true;
  }
  /**
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\UnknownDecl.jrag:4
   * @apilevel internal
   */
  public UnknownDecl Define_unknownDecl(ASTNode _callerNode, ASTNode _childNode) {
    int childIndex = this.getIndexOfChild(_callerNode);
    return unknownDecl();
  }
  /**
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\UnknownDecl.jrag:4
   * @apilevel internal
   * @return {@code true} if this node has an equation for the inherited attribute unknownDecl
   */
  protected boolean canDefine_unknownDecl(ASTNode _callerNode, ASTNode _childNode) {
    return true;
  }
/** @apilevel internal */
protected boolean Program_errors_visited = false;
  /**
   * @attribute coll
   * @aspect Errors
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\Errors.jrag:26
   */
  @ASTNodeAnnotation.Attribute(kind=ASTNodeAnnotation.Kind.COLL)
  @ASTNodeAnnotation.Source(aspect="Errors", declaredAt="C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\Errors.jrag:26")
  public Set<ErrorMessage> errors() {
    ASTState state = state();
    if (Program_errors_computed) {
      return Program_errors_value;
    }
    if (Program_errors_visited) {
      throw new RuntimeException("Circular definition of attribute Program.errors().");
    }
    Program_errors_visited = true;
    state().enterLazyAttribute();
    state().trace().computeBegin("Errors", this, "Program.errors()", "", "");
    Program_errors_value = errors_compute();
    state().trace().computeEnd("Errors", this, "Program.errors()", "", Program_errors_value);
    Program_errors_computed = true;
    state().leaveLazyAttribute();
    Program_errors_visited = false;
    return Program_errors_value;
  }
  /** @apilevel internal */
  private Set<ErrorMessage> errors_compute() {
    ASTNode node = this;
    while (node != null && !(node instanceof Program)) {
      node = node.getParent();
    }
    Program root = (Program) node;
    root.survey_Program_errors();
    Set<ErrorMessage> _computedValue = new TreeSet<ErrorMessage>();
    if (root.contributorMap_Program_errors.containsKey(this)) {
      for (ASTNode contributor : (java.util.Set<ASTNode>) root.contributorMap_Program_errors.get(this)) {
        contributor.contributeTo_Program_errors(_computedValue);
      }
    }
    return _computedValue;
  }
  /** @apilevel internal */
  protected boolean Program_errors_computed = false;

  /** @apilevel internal */
  protected Set<ErrorMessage> Program_errors_value;

/** @apilevel internal */
protected boolean Program_lsp_diagnostics_visited = false;
  /**
   * @attribute coll
   * @aspect LSPDiagnostics
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\LSP.jrag:16
   */
  @ASTNodeAnnotation.Attribute(kind=ASTNodeAnnotation.Kind.COLL)
  @ASTNodeAnnotation.Source(aspect="LSPDiagnostics", declaredAt="C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\LSP.jrag:16")
  public Set<Diagnostic> lsp_diagnostics() {
    ASTState state = state();
    if (Program_lsp_diagnostics_computed) {
      return Program_lsp_diagnostics_value;
    }
    if (Program_lsp_diagnostics_visited) {
      throw new RuntimeException("Circular definition of attribute Program.lsp_diagnostics().");
    }
    Program_lsp_diagnostics_visited = true;
    state().enterLazyAttribute();
    state().trace().computeBegin("LSPDiagnostics", this, "Program.lsp_diagnostics()", "", "");
    Program_lsp_diagnostics_value = lsp_diagnostics_compute();
    state().trace().computeEnd("LSPDiagnostics", this, "Program.lsp_diagnostics()", "", Program_lsp_diagnostics_value);
    Program_lsp_diagnostics_computed = true;
    state().leaveLazyAttribute();
    Program_lsp_diagnostics_visited = false;
    return Program_lsp_diagnostics_value;
  }
  /** @apilevel internal */
  private Set<Diagnostic> lsp_diagnostics_compute() {
    ASTNode node = this;
    while (node != null && !(node instanceof Program)) {
      node = node.getParent();
    }
    Program root = (Program) node;
    root.survey_Program_lsp_diagnostics();
    Set<Diagnostic> _computedValue = new HashSet();
    if (root.contributorMap_Program_lsp_diagnostics.containsKey(this)) {
      for (ASTNode contributor : (java.util.Set<ASTNode>) root.contributorMap_Program_lsp_diagnostics.get(this)) {
        contributor.contributeTo_Program_lsp_diagnostics(_computedValue);
      }
    }
    return _computedValue;
  }
  /** @apilevel internal */
  protected boolean Program_lsp_diagnostics_computed = false;

  /** @apilevel internal */
  protected Set<Diagnostic> Program_lsp_diagnostics_value;


}
