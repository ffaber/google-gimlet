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

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.MoreExecutors;

import junit.framework.TestCase;

import java.util.concurrent.Callable;

/**
 * Tests the {@link DefaultExecutionStrategy} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class DefaultExecutionStrategyTest extends TestCase {

  public void testGetParallelTransform() throws Exception {
    internalTestGetParallelTransform(
        ExecutionStrategies.sameThreadStrategy());
    internalTestGetParallelTransform(
        ExecutionStrategies.singleThreadStrategy("test"));
    internalTestGetParallelTransform(
        ExecutionStrategies.executorServiceStrategy(
            MoreExecutors.sameThreadExecutor()));
  }

  /** Tests the {@link ExecutionStrategy#getParallelTransform()} logic. */
  private void internalTestGetParallelTransform(
      ExecutionStrategy executionStrategy) throws Exception {
    Function<Callable<Integer>, Callable<Integer>> parTransform =
        executionStrategy.getParallelTransform();
    Callable<Integer> testCallable = Callables.callableFor(1);
    Callable<Integer> trasformedCallable = parTransform.apply(testCallable);
    assertEquals(1, trasformedCallable.call().intValue());
  }

  public void testGetParallelMapTransform() throws Exception {
    internalTestGetParallelMapTransform(
        ExecutionStrategies.sameThreadStrategy());
    internalTestGetParallelMapTransform(
        ExecutionStrategies.singleThreadStrategy("test"));
    internalTestGetParallelMapTransform(
        ExecutionStrategies.executorServiceStrategy(
            MoreExecutors.sameThreadExecutor()));
  }

  /** Tests {@link ExecutionStrategy#getParallelMapTransform(Function)} ()} */
  private void internalTestGetParallelMapTransform(
      ExecutionStrategy executionStrategy) throws Exception {
    // toStringFunction() is a Function<Object, String>, but because
    // getParallelMapTransform() is defined without wildcards, we need to
    // pretend it's only a Function<Integer, String> (which is safe)
    @SuppressWarnings("unchecked")
    Function<Integer, String> toString
        = (Function) Functions.toStringFunction();
    Function<Iterable<Integer>, Callable<Iterable<String>>> parMapTransform =
        executionStrategy.getParallelMapTransform(toString);

    Iterable<Integer> integers = ImmutableList.of(1, 2, 3);
    Iterable<String> transformedIntegers =
        parMapTransform.apply(integers).call();

    assertContentsInOrder(transformedIntegers, "1", "2", "3");
  }
}