// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.inject.nestedscope;

import junit.framework.TestCase;

/**
 * This class provides tests for the {@link NestedScopeBasedProvider} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class NestedScopeBasedProviderTest extends TestCase {

  public void testGet() {
    NestedScopeBasedProvider nestedScopeBasedProvider =
        new NestedScopeBasedProvider();
    try {
      nestedScopeBasedProvider.get();
      fail("Expected to see an UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      // expected
    }
  }
}
