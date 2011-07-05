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
 * Tests the {@link ClosestMethodChooser} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
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