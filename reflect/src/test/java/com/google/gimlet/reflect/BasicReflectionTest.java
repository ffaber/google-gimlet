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

import static com.google.gimlet.testing.tl4j.GimletAsserts.assertSize;

import junit.framework.TestCase;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Tests basic reflection.
 * 
 * @author ffaber@gmail.com (Fred Faber)
 */
public class BasicReflectionTest extends TestCase {

  static class GrandparentClass {
    String getName() {
      return "GrandparentClass";
    }
  }

  static class ParentClass extends GrandparentClass {
    String getName() {
      return "ParentClass";
    }
  }

  static class ChildClass extends ParentClass {
    String getName() {
      return "ChildClass";
    }
  }

  public void testMethodInvocation() throws Exception {
    try {
      GrandparentClass.class.getMethod("getName");
      fail("Should not be visible since it's not public");
    } catch (NoSuchMethodException e) {
      // expected
    }
  }

  public void testMethodInvocation2() throws Exception {
    Method grandparentMethod = GrandparentClass.class.getDeclaredMethod(
        "getName");
    ChildClass childClass = new ChildClass();
    assertEquals("ChildClass", grandparentMethod.invoke(childClass));

    Method parentMethod = ParentClass.class.getDeclaredMethod("getName");
    assertEquals("ChildClass", parentMethod.invoke(childClass));
  }

  // --- traversal tests

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  @interface MerryMethodToCall  { }

  interface MerryInterface {
    @MerryMethodToCall void call();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  @interface GrumpyMethodToCall  { }

  interface GrumpyInterface {
    @GrumpyMethodToCall void call();
  }

  interface ApatheticInterface {
    void call();
  }

  static class ImplementingClass
      implements MerryInterface, ApatheticInterface, GrumpyInterface {
    @Override public void call() { }
  }

  public void testGetDeclaredMethodOrder() throws Exception {
    // This call only looks on ImplementingClass for methods. We'd need to
    // traverse the hierarchy to find the methods on the interfaces implemented
    // by the class.
    Method[] methods = ImplementingClass.class.getDeclaredMethods();
    System.err.println("Methods: " + Arrays.toString(methods));
    for (Method method : methods) {
      if (method.getName().equals("call")) {
        List<Annotation> annotations = Arrays.asList(method.getAnnotations());
        assertSize(0, annotations);
      }
    }
  }


}