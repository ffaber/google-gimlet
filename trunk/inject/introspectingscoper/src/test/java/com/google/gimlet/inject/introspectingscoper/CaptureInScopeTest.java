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

package com.google.gimlet.inject.introspectingscoper;


import junit.framework.TestCase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * Tests the {@link CaptureInScope} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class CaptureInScopeTest extends TestCase {

  /**
   * This method checks that the default value of the
   * {@link CaptureInScope#value()} method is equal to the value referenced
   * by {@link CaptureInScopeConstants#DEFAULT_ANNOTATION_VALUE}.
   */
  @CaptureInScope
  public void testDefaultValuesAreConsistent() throws Exception {
    Method thisTestMethod = CaptureInScopeTest.class.getDeclaredMethod(
        "testDefaultValuesAreConsistent");
    CaptureInScope captureInScopeAnnotation =
        thisTestMethod.getAnnotation(CaptureInScope.class);
    assertEquals(
        CaptureInScopeConstants.DEFAULT_ANNOTATION_VALUE,
        captureInScopeAnnotation.value());
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.PARAMETER)
  private @interface TestAnnotation { }

  /**
   * This method checks that  {@link CaptureInScope#value()} method overrides
   * its default value when given an argument.
   */
  @CaptureInScope(TestAnnotation.class)
  public void testValueMethod() throws Exception {
    Method thisTestMethod = CaptureInScopeTest.class.getDeclaredMethod(
        "testValueMethod");
    CaptureInScope captureInScopeAnnotation =
        thisTestMethod.getAnnotation(CaptureInScope.class);
    assertEquals(
        TestAnnotation.class,
        captureInScopeAnnotation.value());
  }
}