// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.reflect;

import static com.google.gimlet.testing.tl4j.GimletAsserts.assertContentsAnyOrder;

import com.google.common.collect.Iterables;
import com.google.gimlet.reflect.testing.ClassDefinitionHolderTestCase;
import com.google.gimlet.reflect.testing.subpackage.TestingSubpackageTestClass;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Tests the {@link ClosestMethodChooser} class.
 *
 * @author ffaber@google.com (Fred Faber)
 */
public class ClosestMethodChooserTest
    extends ClassDefinitionHolderTestCase {

  private ClosestMethodChooser closestMethodChooser;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    closestMethodChooser = new ClosestMethodChooser();
  }

  public void testChoseClosestMethods() {
    Iterable<Method> methods =
        Iterables.concat(
            Arrays.asList(
                TestingSubpackageTestClass.class.getDeclaredMethods()),
            Arrays.asList(TestAbstractClass.class.getDeclaredMethods()),
            Arrays.asList(TestInterface.class.getDeclaredMethods()));

    Iterable<Method> closestMethods =
        closestMethodChooser.choseClosestMethods(methods);

    assertContentsAnyOrder(
        closestMethods,
        privateParentMethod,
        packagePrivateParentMethod,
        protectedParentMethod,
        publicParentMethod,
        childPrivateMethod,
        childImplementationOfOverriddenAbstractClassMethod,
        childImplementationOfPublicInterfaceMethod);
  }
}