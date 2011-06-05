// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.reflect;

import static com.google.gimlet.testing.tl4j.GimletAsserts.assertContentsInOrder;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gimlet.reflect.testing.ClassDefinitionHolderTestCase;
import com.google.gimlet.reflect.testing.subpackage.TestingSubpackageTestClass;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Tests the {@link GetClosestVisibleMethods} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class GetClosestVisibleMethodsTest
    extends ClassDefinitionHolderTestCase {

  private GetClosestVisibleMethods getClosestVisibleMethods;

  @Override protected void setUp() throws Exception {
    super.setUp();
    getClosestVisibleMethods = new GetClosestVisibleMethods();
  }

  public void testGetVisibleMethods() throws Exception {
    List<Method> actualVisibleMethods = Lists.newArrayList(
        Iterables.filter(
            getClosestVisibleMethods.apply(TestingSubpackageTestClass.class),
            NOT_DECLARED_ON_OBJECT_PREDICATE));

    assertContentsInOrder(
        actualVisibleMethods,
        childImplementationOfOverriddenAbstractClassMethod,
        protectedParentMethod,
        childImplementationOfPublicInterfaceMethod,
        publicParentMethod,
        childPrivateMethod);
  }
}