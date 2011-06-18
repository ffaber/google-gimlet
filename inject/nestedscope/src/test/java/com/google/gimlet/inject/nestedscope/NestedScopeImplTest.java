// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.inject.nestedscope;

import static com.google.gimlet.testing.tl4j.JUnitAsserts.assertContentsInOrder;

import com.google.common.collect.ImmutableList;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.util.Providers;

import junit.framework.TestCase;

/**
 * This class includes tests for the {@link NestedScope} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class NestedScopeImplTest extends TestCase {

  private NestedScopeImpl nestedScopeImpl;

  @Override protected void setUp() throws Exception {
    super.setUp();
    nestedScopeImpl = new NestedScopeImpl(new SimpleBindingFrameProvider());
  }

  public void testNonNullInitialScope() {
    assertNotNull(nestedScopeImpl.getBindingFrameStack());
  }

  public void testEnterAndExitScope() {
    BindingFrameStack originalBindingFrameStack =
        nestedScopeImpl.getBindingFrameStack();
    BindingFrame originalBindingFrame = new BindingFrame();
    originalBindingFrameStack.push(originalBindingFrame);

    BindingFrame currentBindingFrame = new BindingFrame();
    nestedScopeImpl.enterScope(currentBindingFrame);

    BindingFrameStack currentBindingFrameStack =
        nestedScopeImpl.getBindingFrameStack();

    assertContentsInOrder(
        "The scopes are sequenced within the current frame stack as we expect",
        ImmutableList.copyOf(currentBindingFrameStack.getBindingFrames()),
        currentBindingFrame, originalBindingFrame);

    BindingFrame exitedBindingFrame =
        nestedScopeImpl.getBindingFrameStack().peek();
    nestedScopeImpl.exit();
    currentBindingFrame =
        nestedScopeImpl.getBindingFrameStack().peek();
    assertNotSame(
        "The old frame was popped when the scope was exited",
        exitedBindingFrame,
        currentBindingFrame);

    assertContentsInOrder(
        "After exiting one scope, the original scope remains on the stack",
        ImmutableList.copyOf(currentBindingFrameStack.getBindingFrames()),
        originalBindingFrame);

    exitedBindingFrame =
        nestedScopeImpl.getBindingFrameStack().peek();
    nestedScopeImpl.exit();
    currentBindingFrame =
        nestedScopeImpl.getBindingFrameStack().peek();
    assertNotSame(
        "The old frame was popped when the scope was exited",
        exitedBindingFrame,
        currentBindingFrame);
  }

  public void testSetBindingFrameStack() {
    BindingFrameStack newBindingFrameStack = new BindingFrameStack();
    nestedScopeImpl.setBindingFrameStack(newBindingFrameStack);
    assertEquals(
        "The scope stack was set correctly",
        newBindingFrameStack,
        nestedScopeImpl.getBindingFrameStack());
  }

  /** Tests that we can't scope 2 objects for the same key in the same frame. */
  public void testScopeSameObject_sameFrame() {
    nestedScopeImpl.enter(ValueBasedScopeId.of(NestedScopeImplTest.class));

    Key<String> key = Key.get(String.class);
    String scopedValue = "first_value";
    nestedScopeImpl.put(key, scopedValue);

    try {
      nestedScopeImpl.put(key, "second_put");
      fail("Should not have allowed 2 entries for the same key in the frame.");
    } catch (IllegalStateException ise) {
      // excepted
    }

    // Make sure the same value is still scoped
    assertEquals(scopedValue, nestedScopeImpl.scope(key, null).get());
  }

  /** Tests that we can some 2 objects for the same key in different frames. */
  public void testScopeSameObject_DifferentFrames() {
    nestedScopeImpl.enter(ValueBasedScopeId.of("frame 1"));

    Key<String> key = Key.get(String.class);
    String firstScopedValue = "first_value";
    nestedScopeImpl.put(key, firstScopedValue);

    // Check that the value is scoped correctly
    assertEquals(firstScopedValue, nestedScopeImpl.scope(key, null).get());

    // Enter another scope
    nestedScopeImpl.enter(ValueBasedScopeId.of("frame 2"));
    String secondScopedValue = "second_value";
    nestedScopeImpl.put(key, secondScopedValue);

    // Check that the new value is scoped correctly
    assertEquals(secondScopedValue, nestedScopeImpl.scope(key, null).get());

    // Exit this second scope
    nestedScopeImpl.exit();

    // Check that the value is the first scoped value once again
    assertEquals(firstScopedValue, nestedScopeImpl.scope(key, null).get());
  }

  /** Tests that the unscoped provider is used when no instance is provided. */
  public void testUnscopedProviderUsage() {
    String unscopedString = "string_value";

    Provider<String> unscopedProvider = Providers.of(unscopedString);

    Key<String> key = Key.get(String.class);
    nestedScopeImpl.enter(ValueBasedScopeId.of(NestedScopeImplTest.class));

    // Make sure that when no instance is pushed on the stack, the unscoped
    // provider is used
    assertEquals(
        unscopedString,
        nestedScopeImpl.scope(key, unscopedProvider).get()
    );
  }

  /** Tests that enterNew() resets the scope */
  public void testEnterNewScope() {
    String unscopedString = "unscoped";
    String scopedString = "scoped";

    Provider<String> unscopedProvider = Providers.of(unscopedString);

    Key<String> key = Key.get(String.class);
    nestedScopeImpl.enter(ValueBasedScopeId.of(NestedScopeImplTest.class));
    nestedScopeImpl.put(key, scopedString);

    assertEquals(
        scopedString,
        nestedScopeImpl.scope(key, unscopedProvider).get()
    );

    nestedScopeImpl.enterNew();

    assertEquals(
        unscopedString,
        nestedScopeImpl.scope(key, unscopedProvider).get()
    );
  }
}