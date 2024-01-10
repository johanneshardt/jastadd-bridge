package lang.ast;

import jastaddBridge.interop.*;
import java.io.PrintStream;
import java.util.Set;
import java.util.TreeSet;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
/** @apilevel internal 
 * @ast class
 * @declaredat ASTState:34
 */
public class ASTState extends java.lang.Object {
  /**
   * This class stores an attribute value tagged with an iteration ID for
   * a circular evaluation.
   * 
   * @apilevel internal
   * @declaredat ASTState:41
   */
  
  /**
   * This class stores an attribute value tagged with an iteration ID for
   * a circular evaluation.
   *
   * @apilevel internal
   */
  protected static class CircularValue {
    Object value;
    Cycle cycle;
  }
  /**
   * Instances of this class are used to uniquely identify circular evaluation iterations.
   * These iteration ID objects are created for each new fixed-point iteration in
   * a circular evaluation.
   * 
   * @apilevel internal
   * @declaredat ASTState:53
   */
  

  /**
   * Instances of this class are used to uniquely identify circular evaluation iterations.
   * These iteration ID objects are created for each new fixed-point iteration in
   * a circular evaluation.
   *
   * @apilevel internal
   */
  protected static class Cycle {
  }
  /**
   * The iteration ID used outside of circular evaluation.
   * 
   * <p>This is the iteration ID when no circular evaluation is ongoing.
   * @declaredat ASTState:61
   */
  

  /**
   * The iteration ID used outside of circular evaluation.
   *
   * <p>This is the iteration ID when no circular evaluation is ongoing.
   */
  public static final Cycle NON_CYCLE = new Cycle();
  /**
   * Tracks the state of the current circular evaluation. This class defines a
   * stack structure where the next element on the stack is pointed to by the
   * {@code next} field.
   * 
   * @apilevel internal
   * @declaredat ASTState:70
   */
  

  /**
   * Tracks the state of the current circular evaluation. This class defines a
   * stack structure where the next element on the stack is pointed to by the
   * {@code next} field.
   *
   * @apilevel internal
   */
  protected static class CircleState {
    final CircleState next;
    boolean change = false;

    /** Evaluation depth of lazy attributes. */
    int lazyAttribute = 0;

    boolean lastCycle = false;

    /** Cycle ID of the latest cycle in this circular evaluation. */
    Cycle cycle = NON_CYCLE;


    protected CircleState(CircleState next) {
      this.next = next;
    }
  }
  /** Sentinel circle state representing non-circular evaluation. 
   * @declaredat ASTState:90
   */
  


  /** Sentinel circle state representing non-circular evaluation. */
  private static final CircleState CIRCLE_BOTTOM = new CircleState(null);
  /**
   * Current circular state.
   * @apilevel internal
   * @declaredat ASTState:96
   */
  

  /**
   * Current circular state.
   * @apilevel internal
   */
  private CircleState circle = CIRCLE_BOTTOM;
  /** @apilevel internal 
   * @declaredat ASTState:99
   */
  

  /** @apilevel internal */
  protected boolean inCircle() {
    return circle != CIRCLE_BOTTOM;
  }
  /** @apilevel internal 
   * @declaredat ASTState:104
   */
  

  /** @apilevel internal */
  protected boolean calledByLazyAttribute() {
    return circle.lazyAttribute > 0;
  }
  /** @apilevel internal 
   * @declaredat ASTState:109
   */
  

  /** @apilevel internal */
  protected void enterLazyAttribute() {
    circle.lazyAttribute += 1;
  }
  /** @apilevel internal 
   * @declaredat ASTState:114
   */
  

  /** @apilevel internal */
  protected void leaveLazyAttribute() {
    circle.lazyAttribute -= 1;
  }
  /** @apilevel internal 
   * @declaredat ASTState:119
   */
  

  /** @apilevel internal */
  protected void enterCircle() {
    CircleState next = new CircleState(circle);
    circle = next;
  }
  /** @apilevel internal 
   * @declaredat ASTState:126
   */
  


  /** @apilevel internal */
  protected void leaveCircle() {
    circle = circle.next;
  }
  /** @apilevel internal 
   * @declaredat ASTState:131
   */
  

  /** @apilevel internal */
  protected Cycle nextCycle() {
    Cycle cycle = new Cycle();
    circle.cycle = cycle;
    return cycle;
  }
  /** @apilevel internal 
   * @declaredat ASTState:138
   */
  

  /** @apilevel internal */
  protected Cycle cycle() {
    return circle.cycle;
  }
  /** @apilevel internal 
   * @declaredat ASTState:143
   */
  

  /** @apilevel internal */
  protected CircleState currentCircle() {
    return circle;
  }
  /** @apilevel internal 
   * @declaredat ASTState:149
   */
  


  /** @apilevel internal */
  protected void setChangeInCycle() {
    circle.change = true;
  }
  /** @apilevel internal 
   * @declaredat ASTState:154
   */
  

  /** @apilevel internal */
  protected boolean testAndClearChangeInCycle() {
    boolean change = circle.change;
    circle.change = false;
    return change;
  }
  /** @apilevel internal 
   * @declaredat ASTState:161
   */
  

  /** @apilevel internal */
  protected boolean changeInCycle() {
    return circle.change;
  }
  /** @apilevel internal 
   * @declaredat ASTState:166
   */
  

  /** @apilevel internal */
  protected boolean lastCycle() {
    return circle.lastCycle;
  }
  /**
   * This is part of the cacheCycle optimization:
   * a circular attribute is evaluated one extra time after the
   * last iteration of Case1, in order to mark all dependencies
   * used in the last iteration as memoized.
   * @apilevel internal
   * @declaredat ASTState:177
   */
  

  /**
   * This is part of the cacheCycle optimization:
   * a circular attribute is evaluated one extra time after the
   * last iteration of Case1, in order to mark all dependencies
   * used in the last iteration as memoized.
   * @apilevel internal
   */
  protected void startLastCycle() {
    nextCycle();
    circle.lastCycle = true;
  }
  /**
   * @declaredat ASTState:182
   */
  

  protected ASTState() {
  }
  /**
   * @declaredat ASTState:185
   */
  

  public interface ReceiverFactory {
    Trace.Receiver build();
  }
  /**
   * @declaredat ASTState:189
   */
  

  public static ReceiverFactory receiverFactory = new ReceiverFactory() {
    public Trace.Receiver build() {
      return new Trace.Receiver() {
        public void accept(ASTState.Trace.Event event, ASTNode node, String attribute,
            Object params, Object value) {
        }
      };
    }
  };
  /**
   * @declaredat ASTState:199
   */
  

  private Trace trace = null;
  /** @return the tracer instance used for tracing attribute evaluation in this AST. 
   * @declaredat ASTState:202
   */
  

  /** @return the tracer instance used for tracing attribute evaluation in this AST. */
  public Trace trace() {
    if (trace == null) {
      trace = new Trace(receiverFactory.build());
    }
    return trace;
  }
  /**
   * @declaredat ASTState:209
   */
  

  public static class Trace {
    /**
     * Trace events corresponding to attribute evaluation events.
     *
     * <p>These events can be filtered statically using the flag --tracing to
     * JastAdd2. For example, the flag {@code --tracing=compute,cache} will only trace
     * compute events and cache events. The flag --tracing will enable all events.
     *
     * <p>To access the trace events you will need to register an event receiver.
     * This can be done using the method setReceiver(ASTState.Trace.Receiver).
     */
    public enum Event {
      // Flag: --tracing=compute
      COMPUTE_BEGIN,
      COMPUTE_END,
  
      // Flag: --tracing=cache
      CACHE_WRITE,
      CACHE_READ,
      CACHE_ABORT,
  
      // Flag: --tracing=rewrite
      REWRITE_CASE1_START,
      REWRITE_CASE1_CHANGE,
      REWRITE_CASE1_RETURN,
      REWRITE_CASE2_RETURN,
      REWRITE_CASE3_RETURN,
  
      // Flag: --tracing=circular
      CIRCULAR_NTA_CASE1_START,
      CIRCULAR_NTA_CASE1_CHANGE,
      CIRCULAR_NTA_CASE1_RETURN,
      CIRCULAR_NTA_CASE2_START,
      CIRCULAR_NTA_CASE2_CHANGE,
      CIRCULAR_NTA_CASE2_RETURN,
      CIRCULAR_NTA_CASE3_RETURN,
      CIRCULAR_CASE1_START,
      CIRCULAR_CASE1_CHANGE,
      CIRCULAR_CASE1_RETURN,
      CIRCULAR_CASE2_START,
      CIRCULAR_CASE2_CHANGE,
      CIRCULAR_CASE2_RETURN,
      CIRCULAR_CASE3_RETURN,
  
      // Flag: --tracing=copy
      COPY_NODE,
  
      // Flag: --tracing=flush
      FLUSH_ATTR,
      FLUSH_REWRITE,
      FLUSH_REWRITE_INIT,
      INC_FLUSH_START,
      INC_FLUSH_ATTR,
      INC_FLUSH_END,
  
      // Flag: --tracing=coll
      CONTRIBUTION_CHECK_BEGIN,
      CONTRIBUTION_CHECK_MATCH,
      CONTRIBUTION_CHECK_END,
      
      // Flag: --tracing=token
      TOKEN_READ;
    }
  
    // For traced operations inherent in all ASTs, e.g., copy, or from elements
    // derived from the grammar, e.g., tokens.
    public static final String ASPECT_AST = "AST";
  
    /**
     * Functional interface for a trace event receiver.
     * This can be implemented by applications that want to trace attribute evaluation.
     */
    public interface Receiver {
      void accept(ASTState.Trace.Event event, ASTNode node, String attribute, Object params, Object value);
      default void accept(ASTState.Trace.Event event, String aspect, ASTNode node, String attribute,
          Object params, Object value) {
  	accept(event, node, attribute, params, value);
      }
    }
  
    public Trace(Receiver receiver) {
      this.receiver = receiver;
    }
  
    public Trace() {
    }
  
    // The default event receiver does nothing.
    private Receiver receiver = new Receiver() {
      public void accept(ASTState.Trace.Event event, ASTNode node, String attribute,
          Object params, Object value) {
      }
    };
  
    /**
     * Registers an input filter to use during tracing.
     * @param filter The input filter to register.
     */
    public void setReceiver(ASTState.Trace.Receiver receiver) {
      this.receiver = receiver;
    }
  
    public Receiver getReceiver() {
      return receiver;
    }
  
    /**
     * Trace that an attribute instance started its computation.
     * @param value The value of the attribute instance.
     */
    public void computeBegin(String aspect, ASTNode node, String attr, Object params, Object value) {
      receiver.accept(Event.COMPUTE_BEGIN, aspect, node, attr, params, value);
    }
  
    /**
     * Trace that an attribute instance ended its computation.
     * @param value The value of the attribute instance.
     */
    public void computeEnd(String aspect, ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.COMPUTE_END, aspect, node, attr, params, value);
    }
  
    /**
     * Trace that the cache of an attribute instances was read.
     * @param value The value of the attribute instance.
     */
    public void cacheRead(String aspect, ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.CACHE_READ, aspect, node, attr, params, value);
    }
  
    /**
     * Trace that an attribute instance was cached.
     * @param value The value of the attribute instance.
     */
    public void cacheWrite(String aspect, ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.CACHE_WRITE, aspect, node, attr, params, value);
    }
  
    /**
     * Trace that the caching of an attribute instance was aborted.
     * @param value The value of the attribute instance.
     */
    public void cacheAbort(String aspect, ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.CACHE_ABORT, aspect, node, attr, params, value);
    }
  
    /**
     * Trace that a rewrite evaluation entered case 1.
     * @param value The value of the rewrite.
     */
    public void enterRewriteCase1(ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.REWRITE_CASE1_START, ASPECT_AST, node, attr, params, value);
    }
  
    /**
     * Trace that a rewrite in evaluation case 1 changed value.
     * @param value The value of the rewrite before and after.
     */
    public void rewriteChange(ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.REWRITE_CASE1_CHANGE, ASPECT_AST, node, attr, params, value);
    }
  
    /**
     * Trace that a rewrite returned from evaluation case 1.
     * @param value The value of the rewrite.
     */
    public void exitRewriteCase1(ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.REWRITE_CASE1_RETURN, ASPECT_AST, node, attr, params, value);
    }
  
    /**
     * Trace that a rewrite returned from evaluation case 2.
     * @param value The value of the rewrite.
     */
    public void exitRewriteCase2(ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.REWRITE_CASE2_RETURN, ASPECT_AST, node, attr, params, value);
    }
  
    /**
     * Trace that a rewrite returned from evaluation case 3.
     * @param value The value of the rewrite.
     */
    public void exitRewriteCase3(ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.REWRITE_CASE3_RETURN, ASPECT_AST, node, attr, params, value);
    }
  
    /**
     * Trace that a circular attribute instance entered evaluation case 1.
     * @param value The value of the circular attribute instance.
     */
    public void enterCircularCase1(String aspect, ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.CIRCULAR_CASE1_START, aspect, node, attr, params, value);
    }
  
    /**
     * Trace that a circular attribute instance in evaluation case 1 changed value.
     * @param value The value of the circular attribute instance, before and after.
     */
    public void circularCase1Change(String aspect, ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.CIRCULAR_CASE1_CHANGE, aspect, node, attr, params, value);
    }
  
    /**
     * Trace that a circular attribute instance returned from evaluation case 1.
     * @param value The value of the circular attribute instance.
     */
    public void exitCircularCase1(String aspect, ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.CIRCULAR_CASE1_RETURN, aspect, node, attr, params, value);
    }
  
    /**
     * Trace that a circular attribute instance entered evaluation case 2.
     * @param value The value of the circular attribute instance.
     */
    public void enterCircularCase2(String aspect, ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.CIRCULAR_CASE2_START, aspect, node, attr, params, value);
    }
  
    /**
     * Trace that a circular attribute instance in evaluation case 2 changed value.
     * @param value The value of the circular attribute instance, before and after.
     */
    public void circularCase2Change(String aspect, ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.CIRCULAR_CASE2_CHANGE, aspect, node, attr, params, value);
    }
  
    /**
     * Trace that a circular attribute instance returned from evaluation case 2.
     * @param value The value of the circular attribute instance.
     */
    public void exitCircularCase2(String aspect, ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.CIRCULAR_CASE2_RETURN, aspect, node, attr, params, value);
    }
  
    /**
     * Trace that a circular attribute instance returned from evaluation case 2.
     * @param value The value of the circular attribute instance.
     */
    public void exitCircularCase3(String aspect, ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.CIRCULAR_CASE3_RETURN, aspect, node, attr, params, value);
    }
  
    /**
     * Trace that a circular NTA entered evaluation case 1.
     * @param value The value of the circular NTA.
     */
    public void enterCircularNTACase1(String aspect, ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.CIRCULAR_NTA_CASE1_START, aspect, node, attr, params, value);
    }
  
    /**
     * Trace that a circular NTA in evaluation case 1 changed value.
     * @param value The value of the circular NTA, before and after.
     */
    public void circularNTACase1Change(String aspect, ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.CIRCULAR_NTA_CASE1_CHANGE, aspect, node, attr, params, value);
    }
  
    /**
     * Trace that a circular NTA returned from evaluation case 1.
     * @param value The value of the circular NTA.
     */
    public void exitCircularNTACase1(String aspect, ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.CIRCULAR_NTA_CASE1_RETURN, aspect, node, attr, params, value);
    }
  
    /**
     * Trace that a circular NTA entered evaluation case 2.
     * @param value The value of the circular NTA.
     */
    public void enterCircularNTACase2(String aspect, ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.CIRCULAR_NTA_CASE2_START, aspect, node, attr, params, value);
    }
  
    /**
     * Trace that a circular NTA in evaluation case 2 changed value.
     * @param value The value of the circular NTA, before and after.
     */
    public void circularNTACase2Change(String aspect, ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.CIRCULAR_NTA_CASE2_CHANGE, aspect, node, attr, params, value);
    }
  
    /**
     * Trace that a circular NTA returned from evaluation case 2.
     * @param value The value of the circular NTA.
     */
    public void exitCircularNTACase2(String aspect, ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.CIRCULAR_NTA_CASE2_RETURN, aspect, node, attr, params, value);
    }
  
    /**
     * Trace that a circular NTA returned from evaluation case 2.
     * @param value The value of the circular NTA.
     */
    public void exitCircularNTACase3(String aspect, ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.CIRCULAR_NTA_CASE3_RETURN, aspect, node, attr, params, value);
    }
  
    /**
     * Trace that an AST node was copied.
     * @param node The copied node.
     * @param value The value of the node.
     */
    public void copyNode(ASTNode node, Object value) {
      receiver.accept(ASTState.Trace.Event.COPY_NODE, ASPECT_AST, node, "ASTNode.copy", "", value);
    }
  
    /**
     * Trace that an attribute was flushed.
     * @param value The value of the attribute.
     */
    public void flushAttr(String aspect, ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.FLUSH_ATTR, aspect, node, attr, params, value);
    }
  
    /**
     * Trace that an attribute was flushed by incremental evaluation.
     */
    public void flushIncAttr(String aspect, ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.INC_FLUSH_ATTR, aspect, node, attr, params, value);
    }
  
    /**
     * Trace that flushing done by incremental evaluation begun.
     */
    public void flushIncStart(ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.INC_FLUSH_START, ASPECT_AST, node, attr, params, value);
    }
  
    /**
     * Trace that flushing done by incremental evaluation ended.
     */
    public void flushIncEnd(ASTNode node, String attr, Object params, Object value) {
      receiver.accept(ASTState.Trace.Event.INC_FLUSH_END, ASPECT_AST, node, attr, params, value);
    }
  
    /**
     * Trace that a contribution check to a collection attribute begun.
     */
    public void contributionCheckBegin(String aspect, ASTNode node, String attr, String check) {
      receiver.accept(ASTState.Trace.Event.CONTRIBUTION_CHECK_BEGIN, aspect, node, attr, check, "");
    }
  
    /**
     * Trace that a contribution check to a collection attribute ended.
     */
    public void contributionCheckEnd(String aspect, ASTNode node, String attr, String check) {
      receiver.accept(ASTState.Trace.Event.CONTRIBUTION_CHECK_END, aspect, node, attr, check, "");
    }
  
    /**
     * Trace that a contribution check to a collection attribute found a match.
     */
    public void contributionCheckMatch(String aspect, ASTNode node, String attr, String check, Object value) {
      receiver.accept(ASTState.Trace.Event.CONTRIBUTION_CHECK_MATCH, aspect, node, attr, check, value);
    }
  
    /**
     * Trace that a token was read.
     */
    public void tokenRead(ASTNode node, String token, Object value) {
      receiver.accept(ASTState.Trace.Event.TOKEN_READ, ASPECT_AST, node, token, "", value);
    }
  
  }
  /** @apilevel internal 
   * @declaredat ASTNode:280
   */
  public void reset() {
    // Reset circular evaluation state.
    circle = CIRCLE_BOTTOM;
  }

}
