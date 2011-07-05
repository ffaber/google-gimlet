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

package com.google.gimlet.commonality;

import com.google.common.base.Function;

/**
 * This class provides convenience methods to facilitate using the behavior
 * defined on {@link CommonalityDetector}.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public final class CommonalityDetectors {
  private CommonalityDetectors() { }

  /**
   * Returns a {@link CommonalityDetector} that uses the given function to
   * extract values from its targets.
   */
  public static <T, S> CommonalityDetector<T, S> forFunction(
      Function<? super T, ? extends S> extractor) {
    return new FunctionDrivenCommonalityDetector<T, S>(extractor);
  }

  /**
   * Returns a {@link CommonalityDetector} which uses reflection to invoke
   * the given no-arg method on its targets in order to extract the common value
   * from each.
   */
  @SuppressWarnings("unchecked")
  public static <T, S> CommonalityDetector<T, S>
  newReflexiveCommonalityDetector(String methodName) {
    Function rawFunction = new GetValueViaReflectionFunction(methodName);
    return forFunction(rawFunction);
  }

  /**
   * Returns the result of detecting the common value of the given {@code items}
   * through means of applying generic detection logic that uses the given
   * {@code extractor}.
   */
  @SuppressWarnings("unchecked")
  public static <T, S> S detectCommonalityWithFunction(
      Iterable<T> items, Function<? super T, ? extends S> extractor) {
    // IntelliJ says the cast below is redundant, but compile fails without it.
    return forFunction(extractor).detectCommonality((Iterable) items);
  }

  /**
   * Returns the result of detecting the common value of the given {@code items}
   * through means of invoking the given no-arg method that is expected to exist
   * on each.
   */
  @SuppressWarnings("unchecked")
  public static <S> S detectCommonalityIntrospectively(
      String methodName, Iterable<?> items) {
    Function<Object, S> extractor =
        new GetValueViaReflectionFunction<S>(methodName);
    return forFunction(extractor).detectCommonality((Iterable) items);
  }
}
