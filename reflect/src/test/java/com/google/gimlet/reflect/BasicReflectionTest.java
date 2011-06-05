// Copyright 2011 Google Inc.  All Rights Reserved 

package com.google.gimlet.reflect;

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
        assertEquals("Expect only one: " + annotations, 1, annotations.size());
        assertSame(MerryMethodToCall.class, annotations.get(0).getClass());
      }
    }
  }


}