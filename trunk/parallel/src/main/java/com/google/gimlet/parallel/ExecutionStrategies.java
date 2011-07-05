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
