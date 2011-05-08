// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.inject.nestedscope.testing;

import com.google.gimlet.inject.nestedscope.NestedScope;
import com.google.inject.Inject;
import com.google.inject.testing.guiceberry.TestScopeListener;

/**
 * Provides an implementation of {@link TestScopeListener} that enters and
 * exits a {@link NestedScope} when {@link #enteringScope()} and
 * {@link #exitingScope()} are called, respectively.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class NestedScopeTestScopeListener implements TestScopeListener {

  @Inject @SuppressWarnings({ "UnusedDeclaration" })
  private NestedScope nestedScope;

  @Override public void enteringScope() {
    nestedScope.enterNew();
  }

  @Override public void exitingScope() {
    nestedScope.exit();
  }
}
