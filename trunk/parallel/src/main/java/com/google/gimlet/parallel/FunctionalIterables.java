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

import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Provides utility methods to operate on {@link Iterable}s.
 *
 * @author ffaber@gmail.com (Fred Faber)
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
