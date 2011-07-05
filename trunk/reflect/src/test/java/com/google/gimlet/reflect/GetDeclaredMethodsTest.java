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