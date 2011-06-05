// Copyright 2009 Google Inc.  All Rights Reserved 

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