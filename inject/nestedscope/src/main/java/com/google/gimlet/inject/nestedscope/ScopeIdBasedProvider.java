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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A provider that looks at the current stack of {@link ScopeId}s to determine
 * what object to provide based on the configured bindings of each scope.
 * <p>
 * This provide walks the stack of scopes starting from the most recent scope.
 * It returns the first value it finds for its associated {@link Key}, which
 * is to say that it prefers a value from a more inner scope over a value from
 * a more outer scope.
 */
class ScopeIdBasedProvider<T> implements Provider<T> {

  private static final Logger logger =
      Logger.getLogger(ScopeIdBasedProvider.class.getCanonicalName());

  private final Key<T> keyOfProvidedElement;
  private final Map<ScopeId, Key<? extends T>> scopedBindings;

  @Inject Provider<Iterable<ScopeId>> scopeIdsProvider;
  @Inject Injector injector;

  ScopeIdBasedProvider(
      Key<T> keyOfProvidedElement,
      Map<ScopeId, Key<? extends T>> scopedBindings) {
    this.keyOfProvidedElement = keyOfProvidedElement;
    this.scopedBindings = scopedBindings;
  }

  @Override public T get() {
    logFine("Bindings that exist: [%s]", scopedBindings.keySet());

    Iterable<ScopeId> scopeIds = checkNotNull(scopeIdsProvider.get());
    logFine("Current scopedIds: %s", scopeIds);

    for (ScopeId scopeId : scopeIds) {
      if (scopedBindings.containsKey(scopeId)) {
        Key<? extends T> key = checkNotNull(scopedBindings.get(scopeId),
            "a null provider returned for scopeId: " + scopeId);
        T instance = injector.getInstance(key);
        logFine("Returning instance of type %s for key %s from scope %s",
            instance.getClass().getName(), key, scopeId);
        return instance;
      }
    }

    throw new IllegalStateException(
        String.format(
            "Binding for %s did not exist within ScopeIds:\n%s\n" +
            "We only have bindings for ScopeIds:\n%s",
            keyOfProvidedElement, scopeIds, scopedBindings.keySet()));
  }

  private static void logFine(String fmtString, Object... args) {
    if (logger.isLoggable(Level.FINE)) {
      logger.fine(String.format(fmtString, args));
    }
  }
}