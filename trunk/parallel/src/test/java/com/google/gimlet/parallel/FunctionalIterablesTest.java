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

import static com.google.gimlet.testing.tl4j.JUnitAsserts.assertContentsInOrder;

import com.google.common.collect.ImmutableList;

import junit.framework.TestCase;

import java.util.concurrent.Callable;

/**
 * Tests the {@link FunctionalIterables} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class FunctionalIterablesTest extends TestCase {

  public void testCollectCallables() throws Exception {
    Callable<Integer> callable1 = newCallable(1);
    Callable<Integer> callable2 = newCallable(2);

    Iterable<Callable<Integer>> callableIterable =
        ImmutableList.of(callable1, callable2);

    Callable<Iterable<Integer>> iterableCallable =
        FunctionalIterables.collectCallables(callableIterable);

    assertContentsInOrder(iterableCallable.call(), 1, 2);
  }

  private static Callable<Integer> newCallable(final Integer value) {
    return new Callable<Integer>() {
      @Override
      public Integer call() throws Exception {
        return value;
      }
    };
  }
}