// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.inject.nestedscope;

import com.google.inject.matcher.AbstractMatcher;

import java.lang.reflect.Method;

/**
 * This class matches methods that are eligible for nested scoping.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
final class NestedScopeMethodMatcher extends AbstractMatcher<Method> {

  private static final NestedScopeMethodMatcher MATCHER =
      new NestedScopeMethodMatcher();

  /** Returns a singleton instance of {@link NestedScopeMethodMatcher} */
  static NestedScopeMethodMatcher methodMatcher() {
    return MATCHER;
  }

  /** Prevents external instantiation. */
  private NestedScopeMethodMatcher() { }

  @Override public boolean matches(Method method) {
    return method.isAnnotationPresent(NestedScoped.class);
  }
}
