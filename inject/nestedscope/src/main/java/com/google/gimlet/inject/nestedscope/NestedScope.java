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

import com.google.inject.Key;
import com.google.inject.Scope;

/**
 * This is a {@link Scope} that may be used to encompass a logical unit of work.
 * It provides explicit methods to enter and exist a scope.  This scoping is
 * <em>nestable</em>, which means that that it is possible for a scoped value to
 * be shadowed by a newer value that is scoped within a subsequent scope.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public interface NestedScope extends Scope {

  /** Calls {@link #enterNew(ScopeId)} with {@link ScopeId#DEFAULT}. */
  void enterNew();

  /**
   * Similar to {@link #enter(ScopeId)}, but guarantees that a new scope will
   * be entered. The exact behavior of this method is implementation dependent.
   */
  void enterNew(ScopeId scopeId);

  /**
   * This method enters into a new nested scope.  It may be called during a
   * unit of work, or at the start of a unit of work.  It is preferable to
   * call {@link #enterNew()} or {@link #enterNew(ScopeId)} at the beginning of
   * a new unit of work, because those methods will check whether the current
   * thread is already within a unit of work.  Hence, this method must be
   * used to enter a nested unit of work.
   * <p>
   * If the given {@link ScopeId} is a duplicate of an outer scope, then an
   * {@link IllegalArgumentException} is thrown.
   */
  void enter(ScopeId scopeId);

  /**
   * Allows you to add an {@code object} in scope. Must be called in between
   * {@code enter} and {@code exit}.
   * @throws IllegalStateException if an object already exists in this scope
   * for the given {@code key}
   */
  <T> void put(Key<T> key, T object);

  /**
   * Same as {@link #put(Key, Object)}, but accepts a {@link Class} from which
   * a {@link Key} is created.
   */
  <T> void put(Class<T> clazz, T object);

  /** Must be called when the unit of work completes. */
  void exit();
}
