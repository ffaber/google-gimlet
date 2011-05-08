// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.inject.nestedscope;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

/**
 * This module installs configures and installs the {@link
 * NestedScopeInterceptor}, which enables the intercept-and-scope behavior of
 * the package.  Specifically, this enables interception of a method that is
 * annotated with {@link NestedScope} so that any of its arguments that
 * correspond to parameters annotated with {@link CaptureInNestedScope} are
 * thusly captured in {@link NestedScope}.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class NestedScopeInterceptorModule extends AbstractModule {

  @Override
  protected void configure() {
    NestedScopeInterceptor nestedScopeInterceptor =
        new NestedScopeInterceptor();
    requestInjection(nestedScopeInterceptor);

    bindInterceptor(
        Matchers.any(),
        NestedScopeMethodMatcher.methodMatcher(),
        nestedScopeInterceptor);
  }
}