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
 * @astdecl ASTNode;
 * @production ASTNode;

 */
public class ASTNode<T extends ASTNode> extends beaver.Symbol implements Cloneable {
  /**
   * @aspect DumpTree
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\DumpTree.jrag:9
   */
  public String TREE_VERSION() { return "23a"; }
  /**
   * @aspect DumpTree
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\DumpTree.jrag:10
   */
  private static final String DUMP_TREE_INDENT = "  ";
  /**
   * @aspect DumpTree
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\DumpTree.jrag:12
   */
  public String dumpTree() {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		dumpTree(new PrintStream(bytes));
		return bytes.toString();
	}
  /**
   * @aspect DumpTree
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\DumpTree.jrag:18
   */
  public void dumpTree(PrintStream out) {
		dumpTree(out, "");
		out.flush();
	}
  /**
   * @aspect DumpTree
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\DumpTree.jrag:23
   */
  public void dumpTree(PrintStream out, String indent) {
		out.print(indent + getClass().getSimpleName());
		out.println(getTokens());
		String childIndent = indent + DUMP_TREE_INDENT;
		for (ASTNode child : astChildren()) {
			if (child == null) {
				out.println(childIndent + "null");
			} else {
				child.dumpTree(out, childIndent);
			}
		}
	}
  /**
   * @aspect DumpTree
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\DumpTree.jrag:36
   */
  public String getTokens() {
		java.util.TreeSet<java.lang.reflect.Method> methods = new java.util.TreeSet<>(
				new java.util.Comparator<java.lang.reflect.Method>() {
					public int compare(java.lang.reflect.Method m1, java.lang.reflect.Method m2) {
						return m1.getName().compareTo(m2.getName());
					}
				});

		methods.addAll(java.util.Arrays.asList(getClass().getMethods()));

		String result = "";
		for (java.lang.reflect.Method method : methods) {
			ASTNodeAnnotation.Token token = method.getAnnotation(ASTNodeAnnotation.Token.class);
			if (token != null) {
				try {
					result += String.format(" %s=\"%s\"", token.name(), method.invoke(this));
				} catch (IllegalAccessException ignored) {
				} catch (InvocationTargetException ignored) {
				}
			}
		}
		return result;
	}
  /**
   * @aspect Errors
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\Errors.jrag:22
   */
  protected ErrorMessage error(String message) {
		return new ErrorMessage(message, getLine(getStart()));
	}
  /**
   * @aspect LSPDiagnostics
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\LSP.jrag:20
   */
  Diagnostic diagnostic(String message, Severity s) {
    return Diagnostic.of(message, s, range());
  }
  /**
   * @aspect PrettyPrint
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\PrettyPrint.jrag:4
   */
  public void prettyPrint(PrintStream out) {
		prettyPrint(out, "");
		out.println();
	}
  /**
   * @aspect PrettyPrint
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\PrettyPrint.jrag:9
   */
  public void prettyPrint(PrintStream out, String ind) {
	  for (ASTNode child : astChildren()){
		  child.prettyPrint(out, ind);
		}
	}
  /**
   * @aspect Tracing
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\Tracing.jrag:2
   */
  public void cpr_setTraceReceiver(final java.util.function.Consumer<Object[]> recv) {
    trace().setReceiver(new ASTState.Trace.Receiver() {
      @Override
      public void accept(ASTState.Trace.Event event, ASTNode node, String attribute, Object params, Object value) {
        recv.accept(new Object[] { event, node, attribute, params, value });
      }
    });
  }
  /**
   * @declaredat ASTNode:1
   */
  public ASTNode() {
    super();
    init$Children();
  }
  /**
   * Initializes the child array to the correct size.
   * Initializes List and Opt nta children.
   * @apilevel internal
   * @ast method
   * @declaredat ASTNode:11
   */
  public void init$Children() {
  }
  /**
   * Cached child index. Child indices are assumed to never change (AST should
   * not change after construction).
   * @apilevel internal
   * @declaredat ASTNode:18
   */
  private int childIndex = -1;
  /** @apilevel low-level 
   * @declaredat ASTNode:21
   */
  public int getIndexOfChild(ASTNode node) {
    if (node == null) {
      return -1;
    }
    if (node.childIndex >= 0) {
      return node.childIndex;
    }
    for (int i = 0; children != null && i < children.length; i++) {
      if (children[i] == node) {
        node.childIndex = i;
        return i;
      }
    }
    return -1;
  }
  /** @apilevel internal 
   * @declaredat ASTNode:38
   */
  public static final boolean generatedWithCacheCycle = true;
  /** @apilevel low-level 
   * @declaredat ASTNode:41
   */
  protected ASTNode parent;
  /** @apilevel low-level 
   * @declaredat ASTNode:44
   */
  protected ASTNode[] children;
  /**
   * @declaredat ASTNode:46
   */
  public final ASTState.Trace trace() {
    return state().trace();
  }
  /** @apilevel internal 
   * @declaredat ASTNode:51
   */
  private static ASTState state = new ASTState();
  /** @apilevel internal 
   * @declaredat ASTNode:54
   */
  public final ASTState state() {
    return state;
  }
  /** @apilevel internal 
   * @declaredat ASTNode:59
   */
  public final static ASTState resetState() {
    return state = new ASTState();
  }
  /**
   * @return an iterator that can be used to iterate over the children of this node.
   * The iterator does not allow removing children.
   * @declaredat ASTNode:68
   */
  public java.util.Iterator<T> astChildIterator() {
    return new java.util.Iterator<T>() {
      private int index = 0;

      @Override
      public boolean hasNext() {
        return index < getNumChild();
      }

      @Override
      public T next() {
        return hasNext() ? (T) getChild(index++) : null;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }
  /** @return an object that can be used to iterate over the children of this node 
   * @declaredat ASTNode:90
   */
  public Iterable<T> astChildren() {
    return new Iterable<T>() {
      @Override
      public java.util.Iterator<T> iterator() {
        return astChildIterator();
      }
    };
  }
  /**
   * @declaredat ASTNode:99
   */
  public static String nodeToString(Object node) {
    return (node != null ? node.getClass().getSimpleName() : "null");
  }
  /** @apilevel low-level 
   * @declaredat ASTNode:103
   */
  public T getChild(int i) {
    ASTNode child = getChildNoTransform(i);
    return (T) child;
  }
  /** @apilevel low-level 
   * @declaredat ASTNode:108
   */
  public ASTNode addChild(T node) {
    setChild(node, getNumChildNoTransform());
    return this;
  }
  /**
   * Gets a child without triggering rewrites.
   * @apilevel low-level
   * @declaredat ASTNode:116
   */
  public T getChildNoTransform(int i) {
    if (children == null) {
      return null;
    }
    T child = (T) children[i];
    return child;
  }
  /** @apilevel low-level 
   * @declaredat ASTNode:124
   */
  protected int numChildren;
  /** @apilevel low-level 
   * @declaredat ASTNode:127
   */
  protected int numChildren() {
    return numChildren;
  }
  /** @apilevel low-level 
   * @declaredat ASTNode:132
   */
  public int getNumChild() {
    return numChildren();
  }
  /**
   * Behaves like getNumChild, but does not invoke AST transformations (rewrites).
   * @apilevel low-level
   * @declaredat ASTNode:140
   */
  public final int getNumChildNoTransform() {
    return numChildren();
  }
  /** @apilevel low-level 
   * @declaredat ASTNode:144
   */
  public ASTNode setChild(ASTNode node, int i) {
    if (children == null) {
      children = new ASTNode[(i + 1 > 4 || !(this instanceof List)) ? i + 1 : 4];
    } else if (i >= children.length) {
      ASTNode c[] = new ASTNode[i << 1];
      System.arraycopy(children, 0, c, 0, children.length);
      children = c;
    }
    children[i] = node;
    if (i >= numChildren) {
      numChildren = i+1;
    }
    if (node != null) {
      node.setParent(this);
      node.childIndex = i;
    }
    return this;
  }
  /** @apilevel low-level 
   * @declaredat ASTNode:163
   */
  public ASTNode insertChild(ASTNode node, int i) {
    if (children == null) {
      children = new ASTNode[(i + 1 > 4 || !(this instanceof List)) ? i + 1 : 4];
      children[i] = node;
    } else {
      ASTNode c[] = new ASTNode[children.length + 1];
      System.arraycopy(children, 0, c, 0, i);
      c[i] = node;
      if (i < children.length) {
        System.arraycopy(children, i, c, i+1, children.length-i);
        for(int j = i+1; j < c.length; ++j) {
          if (c[j] != null) {
            c[j].childIndex = j;
          }
        }
      }
      children = c;
    }
    numChildren++;
    if (node != null) {
      node.setParent(this);
      node.childIndex = i;
    }
    return this;
  }
  /** @apilevel low-level 
   * @declaredat ASTNode:189
   */
  public void removeChild(int i) {
    if (children != null) {
      ASTNode child = (ASTNode) children[i];
      if (child != null) {
        child.parent = null;
        child.childIndex = -1;
      }
      // Adding a check of this instance to make sure its a List, a move of children doesn't make
      // any sense for a node unless its a list. Also, there is a problem if a child of a non-List node is removed
      // and siblings are moved one step to the right, with null at the end.
      if (this instanceof List || this instanceof Opt) {
        System.arraycopy(children, i+1, children, i, children.length-i-1);
        children[children.length-1] = null;
        numChildren--;
        // fix child indices
        for(int j = i; j < numChildren; ++j) {
          if (children[j] != null) {
            child = (ASTNode) children[j];
            child.childIndex = j;
          }
        }
      } else {
        children[i] = null;
      }
    }
  }
  /** @apilevel low-level 
   * @declaredat ASTNode:216
   */
  public ASTNode getParent() {
    return (ASTNode) parent;
  }
  /** @apilevel low-level 
   * @declaredat ASTNode:220
   */
  public void setParent(ASTNode node) {
    parent = node;
  }
  /** @apilevel low-level 
   * @declaredat ASTNode:285
   */
  public void flushTreeCache() {
    flushCache();
    if (children != null) {
      for (int i = 0; i < children.length; i++) {
        if (children[i] != null) {
          ((ASTNode) children[i]).flushTreeCache();
        }
      }
    }
  }
  /** @apilevel low-level 
   * @declaredat ASTNode:296
   */
  public void flushCache() {
    flushAttrAndCollectionCache();
  }
  /** @apilevel internal 
   * @declaredat ASTNode:300
   */
  public void flushAttrAndCollectionCache() {
    flushAttrCache();
    flushCollectionCache();
  }
  /** @apilevel internal 
   * @declaredat ASTNode:305
   */
  public void flushAttrCache() {
    lsp_hover_reset();
    range_reset();
    program_reset();
    unknownDecl_reset();
  }
  /** @apilevel internal 
   * @declaredat ASTNode:312
   */
  public void flushCollectionCache() {
  }
  /** @apilevel internal 
   * @declaredat ASTNode:315
   */
  public ASTNode<T> clone() throws CloneNotSupportedException {
    ASTNode node = (ASTNode) super.clone();
    node.flushAttrAndCollectionCache();
    return node;
  }
  /** @apilevel internal 
   * @declaredat ASTNode:321
   */
  public ASTNode<T> copy() {
    try {
      ASTNode node = (ASTNode) clone();
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
   * @declaredat ASTNode:340
   */
  @Deprecated
  public ASTNode<T> fullCopy() {
    return treeCopyNoTransform();
  }
  /**
   * Create a deep copy of the AST subtree at this node.
   * The copy is dangling, i.e. has no parent.
   * @return dangling copy of the subtree at this node
   * @apilevel low-level
   * @declaredat ASTNode:350
   */
  public ASTNode<T> treeCopyNoTransform() {
    ASTNode tree = (ASTNode) copy();
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
   * @declaredat ASTNode:370
   */
  public ASTNode<T> treeCopy() {
    ASTNode tree = (ASTNode) copy();
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
   * Performs a full traversal of the tree using getChild to trigger rewrites
   * @apilevel low-level
   * @declaredat ASTNode:387
   */
  public void doFullTraversal() {
    for (int i = 0; i < getNumChild(); i++) {
      getChild(i).doFullTraversal();
    }
  }
  /**
   * @aspect <NoAspect>
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\Errors.jrag:26
   */
    /** @apilevel internal */
  protected void collect_contributors_Program_errors(Program _root, java.util.Map<ASTNode, java.util.Set<ASTNode>> _map) {
    for (int i = 0; i < getNumChild(); i++) {
      getChild(i).collect_contributors_Program_errors(_root, _map);
    }
  }
  /** @apilevel internal */
  protected void contributeTo_Program_errors(Set<ErrorMessage> collection) {
  }

  /**
   * @aspect <NoAspect>
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\LSP.jrag:16
   */
    /** @apilevel internal */
  protected void collect_contributors_Program_lsp_diagnostics(Program _root, java.util.Map<ASTNode, java.util.Set<ASTNode>> _map) {
    for (int i = 0; i < getNumChild(); i++) {
      getChild(i).collect_contributors_Program_lsp_diagnostics(_root, _map);
    }
  }
  /** @apilevel internal */
  protected void contributeTo_Program_lsp_diagnostics(Set<Diagnostic> collection) {
  }

/** @apilevel internal */
protected boolean lsp_hover_visited = false;
  /** @apilevel internal */
  private void lsp_hover_reset() {
    lsp_hover_computed = false;
    
    lsp_hover_value = null;
    lsp_hover_visited = false;
  }
  /** @apilevel internal */
  protected boolean lsp_hover_computed = false;

  /** @apilevel internal */
  protected String lsp_hover_value;

  /**
   * @attribute syn
   * @aspect LSP
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\LSP.jrag:5
   */
  @ASTNodeAnnotation.Attribute(kind=ASTNodeAnnotation.Kind.SYN)
  @ASTNodeAnnotation.Source(aspect="LSP", declaredAt="C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\LSP.jrag:5")
  public String lsp_hover() {
    ASTState state = state();
    if (lsp_hover_computed) {
      return lsp_hover_value;
    }
    if (lsp_hover_visited) {
      throw new RuntimeException("Circular definition of attribute ASTNode.lsp_hover().");
    }
    lsp_hover_visited = true;
    state().enterLazyAttribute();
    state().trace().computeBegin("LSP", this, "ASTNode.lsp_hover()", "", "");
    lsp_hover_value = this.getClass().getSimpleName();
    state().trace().computeEnd("LSP", this, "ASTNode.lsp_hover()", "", lsp_hover_value);
    lsp_hover_computed = true;
    state().leaveLazyAttribute();
    lsp_hover_visited = false;
    return lsp_hover_value;
  }
/** @apilevel internal */
protected boolean range_visited = false;
  /** @apilevel internal */
  private void range_reset() {
    range_computed = false;
    
    range_value = null;
    range_visited = false;
  }
  /** @apilevel internal */
  protected boolean range_computed = false;

  /** @apilevel internal */
  protected Range range_value;

  /**
   * @attribute syn
   * @aspect LSPDiagnostics
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\LSP.jrag:18
   */
  @ASTNodeAnnotation.Attribute(kind=ASTNodeAnnotation.Kind.SYN)
  @ASTNodeAnnotation.Source(aspect="LSPDiagnostics", declaredAt="C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\LSP.jrag:18")
  public Range range() {
    ASTState state = state();
    if (range_computed) {
      return range_value;
    }
    if (range_visited) {
      throw new RuntimeException("Circular definition of attribute ASTNode.range().");
    }
    range_visited = true;
    state().enterLazyAttribute();
    state().trace().computeBegin("LSPDiagnostics", this, "ASTNode.range()", "", "");
    range_value = Range.of(getStart(), getEnd());
    state().trace().computeEnd("LSPDiagnostics", this, "ASTNode.range()", "", range_value);
    range_computed = true;
    state().leaveLazyAttribute();
    range_visited = false;
    return range_value;
  }
  /**
   * @attribute inh
   * @aspect Errors
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\Errors.jrag:28
   */
  @ASTNodeAnnotation.Attribute(kind=ASTNodeAnnotation.Kind.INH)
  @ASTNodeAnnotation.Source(aspect="Errors", declaredAt="C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\Errors.jrag:28")
  public Program program() {
    ASTState state = state();
    if (program_computed) {
      return program_value;
    }
    if (program_visited) {
      throw new RuntimeException("Circular definition of attribute ASTNode.program().");
    }
    program_visited = true;
    state().enterLazyAttribute();
    state().trace().computeBegin("Errors", this, "ASTNode.program()", "", "");
    program_value = getParent().Define_program(this, null);
    state().trace().computeEnd("Errors", this, "ASTNode.program()", "", program_value);
    program_computed = true;
    state().leaveLazyAttribute();
    program_visited = false;
    return program_value;
  }
/** @apilevel internal */
protected boolean program_visited = false;
  /** @apilevel internal */
  private void program_reset() {
    program_computed = false;
    
    program_value = null;
    program_visited = false;
  }
  /** @apilevel internal */
  protected boolean program_computed = false;

  /** @apilevel internal */
  protected Program program_value;

  /**
   * @attribute inh
   * @aspect UnknownDecl
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\UnknownDecl.jrag:4
   */
  @ASTNodeAnnotation.Attribute(kind=ASTNodeAnnotation.Kind.INH)
  @ASTNodeAnnotation.Source(aspect="UnknownDecl", declaredAt="C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\UnknownDecl.jrag:4")
  public UnknownDecl unknownDecl() {
    ASTState state = state();
    if (unknownDecl_computed) {
      return unknownDecl_value;
    }
    if (unknownDecl_visited) {
      throw new RuntimeException("Circular definition of attribute ASTNode.unknownDecl().");
    }
    unknownDecl_visited = true;
    state().enterLazyAttribute();
    state().trace().computeBegin("UnknownDecl", this, "ASTNode.unknownDecl()", "", "");
    unknownDecl_value = getParent().Define_unknownDecl(this, null);
    state().trace().computeEnd("UnknownDecl", this, "ASTNode.unknownDecl()", "", unknownDecl_value);
    unknownDecl_computed = true;
    state().leaveLazyAttribute();
    unknownDecl_visited = false;
    return unknownDecl_value;
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

  /** @apilevel internal */
  public Program Define_program(ASTNode _callerNode, ASTNode _childNode) {
    ASTNode self = this;
    ASTNode parent = getParent();
    while (parent != null && !parent.canDefine_program(self, _callerNode)) {
      _callerNode = self;
      self = parent;
      parent = self.getParent();
    }
    return parent.Define_program(self, _callerNode);
  }

  /**
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\Errors.jrag:29
   * @apilevel internal
   * @return {@code true} if this node has an equation for the inherited attribute program
   */
  protected boolean canDefine_program(ASTNode _callerNode, ASTNode _childNode) {
    return false;
  }
  /** @apilevel internal */
  public IdDecl Define_lookup(ASTNode _callerNode, ASTNode _childNode, String name) {
    ASTNode self = this;
    ASTNode parent = getParent();
    while (parent != null && !parent.canDefine_lookup(self, _callerNode, name)) {
      _callerNode = self;
      self = parent;
      parent = self.getParent();
    }
    return parent.Define_lookup(self, _callerNode, name);
  }

  /**
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\NameAnalysis.jrag:21
   * @apilevel internal
   * @return {@code true} if this node has an equation for the inherited attribute lookup
   */
  protected boolean canDefine_lookup(ASTNode _callerNode, ASTNode _childNode, String name) {
    return false;
  }
  /** @apilevel internal */
  public boolean Define_inExprOf(ASTNode _callerNode, ASTNode _childNode, IdDecl decl) {
    ASTNode self = this;
    ASTNode parent = getParent();
    while (parent != null && !parent.canDefine_inExprOf(self, _callerNode, decl)) {
      _callerNode = self;
      self = parent;
      parent = self.getParent();
    }
    return parent.Define_inExprOf(self, _callerNode, decl);
  }

  /**
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\NameAnalysis.jrag:35
   * @apilevel internal
   * @return {@code true} if this node has an equation for the inherited attribute inExprOf
   */
  protected boolean canDefine_inExprOf(ASTNode _callerNode, ASTNode _childNode, IdDecl decl) {
    return false;
  }
  /** @apilevel internal */
  public UnknownDecl Define_unknownDecl(ASTNode _callerNode, ASTNode _childNode) {
    ASTNode self = this;
    ASTNode parent = getParent();
    while (parent != null && !parent.canDefine_unknownDecl(self, _callerNode)) {
      _callerNode = self;
      self = parent;
      parent = self.getParent();
    }
    return parent.Define_unknownDecl(self, _callerNode);
  }

  /**
   * @declaredat C:\\Programming\\LTH\\Project in computer science (EDAN70)\\jastaddlsp-dag-johannes\\examples\\CalcRAG\\src\\jastadd\\UnknownDecl.jrag:5
   * @apilevel internal
   * @return {@code true} if this node has an equation for the inherited attribute unknownDecl
   */
  protected boolean canDefine_unknownDecl(ASTNode _callerNode, ASTNode _childNode) {
    return false;
  }
public ASTNode rewrittenNode() { throw new Error("rewrittenNode is undefined for ASTNode"); }

}
