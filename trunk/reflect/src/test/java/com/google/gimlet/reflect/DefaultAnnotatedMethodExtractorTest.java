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

import static com.google.gimlet.testing.tl4j.GimletAsserts.assertContentsInOrder;

import com.google.common.collect.ImmutableList;

import junit.framework.TestCase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * Tests the {@link DefaultAnnotatedMethodExtractor} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class DefaultAnnotatedMethodExtractorTest extends TestCase {

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface TestAnnotation { }

  interface TestParentInterface {
    @TestAnnotation
    void annotatedParentMethod();

    void unannotatedParentMethod();

    @TestAnnotation
    void overriddenAnnotatedMethod();

    void overriddenUnannotatedMethod();

    @TestAnnotation
    void overriddenInitiallyAnnotatedMethod();

    void overriddenInitiallyUnannotatedMethod();
  }

  interface TestChildInterface extends TestParentInterface {
    @TestAnnotation
    void annotatedChildMethod();

    void unannotatedChildMethod();

    @Override @TestAnnotation
    void overriddenAnnotatedMethod();

    @Override
    void overriddenUnannotatedMethod();

    @Override
    void overriddenInitiallyAnnotatedMethod();

    @Override @TestAnnotation
    void overriddenInitiallyUnannotatedMethod();
  }

  private DefaultAnnotatedMethodExtractor defaultAnnotatedMethodExtractor;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    defaultAnnotatedMethodExtractor = new DefaultAnnotatedMethodExtractor();
  }

  public void testAllAnnotatedMethods() throws Exception {
    ImmutableList<Method> annotatedMethods =
        defaultAnnotatedMethodExtractor.extractAllAnnotatedMethods(
            TestChildInterface.class, TestAnnotation.class);
    assertContentsInOrder(
        annotatedMethods,
        TestChildInterface.class.getMethod("annotatedChildMethod"),
        TestChildInterface.class.getMethod("annotatedParentMethod"),
        TestChildInterface.class.getMethod("overriddenAnnotatedMethod"),
        TestParentInterface.class.getMethod(
            "overriddenInitiallyAnnotatedMethod"),
        TestChildInterface.class.getMethod(
            "overriddenInitiallyUnannotatedMethod"));
  }
}