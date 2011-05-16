// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.gimlet.collect;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.List;

/**
 * Collection of functions to create {@link List}s of varying types.
 *
 * TODO: add tests for all of this.
 */
public final class GimletLists {
  private GimletLists() { }

  /**
   * Returns a list that contains the elements of the {@link Iterable} that
   * pass the {@link Predicate}.
   */
  public static <T> ImmutableList<T> filter(
      Iterable<T> iterable, Predicate<? super T> predicate) {
    return ImmutableList.copyOf(Iterables.filter(iterable, predicate));
  }

  /**
   * Returns a transformed list of the elements in {@link Iterable} using the
   * given {@link Function}.
   */
  public static <F, T> ImmutableList<T> transform(
      Iterable<F> iterable, Function<? super F, ? extends T> function) {
    return ImmutableList.copyOf(Iterables.transform(iterable, function));
  }

  /**
   * Returns a list that contains the concatenated results of the given
   * Iterables.
   */
  public static <T> ImmutableList<T> concat(
      Iterable<? extends T>... iterables) {
    return ImmutableList.copyOf(Iterables.concat(iterables));
  }

  /**
   * Returns a list that contains the concatenated results of the given Iterable
   * of Iterables.
   */
  public static <T> ImmutableList<T> concat(
      Iterable<? extends Iterable<? extends T>> iterables) {
    return ImmutableList.copyOf(Iterables.concat(iterables));
  }
}
