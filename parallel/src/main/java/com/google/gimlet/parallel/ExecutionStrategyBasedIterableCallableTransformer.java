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

import com.google.common.base.Function;

import java.util.concurrent.Callable;

/**
 * Implementation of {@link IterableCallableTransformer}
 * that executes the individual callables via the given {@link
 * ExecutionStrategy}.
 *
 */
class ExecutionStrategyBasedIterableCallableTransformer
    implements IterableCallableTransformer {

  private final ExecutionStrategy executionStrategy;

  ExecutionStrategyBasedIterableCallableTransformer(
      ExecutionStrategy executionStrategy) {
    this.executionStrategy = executionStrategy;
  }

  @Override
  public <T> Callable<Iterable<T>> apply(
      Iterable<Callable<T>> from) {
    return executionStrategy.getParallelMapTransform(
        new Function<Callable<T>, T>() {
          @Override
          public T apply(Callable<T> from) {
            try {
              return from.call();
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          }
        }).apply(from);
  }
}
