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

package com.google.gimlet.parallel;

import com.google.inject.Provider;

/**
 * Implementation of a {@link Provider} that provides {@link
 * ExecutionStrategyBasedIterableCallableTransformer}.
 *
 */
public class IterableCallableTransformerProvider
    implements Provider<ExecutionStrategyBasedIterableCallableTransformer> {

  private final ExecutionStrategy executionStrategy;

  private IterableCallableTransformerProvider(
      ExecutionStrategy executionStrategy) {
    this.executionStrategy = executionStrategy;
  }

  public static IterableCallableTransformerProvider of(
      ExecutionStrategy executionStrategy) {
    return new IterableCallableTransformerProvider(executionStrategy);
  }

  @Override public ExecutionStrategyBasedIterableCallableTransformer get() {
    return new ExecutionStrategyBasedIterableCallableTransformer(
        executionStrategy);
  }
}
