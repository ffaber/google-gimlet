// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.inject.introspectingscoper;

import com.google.gimlet.inject.nestedscope.NestedScoped;

import junit.framework.TestCase;

import java.lang.annotation.Annotation;

/**
 * Tests the {@link IntrospectingScopeOutOfScopeProvider} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class IntrospectingScopeOutOfScopeProviderTest extends TestCase {

  public void testErrorMessage() {
    Class targetClass = String.class;
    Class<? extends Annotation> annotationClass = NestedScoped.class;
    IntrospectingScopeOutOfScopeProvider provider =
        new IntrospectingScopeOutOfScopeProvider(targetClass, annotationClass);

    try {
      provider.get();
      fail("Should have thrown an exception");
    } catch (UnsupportedOperationException uoe) {
      String exceptionMessage = uoe.getMessage();
      assertEquals(
          "Class String expected to be bound in scope NestedScoped.",
          exceptionMessage);
    }
  }
}