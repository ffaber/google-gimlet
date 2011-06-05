// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.reflect.testing;

import com.google.common.base.Predicate;
import com.google.gimlet.reflect.testing.subpackage.TestingSubpackageTestClass;

import junit.framework.TestCase;

import java.lang.reflect.Method;

/**
 * Provides a configuration of test classes that are convenient to reference
 * in subclasses.
 * 
 * @author ffaber@gmail.com (Fred Faber)
 */
public abstract class ClassDefinitionHolderTestCase extends TestCase {

  /**
   * This predicate rejects methods that are declared on {@link Object}
   * (which includes methods like {@link Object#hashCode()},
   * {@link Object#toString()}, etc). 
   */
  protected static final Predicate<Method> NOT_DECLARED_ON_OBJECT_PREDICATE =
      new Predicate<Method>() {
        public boolean apply(Method input) {
          return !input.getDeclaringClass().equals(Object.class);
        }
      };

  /**
   * The test class hierarchy is this
   *             TestInterface  (in this package)
   *                  ^
   *                  |
   *             TestAbstractClass (in this package)
   *                 ^
   *                 |
   *      TestingSubpackageTestClass (in another package)
   *
   *                  AND
   *
   *            UnrelatedTestClass (in this package)
   */

  protected interface TestInterface {
    void publicInterfaceMethod();
  }

  public static abstract class TestAbstractClass implements TestInterface {
    private void privateParentMethod() { }

    void packagePrivateParentMethod() { }

    protected void protectedParentMethod() { }

    public void publicParentMethod() { }

    public void overriddenAbstractClassMethod(Integer unused) { }
  }

  protected class UnrelatedTestClass {
    protected void unrelatedProtectedMethod() { }
  }

  protected Method publicInterfaceMethod;
  protected Method privateParentMethod;
  protected Method packagePrivateParentMethod;
  protected Method protectedParentMethod;
  protected Method publicParentMethod;
  protected Method childPrivateMethod;
  protected Method parentImplementationOfOverriddenAbstractClassMethod;
  protected Method childImplementationOfPublicInterfaceMethod;
  protected Method childImplementationOfOverriddenAbstractClassMethod;
  protected Method unrelatedProtectedMethod;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    publicInterfaceMethod =
        TestInterface.class.getDeclaredMethod("publicInterfaceMethod");
    privateParentMethod =
        TestAbstractClass.class.getDeclaredMethod("privateParentMethod");
    packagePrivateParentMethod =
        TestAbstractClass.class.getDeclaredMethod("packagePrivateParentMethod");
    protectedParentMethod =
        TestAbstractClass.class.getDeclaredMethod("protectedParentMethod");
    publicParentMethod =
        TestAbstractClass.class.getDeclaredMethod("publicParentMethod");
    childPrivateMethod =
        TestingSubpackageTestClass.class.getDeclaredMethod(
            "testingSubpackageTestClassPrivateMethod");
    parentImplementationOfOverriddenAbstractClassMethod = 
        TestAbstractClass.class.getDeclaredMethod(
            "overriddenAbstractClassMethod", Integer.class);
    childImplementationOfPublicInterfaceMethod =
        TestingSubpackageTestClass.class.getDeclaredMethod(
            "publicInterfaceMethod");
    childImplementationOfOverriddenAbstractClassMethod =
        TestingSubpackageTestClass.class.getDeclaredMethod(
            "overriddenAbstractClassMethod", Integer.class);
    unrelatedProtectedMethod =
        UnrelatedTestClass.class.getDeclaredMethod("unrelatedProtectedMethod");

  }
}
