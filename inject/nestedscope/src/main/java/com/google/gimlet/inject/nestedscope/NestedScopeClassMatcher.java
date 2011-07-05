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

/**
 * This class matches classes that have methods that are eligible for nested
 * scoping.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class NestedScopeClassMatcher extends AbstractMatcher<Class<?>> {

  /**
   * A nullable package name to use as the parent package, equal to and below
   * which any package will be eligible to provide a scoped method.
   */
  private final String packageName;

  /**
   * Creates a {@link NestedScopeClassMatcher} that matches any class, without
   * prejudice.
   */
  public NestedScopeClassMatcher() {
    packageName = null;
  }

  /**
   * Creates a {@link NestedScopeClassMatcher} that matches only classes that
   * live in the given package, or within a subpackage of the given package.
   */
  public NestedScopeClassMatcher(Package aPackage) {
    packageName = aPackage.getName();
  }

  @SuppressWarnings({ "SimplifiableIfStatement" })
  @Override
  public boolean matches(Class<?> clazz) {
    if (packageName == null) {
      return true;
    }

    return clazz.getPackage().getName().startsWith(packageName);
  }
}
