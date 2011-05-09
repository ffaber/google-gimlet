// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.parallel;

import com.google.common.collect.Iterables;

import java.util.concurrent.Callable;

/**
 * Contains utility methods related to IterableCallableTransformers.
 *
 */
public class IterableCallableTransformers {

  private IterableCallableTransformers(){}

  public static IterableCallableTransformer getSimpleTransformer() {
    return new IterableCallableTransformer() {
      @Override public <T> Callable<Iterable<T>> apply(
          final Iterable<Callable<T>> from) {
        return new Callable<Iterable<T>>() {
          @Override public Iterable<T> call() throws Exception {
            return Iterables.transform(from, Callables.<T>call());
          }
        };
      }
    };
  }

}
