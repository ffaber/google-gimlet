// Copyright 2011 Google Inc.  All Rights Reserved

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
