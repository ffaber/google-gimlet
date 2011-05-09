// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.parallel;

import com.google.common.base.Function;
import com.google.inject.Provider;

import java.util.concurrent.Callable;

/**
 * Provides utility methods to work with instances of {@link Callable}.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public final class Callables {
  private Callables() { }

  /**
   * Returns a {@link Callable} that returns the value produced by calling
   * {@link Provider#get()} on the given {@link Provider}.  Useful for
   * bringing instances of {@link Provider} into the callable domain.
   */
  public static <T> Callable<T> fromProvider(
      final Provider<? extends T> provider) {
    return new Callable<T>() {
      @Override public T call() throws Exception {
        return provider.get();
      }
    };
  }

  /**
   * Returns a {@link Callable} that returns the given value when
   * {@link Callable#call()} is called.
   */
  public static <T> Callable<T> callableFor(final T value) {
    return new Callable<T>() {
      @Override public T call() throws Exception {
        return value;
      }
    };
  }

  /**
   * Lifts the output of the given function into the callable domain.  This is
   * useful for lifting values in the codomain back into callables when
   * composing functions together.
   */
  public static <I, O> Function<I, Callable<O>> returnValueAsCallable(
      final Function<I, O> transform) {

    return new Function<I, Callable<O>>() {
      @Override public Callable<O> apply(final I from) {
        return new Callable<O>() {
          @Override public O call() throws Exception {
            return transform.apply(from);
          }
        };
      }
    };
  }

  /**
   * Returns a callable that always throws an
   * {@link UnsupportedOperationException}
   */
  public static <T> Callable<T> unsupportedCallable() {
    return new Callable<T>() {
      @Override public T call() throws Exception {
        throw new UnsupportedOperationException();
      }
    };
  }

  /**
   * Returns a {@link Function} that returns the value of the Callable}.
   */
  public static <T> Function<Callable<T>, T> call() {
    return new Function<Callable<T>, T>() {
      @Override public T apply(Callable<T> from) {
        try {
          return from.call();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };
  }
}
