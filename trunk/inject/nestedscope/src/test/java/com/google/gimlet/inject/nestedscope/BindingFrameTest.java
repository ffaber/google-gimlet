// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.inject.nestedscope;

import com.google.inject.Key;

import junit.framework.TestCase;

/**
 * This class provides tests for the {@link BindingFrame} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class BindingFrameTest extends TestCase {

  /** Tests simple scope lookups. */
  public void testScoping() {
    BindingFrame bindingFrame = new BindingFrame();
    Key<String> key = Key.get(String.class);
    bindingFrame.put(key, "hello");
    assertEquals("hello", bindingFrame.get(key));

    bindingFrame.put(key, "goodbye");
    assertEquals("goodbye", bindingFrame.get(key));

    assertNull(bindingFrame.get(Key.get(Integer.class)));
  }
}
