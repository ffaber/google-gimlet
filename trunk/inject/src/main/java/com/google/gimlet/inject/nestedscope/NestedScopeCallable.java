// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.inject.nestedscope;

import java.util.concurrent.Callable;

/**
 * This class represents a {@link Callable} that may be used to endow the thread
 * that executes the callable with a {@link BindingFrameStack}.  Specifically,
 * this is done using the following steps:
 * <ol>
 *   <li> When this callable is  created, it stores a shallow clone of the
 *    current thread's <tt>BindingFrameStack</tt>.
 *   <li> When the callable is invoked, it stores the current thread's
 *    <tt>BindingFrameStack</tt> as a local variable.
 *   <li> The callable then swaps in the shallow clone of the
 *    <tt>BindingFrameStack</tt> it stored when it was created.  This means that
 *    the current thread now sees this shallow clone to be its own
 *    <tt>BindingFrameStack</tt>.
 *   <li> When the callable is finished running, it restores the current
 *    thread's true <tt>BindingFrameStack</tt>, which was store in a local
 *    variable.
 * </ol>
 * <p>
 * Note that the cloned copy of the <tt>BindingFrameStack</tt> is created
 * <em>when this callable is created</em>.  This means that the thread that runs
 * this callable will "inherit" the scopes that existed on the thread that
 * created this callable, at the time of creation.  If the scopes of this
 * "creating" thread change after the callable is created, then this callable
 * has knowledge of that.
 * <p>
 *  A common use of this class is the emulation of
 * {@code BindingFrameStack} inheritance from a parent thread to a child thread.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
class NestedScopeCallable<T> implements Callable<T> {

  private enum NestedScopeCallableScopeId implements ScopeId {
    NESTED_SCOPE_CALLABLE_SCOPE_ID
  }

  /** The scope in which this callable is run. */
  private final NestedScopeImpl nestedScopeImpl;

  /** The inner callable run by this callable. */
  private final Callable<? extends T> innerCallable;

  /**
   * A shallow clone of the {@link BindingFrameStack} that was active for the
   * thread that created this callable at the time of creation.
   */
  private final BindingFrameStack originalBindingFrameStack;

  protected NestedScopeCallable(
      NestedScopeImpl nestedScopeImpl,
      Callable<? extends T> innerCallable) {
    this.nestedScopeImpl = nestedScopeImpl;
    this.innerCallable = innerCallable;
    this.originalBindingFrameStack = cloneStack(nestedScopeImpl);
  }

  @Override public T call() throws Exception {
    BindingFrameStack currentBindingFrames =
        nestedScopeImpl.getBindingFrameStack();
    try {
      // substitute the state of the current thread with that which is
      // stored on the callable.
      nestedScopeImpl.setBindingFrameStack(originalBindingFrameStack);

      // we enter so that if multiple threads are inheriting the
      // original binding frame stack, then the objects they scope will
      // not be visible to each other.
      nestedScopeImpl.enter(
          NestedScopeCallableScopeId.NESTED_SCOPE_CALLABLE_SCOPE_ID);
      return innerCallable.call();
    } finally {
      nestedScopeImpl.exit();
      // restore state
      nestedScopeImpl.setBindingFrameStack(currentBindingFrames);
    }
  }

  /**
   * This method clones the {@link BindingFrameStack}s of the current thread
   * from the given {@link NestedScopeImpl} and returns this clone.
   */
  private BindingFrameStack cloneStack(NestedScopeImpl nestedScopeImpl) {
    try {
      return nestedScopeImpl.getBindingFrameStack().clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }
}
