// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.parallel;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * This class provides convenience static constructors for different instances
 * of {@link ExecutionStrategy}, as well as a handful of utility methods that
 * are helpful when working with {@link ExecutionStrategy}s.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public final class ExecutionStrategies {
  private ExecutionStrategies() { }

  /**
   * Provides a {@link ExecutionStrategy} that effects changes from
   * {@link Callable} to {@link Future} by using the calling thread to
   * run the given {@link Future}.
   */
  public static ExecutionStrategy sameThreadStrategy() {
    return new DefaultExecutionStrategy(
        new Function<Callable, Future>() {
          @Override
          public Future apply(Callable from) {
            try {
              return Futures.immediateFuture(from.call());
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          }
        }
    );
  }

  /**
   * Provides a {@link ExecutionStrategy} that effects changes from
   * {@link Callable} to {@link Future} by using a new single thread to
   * run the given {@link Future}.
   */
  public static ExecutionStrategy singleThreadStrategy(
      final String threadName) {
    return new DefaultExecutionStrategy(
        new Function<Callable, Future>() {
          @SuppressWarnings({"unchecked"})
          @Override
          public Future apply(Callable from) {
            FutureTask futureTask = new FutureTask(from);
            Thread singleThread = new Thread(futureTask);
            singleThread.setName(threadName);
            singleThread.start();
            return futureTask;
          }
        }
    );
  }

  /**
   * Provides a {@link ExecutionStrategy} that effects changes from
   * {@link Callable} to {@link Future} by using using the given
   * {@link ExecutorService}.
   */
  public static ExecutionStrategy executorServiceStrategy(
      final ExecutorService executorService) {
    return new DefaultExecutionStrategy(
        new Function<Callable, Future>() {
          @SuppressWarnings({"unchecked"})
          @Override
          public Future apply(Callable from) {
            return executorService.submit(from);
          }
        }
    );
  }

  // --- Utility methods below ---

  /**
   * Effectively provides a function that transforms a given {@link Future}
   * back into the {@link Callable} domain.
   */
  static <T> Function<Future<T>, Callable<T>> obtain() {
    return new Function<Future<T>, Callable<T>>() {
      @Override
      public Callable<T> apply(final Future<T> from) {
        return new Callable<T>() {
          @Override
          public T call() throws Exception {
            return from.get();
          }
        };
      }
    };
  }
}
