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
import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Provides a default implementation of {@link ExecutionStrategy} which
 * implements generically the interface methods.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
class DefaultExecutionStrategy implements ExecutionStrategy {

  // TODO: this should be changed so that it is not a function since
  // we rather have the type params on the apply method, than on the the
  // Function itself.
  private final Function<Callable, Future> transform;

  DefaultExecutionStrategy(
      Function<Callable, Future> transform) {
    this.transform = transform;
  }

  @SuppressWarnings({"unchecked"})
  @Override
  public <T> Function<Callable<T>, Future<T>> getTransform() {
    Function function = transform;
    return function;
  }

  @Override
  public <T> Function<Callable<T>, Callable<T>> getParallelTransform() {
    return Functions.compose(ExecutionStrategies.<T>obtain(),
        this.<T>getTransform());
  }

  @Override
  public <L, T> Function<Iterable<L>, Callable<Iterable<T>>>
  getParallelMapTransform(final Function<L, T> mappingFunction) {
    return new Function<Iterable<L>, Callable<Iterable<T>>>() {
      @Override
      public Callable<Iterable<T>> apply(Iterable<L> from) {

        Iterable<Callable<T>> mappedIterable = Iterables.transform(from,
            Callables.returnValueAsCallable(mappingFunction));

        List<Future<T>> futureIterable = Lists.newArrayList(
            Iterables.transform(mappedIterable,
                DefaultExecutionStrategy.this.<T>getTransform()));

        Iterable<Callable<T>> resultIterable =
            Iterables.transform(
                futureIterable, ExecutionStrategies.<T>obtain());

        return FunctionalIterables.collectCallables(resultIterable);
      }
    };
  }
}
