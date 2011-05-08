// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.inject.nestedscope;


import junit.framework.TestCase;

import java.util.concurrent.Callable;

/**
 * This class includes tests for the {@link NestedScopeCallable} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class NestedScopeCallableTest extends TestCase {

  NestedScopeImpl nestedScopeImpl;
  Callable<BindingFrameStack> innerCallable;
  BindingFrameStack capturedBindingFrameStack;
  NestedScopeCallable<BindingFrameStack> nestedScopeImplCallable;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    nestedScopeImpl = new NestedScopeImpl(new SimpleBindingFrameProvider());

    innerCallable = new Callable<BindingFrameStack>() {
      public BindingFrameStack call() throws Exception {
        return nestedScopeImpl.getBindingFrameStack();
      }
    };

    BindingFrame capturedBindingFrame = new BindingFrame();
    capturedBindingFrameStack = new BindingFrameStack();
    capturedBindingFrameStack.push(capturedBindingFrame);

    nestedScopeImpl.setBindingFrameStack(capturedBindingFrameStack);
    nestedScopeImplCallable = new NestedScopeCallable<BindingFrameStack>(
        nestedScopeImpl, innerCallable);
  }

  // For each method below, this description applies.  The differences among
  // the methods are the way in which they manipulate the creating thread's
  // stack after the callable is created.
  //
  // This tests that a {@link NestedScopeCallable} substitutes the thread's
  // current {@link BindingFrameStack} with the scope that is given to it in its
  // constructor.  This method also tests that the the {@link BindingFrameStack}
  // of the thread running the callable is restored upon completion of the
  // callable's <tt>call()</tt> method.

  /** Does not change the creating thread's stack. */
  public void testNestedScopeCaptured_noChangeInStack() throws Exception {
    assertBindingFrameStacks(
        nestedScopeImpl,
        nestedScopeImplCallable,
        capturedBindingFrameStack,
        capturedBindingFrameStack);
  }

  /** Swaps in an entirely new stack for the creating thread. */
  public void testNestedScopeCaptured_enitrelyNewStack() throws Exception {
    BindingFrame newerFrame = new BindingFrame();
    BindingFrameStack newerFrameStack = new BindingFrameStack();
    newerFrameStack.push(newerFrame);
    nestedScopeImpl.setBindingFrameStack(newerFrameStack);

    assertBindingFrameStacks(
        nestedScopeImpl,
        nestedScopeImplCallable,
        newerFrameStack,
        capturedBindingFrameStack);
  }

  /** Pushes more data onto the creating thread's stack. */
  public void testNestedScopeCaptured_pushedMoreOntoStack() throws Exception {
    BindingFrameStack expectedCapturedStack = capturedBindingFrameStack.clone();
    capturedBindingFrameStack.push(new BindingFrame());

    assertBindingFrameStacks(
        nestedScopeImpl,
        nestedScopeImplCallable,
        capturedBindingFrameStack,
        expectedCapturedStack);
  }

  /** Pops frames off of the creating thread's stack. */
  public void testNestedScopeCaptured_poppedFramesFromStack() throws Exception {
    BindingFrameStack expectedCapturedStack = capturedBindingFrameStack.clone();
    capturedBindingFrameStack.pop();

    assertBindingFrameStacks(
        nestedScopeImpl,
        nestedScopeImplCallable,
        capturedBindingFrameStack,
        expectedCapturedStack);
  }

  /**
   * This method asserts that the given callable performs the expected
   * operations on the BindingFrameStacks involved.  Specifically, it checks
   * that: <ul> <li> before running, the current thread's stack is the
   * <tt>expectedCurrentStack</tt>. <li> when run, the callable sets the current
   * thread's stack to be the <tt>expectedCapturedStack</tt>. <li> when
   * complete, the callable restores the current thread's stack to be the
   * <tt>expectedCurrentStack</tt>. </ul> <p> The <tt>nestedScopeImpl</tt> is
   * passed into this method in order to determine what the current thread's
   * <tt>BindingFrameStack</tt> is at various points.
   */
  private void assertBindingFrameStacks(
      NestedScopeImpl nestedScopeImpl,
      NestedScopeCallable<BindingFrameStack> capturingCallable,
      BindingFrameStack expectedCurrentStack,
      BindingFrameStack expectedCapturedStack) throws Exception {
    // We make sure that we know what the current thread's scope is.
    assertEquals(
        "The current thread has the expected scope.",
        expectedCurrentStack,
        nestedScopeImpl.getBindingFrameStack());

    // The return value here is expected to be a copy of the captured scope.
    BindingFrameStack capturedBindingFrameStack =
        capturingCallable.call();
    assertEquals(
        "The callable appears to have used the captured scope.",
        expectedCapturedStack.getName(),
        capturedBindingFrameStack.getName());
    assertNotSame(
        "The callable appears to have used a clone the captured scope.",
        expectedCapturedStack,
        capturedBindingFrameStack);

    // We then expect that the calling thread has its scope restored.
    assertEquals(
        "The current thread has had its original scope restored.",
        expectedCurrentStack,
        nestedScopeImpl.getBindingFrameStack());
  }
}
