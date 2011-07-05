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

import junit.framework.TestCase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * Tests the {@link AnnotationPresentPredicate} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class AnnotationPresentPredicateTest extends TestCase {

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface TestAnnotation { }

  interface TestInterface {

    @TestAnnotation
    void annotatedMethod();

    void unannotatedMethod();
  }

  private AnnotationPresentPredicate annotationPresentPredicate;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    annotationPresentPredicate =
        new AnnotationPresentPredicate(TestAnnotation.class);
  }

  public void testAnnotationPresent_isPresent() throws Exception {
    Method annotatedMethod =
        TestInterface.class.getMethod("annotatedMethod");
    assertTrue(annotationPresentPredicate.apply(annotatedMethod));
  }

  public void testAnnotationPresent_isNotPresent() throws Exception {
    Method unannotatedMethod =
        TestInterface.class.getMethod("unannotatedMethod");
    assertFalse(annotationPresentPredicate.apply(unannotatedMethod));
  }
}