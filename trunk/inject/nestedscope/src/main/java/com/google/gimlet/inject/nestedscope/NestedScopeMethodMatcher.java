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
