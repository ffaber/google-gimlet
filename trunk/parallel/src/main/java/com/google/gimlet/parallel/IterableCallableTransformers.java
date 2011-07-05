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
