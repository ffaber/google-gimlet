// Copyright 2011 Google Inc.  All Rights Reserved

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