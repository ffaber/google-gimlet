// Copyright 2011 Google Inc.  All Rights Reserved

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
