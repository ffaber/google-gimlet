// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.reflect;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import junit.framework.TestCase;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;

/**
 * Tests the {@link DefaultMethodInvoker} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class DefaultMethodInvokerTest extends TestCase {

  private static final Integer INITIAL_COUNTER_VALUE = 0;

  static class TestClass {

    private final AtomicInteger counter =
        new AtomicInteger(INITIAL_COUNTER_VALUE);

    Integer incrementCounter() {
      return counter.incrementAndGet();
    }

    Void returnsNull() {
      return null;
    }
  }
  
  private TestClass testClassInstance;
  private Iterable<Method> incrementCounterMethodIterable;
  private Method returnsNullMethod;
  private Iterable<Method> returnsNullMethodIterable;
  private DefaultMethodInvoker defaultMethodInvoker;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    testClassInstance = new TestClass();
    Method incrementCounterMethod =
        TestClass.class.getDeclaredMethod("incrementCounter");
    incrementCounterMethodIterable = ImmutableList.of(incrementCounterMethod);

    returnsNullMethod =
        TestClass.class.getDeclaredMethod("returnsNull");
    returnsNullMethodIterable = ImmutableList.of(returnsNullMethod);

    defaultMethodInvoker = new DefaultMethodInvoker();
  }

  public void testInvokeMethod() {
    Integer counterValue = (Integer) Iterables.getOnlyElement(
        defaultMethodInvoker.invokeMethods(
            incrementCounterMethodIterable, testClassInstance).values());
    assertEquals(INITIAL_COUNTER_VALUE + 1, counterValue.intValue());

    counterValue = (Integer) Iterables.getOnlyElement(
        defaultMethodInvoker.invokeMethods(
            incrementCounterMethodIterable, testClassInstance).values());
    assertEquals(INITIAL_COUNTER_VALUE + 2, counterValue.intValue());
  }

  public void testInvalidMethod() throws Exception {
    // We need a method that doesn't exist on the TestClass instance.
    Iterable<Method> invalidMethodIterable =
        ImmutableList.of(ImmutableList.class.getMethod("size"));

    // We expect a RuntimeException to be thrown when trying to invoke it.
    try {
      defaultMethodInvoker.invokeMethods(
          invalidMethodIterable, testClassInstance);
      fail("Expected to see a RuntimeException");
    } catch (RuntimeException e) {
      // Expected
    }
  }

  /** Tests that a null method return values doesn't cause a NPE */
  public void testNullReturnValuesHandledOk() {
    Map<Method, Object> invocationResults = defaultMethodInvoker.invokeMethods(
        returnsNullMethodIterable, testClassInstance);
    
    Map<Method, Object> expectedResults =
        Maps.newHashMap();
    expectedResults.put(returnsNullMethod, null);

    assertEquals(expectedResults, invocationResults);
  }
}