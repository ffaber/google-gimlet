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

import com.google.common.collect.Iterables;
import com.google.gimlet.reflect.testing.ClassDefinitionHolderTestCase;
import com.google.gimlet.reflect.testing.subpackage.TestingSubpackageTestClass;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Tests the {@link VisibleMethodsPredicate} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class VisibleMethodsPredicateTest
    extends ClassDefinitionHolderTestCase {

  public void testGetMethods_testingSubpackageClass() throws Exception {
    VisibleMethodsPredicate visibleMethodsPredicate =
        new VisibleMethodsPredicate(TestingSubpackageTestClass.class);

    Iterable<Method> actualAllowedMethods =
        Iterables.filter(
            Iterables.concat(
                Arrays.asList(
                    TestingSubpackageTestClass.class.getDeclaredMethods()),
                Arrays.asList(TestAbstractClass.class.getDeclaredMethods()),
                Arrays.asList(TestInterface.class.getDeclaredMethods()),
                Arrays.asList(UnrelatedTestClass.class.getDeclaredMethods())),
            visibleMethodsPredicate);

    assertContentsAnyOrder(
        actualAllowedMethods,
        publicInterfaceMethod,
        protectedParentMethod,
        publicParentMethod,
        parentImplementationOfOverriddenAbstractClassMethod,
        childImplementationOfOverriddenAbstractClassMethod,
        childImplementationOfPublicInterfaceMethod,
        childPrivateMethod);
  }

  public void testGetMethods_abstractParentClass() throws Exception {
    VisibleMethodsPredicate visibleMethodsPredicate =
        new VisibleMethodsPredicate(TestAbstractClass.class);

    Iterable<Method> actualAllowedMethods =
        Iterables.filter(
            Iterables.concat(
                Arrays.asList(
                    TestingSubpackageTestClass.class.getDeclaredMethods()),
                Arrays.asList(TestAbstractClass.class.getDeclaredMethods()),
                Arrays.asList(TestInterface.class.getDeclaredMethods()),
                Arrays.asList(UnrelatedTestClass.class.getDeclaredMethods())),
            visibleMethodsPredicate);

    assertContentsAnyOrder(
        actualAllowedMethods,
        publicInterfaceMethod,
        protectedParentMethod,
        publicParentMethod,
        packagePrivateParentMethod,
        privateParentMethod,
        parentImplementationOfOverriddenAbstractClassMethod,
        childImplementationOfOverriddenAbstractClassMethod,
        childImplementationOfPublicInterfaceMethod,
        unrelatedProtectedMethod);
  }

  /**
   * This tests that a protected method within an unrelated class (i.e., not
   * part of the same class hierarchy), and within a different package is not
   * visible.
   */
  public void testUnrelatedProtectedMethod() throws Exception {
    VisibleMethodsPredicate visibleMethodsPredicate =
        new VisibleMethodsPredicate(TestingSubpackageTestClass.class);
    assertFalse(visibleMethodsPredicate.apply(unrelatedProtectedMethod));
  }
}