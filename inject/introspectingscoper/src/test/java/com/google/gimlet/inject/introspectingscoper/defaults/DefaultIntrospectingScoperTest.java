// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.inject.introspectingscoper.defaults;

import static org.easymock.EasyMock.expect;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gimlet.inject.introspectingscoper.CaptureInScope;
import com.google.gimlet.inject.nestedscope.testing.TestNestedScope;
import com.google.gimlet.reflect.AnnotatedMethodExtractor;
import com.google.gimlet.reflect.MethodInvoker;
import com.google.gimlet.testing.easymock.Mocca;
import com.google.inject.BindingAnnotation;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import junit.framework.TestCase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;


/**
 * Tests the {@link DefaultIntrospectingScoper} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class DefaultIntrospectingScoperTest extends TestCase {

  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.FIELD, ElementType.PARAMETER})
  @BindingAnnotation
  public @interface ForUseInKey { }

  static class MethodHolder {

    static final Integer RETURNS_INTEGER_RESULT = 3;
    static final String RETURNS_STRING_RESULT = "some_string";
    static final Set<Integer> RETURNS_SET_OF_INTEGER_RESULT =
        ImmutableSet.of(1, 11, 3);

    @CaptureInScope
    Void returnsNull() {
      return null;
    }

    @CaptureInScope
    Integer returnsInteger() {
      return RETURNS_INTEGER_RESULT;
    }

    @CaptureInScope(ForUseInKey.class)
    String returnsString() {
      return RETURNS_STRING_RESULT;
    }

    @CaptureInScope Set<Integer> returnsSetOfIntegers() {
      return RETURNS_SET_OF_INTEGER_RESULT;
    }
  }

  private Mocca mocks;

  private Method returnsNullMethod;
  private Method returnsStringAndHasNonDefaultAnnotationMethod;
  private Method returnsIntegerMethod;
  private Method returnsSetOfIntegersMethod;

  private AnnotatedMethodExtractor annotatedMethodExtractor;
  private MethodInvoker methodInvoker;
  private TestNestedScope unitOfWorkScope;
  private DefaultIntrospectingScoper defaultIntrospectingScoper;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mocks = new Mocca();

    returnsNullMethod = MethodHolder.class.getDeclaredMethod("returnsNull");
    returnsStringAndHasNonDefaultAnnotationMethod =
        MethodHolder.class.getDeclaredMethod("returnsString");
    returnsIntegerMethod =
        MethodHolder.class.getDeclaredMethod("returnsInteger");
    returnsSetOfIntegersMethod =
        MethodHolder.class.getDeclaredMethod("returnsSetOfIntegers");

    annotatedMethodExtractor = mocks.createMock(AnnotatedMethodExtractor.class);
    methodInvoker = mocks.createMock(MethodInvoker.class);
    unitOfWorkScope = new TestNestedScope();
    defaultIntrospectingScoper = new DefaultIntrospectingScoper(
        annotatedMethodExtractor,
        methodInvoker,
        unitOfWorkScope);
  }

  /**
   * This method invokes the scoper and verifies that the expected scoped
   * objects are actually found.  It's a touch long but it's straighforward.
   */
  public void testIntrospectAndScope() throws Exception {
    ImmutableList<Method> methodsToReturn = ImmutableList.of(
        returnsNullMethod,
        returnsStringAndHasNonDefaultAnnotationMethod,
        returnsIntegerMethod,
        returnsSetOfIntegersMethod);
    expect(annotatedMethodExtractor.extractAllAnnotatedMethods(
        MethodHolder.class, CaptureInScope.class))
        .andReturn(methodsToReturn)
        .once();

    MethodHolder methodHolder = new MethodHolder();

    Map<Method, Object> invocationResults = Maps.newHashMap();
    invocationResults.put(returnsNullMethod, null);
    invocationResults.put(
        returnsIntegerMethod, MethodHolder.RETURNS_INTEGER_RESULT);
    invocationResults.put(
        returnsStringAndHasNonDefaultAnnotationMethod,
        MethodHolder.RETURNS_STRING_RESULT);
    invocationResults.put(
        returnsSetOfIntegersMethod,
        MethodHolder.RETURNS_SET_OF_INTEGER_RESULT);

    expect(methodInvoker.invokeMethods(methodsToReturn, methodHolder))
        .andReturn(invocationResults)
        .once();

    mocks.replayAll();

    defaultIntrospectingScoper.scopeIntrospectively(methodHolder);

    Map<Key<?>, Object> expectedScopedObjects = Maps.newHashMap();
    expectedScopedObjects.put(Key.get(Void.class), null);
    expectedScopedObjects.put(
        Key.get(Integer.class), MethodHolder.RETURNS_INTEGER_RESULT);
    expectedScopedObjects.put(
        Key.get(String.class, ForUseInKey.class),
        MethodHolder.RETURNS_STRING_RESULT);
    expectedScopedObjects.put(
        Key.get(new TypeLiteral<Set<Integer>>(){}),
        MethodHolder.RETURNS_SET_OF_INTEGER_RESULT);

    assertEquals(expectedScopedObjects, unitOfWorkScope.getScopedObjects());
    mocks.verifyAll();
  }
}