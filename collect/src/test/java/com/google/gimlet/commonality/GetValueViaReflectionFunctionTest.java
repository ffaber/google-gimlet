// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.commonality;

import junit.framework.TestCase;

/**
 * Tests the {@link GetValueViaReflectionFunction} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class GetValueViaReflectionFunctionTest extends TestCase {

  private static class HasNoArgGetValueMethod {
    private final Integer value;

    HasNoArgGetValueMethod(Integer value) {
      this.value = value;
    }

    public Integer getValue() {
      return value;
    }
  }

  private static final class HasSingleArgGetValueMethod {
    Integer getValue(Integer arg) {
      return arg;
    }
  }

  private static final class DoesNotHaveGetValueMethod { }

  private GetValueViaReflectionFunction getValueFunction;

  @Override protected void setUp() throws Exception {
    super.setUp();
    getValueFunction = new GetValueViaReflectionFunction("getValue");
  }

  public void testGetValue_withWellFormedMethod() throws Exception {
    assertEquals(3, getValueFunction.apply(new HasNoArgGetValueMethod(3)));
    assertEquals(1, getValueFunction.apply(new HasNoArgGetValueMethod(1)));
    assertEquals(0, getValueFunction.apply(new HasNoArgGetValueMethod(0)));
  }

  public void testGetValue_withSingleArgMethod() throws Exception {
    try {
      getValueFunction.apply(new HasSingleArgGetValueMethod());
      fail();
    } catch (Exception e) {
      // expected
    }
  }

  public void testGetValue_withClassThatHasNoGetValueMethod() throws Exception {
    try {
      getValueFunction.apply(new DoesNotHaveGetValueMethod());
      fail();
    } catch (Exception e) {
      // expected
    }
  }
}