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

/**
 * An implementation of this interface is able to iterate over a collection of
 * objects and detect the value of a common feature shared by each.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public interface CommonalityDetector<T, S> {

  /**
   * Indicates that a collection of objects do not share a purported
   * commonality.
   */
  class InconsistentCommonalityException extends RuntimeException {

    public InconsistentCommonalityException(String message) {
      super(message);
    }
  }

  /**
   * This method introspects each of the given {@code items} in order
   * to determine the value of their common feature.  It is assumed that:
   * <ol>
   *  <li> each item shares a common feature
   *  <li> the iterable is non-empty
   * </ol>
   * <p>
   * If each item is found not to share a common feature (through means of
   * invoking {@code equals(Object)} on the results of the accessor method that
   * retrieves the value of the feature), then an
   * {@link InconsistentCommonalityException} is thrown. For instance, if
   * each item is purported to have the same value returned from its
   * {@code getValue()} method, then this method invokes the method on each,
   * compares the return values, and returns the value if it is equal for all
   * items.
   * <p>
   * If an empty argument is given, an {@link IllegalArgumentException} is
   * thrown.
   */
  S detectCommonality(Iterable<T> items) throws
      InconsistentCommonalityException;
}