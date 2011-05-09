// Copyright 2011 Google Inc.  All Rights Reserved

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