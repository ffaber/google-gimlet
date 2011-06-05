// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.reflect.testing.subpackage;

import com.google.gimlet.reflect.testing.ClassDefinitionHolderTestCase.TestAbstractClass;

/**
 * This class exists for sake of testing behavior of a class that exists
 * outside of the package in which it is used.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class TestingSubpackageTestClass
    extends TestAbstractClass {

  @Override
  public void publicInterfaceMethod() { }

  @Override
  public void overriddenAbstractClassMethod(Integer unused) { }

  private void testingSubpackageTestClassPrivateMethod() { }
}
