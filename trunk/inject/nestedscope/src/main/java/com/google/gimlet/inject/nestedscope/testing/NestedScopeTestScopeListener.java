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
