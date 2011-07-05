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

import junit.framework.TestCase;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Tests the {@link ExecutionStrategies} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class ExecutionStrategiesTest extends TestCase {

  public void testObtain() throws Exception {
    Function<Future<Integer>, Callable<Integer>> futureToCallableTransform =
        ExecutionStrategies.obtain();
    Future<Integer> future = Futures.immediateFuture(1);
    Callable<Integer> callable = futureToCallableTransform.apply(future);
    assertEquals(1, callable.call().intValue());
  }
}