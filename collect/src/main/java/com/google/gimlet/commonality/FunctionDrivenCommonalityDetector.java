// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.commonality;

import com.google.common.base.Function;
import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.Iterables;

/**
 * Provides an implementation of {@link CommonalityDetector} that extracts
 * a purported common value from an iterable of items using a function that is
 * given to it.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
class FunctionDrivenCommonalityDetector<T, S> implements
    CommonalityDetector<T, S> {

  private final Function<? super T, ? extends S> valueExtractor;

  FunctionDrivenCommonalityDetector(
      Function<? super T, ? extends S> valueExtractor) {
    this.valueExtractor = valueExtractor;
  }

  @Override public S detectCommonality(Iterable<T> items)
      throws InconsistentCommonalityException {
    checkArgument(
        !Iterables.isEmpty(items), "Must provide a non-empty argument");

    T sampleItem = Iterables.get(items, 0);
    S sampleValue = valueExtractor.apply(sampleItem);

    for (T item : items) {
      if (!sampleValue.equals(valueExtractor.apply(item))) {
        throw new InconsistentCommonalityException(
            "Not all items had the same value:\n\t" +
                Iterables.toString(items));
      }
    }
    return sampleValue;
  }
}
