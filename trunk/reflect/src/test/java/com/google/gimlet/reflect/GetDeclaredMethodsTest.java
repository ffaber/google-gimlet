// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.reflect;

import static com.google.gimlet.testing.tl4j.GimletAsserts.assertContentsAnyOrder;

import junit.framework.TestCase;

import java.lang.reflect.Method;

/**
 * Tests the {@link GetDeclaredMethods} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class GetDeclaredMethodsTest extends TestCase {

  interface ParentTestInterface {
    void parentMethod();
  }

  interface ChildTestInterface extends ParentTestInterface {
    void childMethod1();

    void childMethod2();
  }

  private GetDeclaredMethods getDeclaredMethods;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    getDeclaredMethods = new GetDeclaredMethods();
  }

  public void testGetMethods_parentIterface() throws Exception {
    Method parentMethod = ParentTestInterface.class.getMethod("parentMethod");
    assertContentsAnyOrder(
        getDeclaredMethods.apply(ParentTestInterface.class), parentMethod);
  }

  public void testGetMethods_childIterface() throws Exception {
    Method method1 = ChildTestInterface.class.getMethod("childMethod1");
    Method method2 = ChildTestInterface.class.getMethod("childMethod2");
    assertContentsAnyOrder(
        getDeclaredMethods.apply(ChildTestInterface.class),
        method1, method2);
  }
}