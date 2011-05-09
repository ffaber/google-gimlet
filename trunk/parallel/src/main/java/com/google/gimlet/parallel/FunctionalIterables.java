// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.parallel;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Provides utility methods to operate on {@link Iterable}s.
 *
 * @author ffaber@google.com (Fred Faber)
 */
public class FunctionalIterables {
  private FunctionalIterables() { }

  /**
   * Provides the means to transform an {@link Iterable<Callable<T>>} into
   * a {@link Callable<Iterable<T>>}.  Useful for collecting the results of
   * mapping a collection with a function.
   */
  public static <T> Callable<Iterable<T>> collectCallables(
      final Iterable<Callable<T>> callableIterable) {

    return new Callable<Iterable<T>>() {
      @Override
      public Iterable<T> call() throws Exception {
        List<T> collectedValues = Lists.newArrayList();
        for (Callable<T> callable : callableIterable) {
          collectedValues.add(callable.call());
        }
        return collectedValues;
      }
    };
  }
}
