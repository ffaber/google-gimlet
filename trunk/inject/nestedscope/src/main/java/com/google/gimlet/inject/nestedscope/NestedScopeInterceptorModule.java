/**
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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