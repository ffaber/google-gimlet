// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.inject.nestedscope;

import static com.google.common.testing.junit3.JUnitAsserts.assertContainsRegex;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.easymock.EasyMock.expect;

import com.google.inject.BindingAnnotation;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import junit.framework.TestCase;

import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * This class provides test methods for the {@link NestedScopeInterceptor}
 * class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class NestedScopeInterceptorTest extends TestCase {

  NestedScopeInterceptor nestedScopeInterceptor;
  Provider<BindingFrame> bindingFrameProvider;
  NestedScopeImpl nestedScopeImpl;

  // TODO(ffaber): replace with a mock controller.
  interface YetToBeImplementedMockController {
    void replayAll();
    void verifyAll();
    <T> T createMock(Class<? extends T> clazz);
  }

  YetToBeImplementedMockController mocks;

  @Override protected void setUp() throws Exception {
    super.setUp();
    bindingFrameProvider = new SimpleBindingFrameProvider();
    nestedScopeImpl = new NestedScopeImpl(bindingFrameProvider);
    nestedScopeInterceptor = new NestedScopeInterceptor();
    nestedScopeInterceptor.initialize(nestedScopeImpl, bindingFrameProvider);
  }

  public void testAddUniqueKey() {
    mocks.replayAll();

    BindingFrame bindingFrame = new BindingFrame();
    Key<String> key = Key.get(String.class);
    String value = "scoped_value";

    assertNull(
        "The frame contains a scoped value for the key: " + key,
        bindingFrame.get(key));

    nestedScopeInterceptor.addUniqueKey(key, value, bindingFrame);
    assertEquals(
        "The scoped value was returned correctly from the bindingFrame",
        value,
        bindingFrame.get(key));

    try {
      nestedScopeInterceptor.addUniqueKey(key, value, bindingFrame);
      fail("Expected an IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }
    mocks.verifyAll();
  }

  public void testScopedMethodArguments() throws Exception {
    Method scopedMethod = getClass().getMethod(
        "scopedMethod",
        String.class,
        String.class,
        String.class,
        String.class,
        Integer.class,
        Integer.class);

    String namedButNotCaptured = "namedButNotCaptured";
    String capturedButNotNamed = "capturedButNotNamed";
    String capturedAndNamed = "capturedAndNamed";
    String capturedAndNamedToo = "capturedAndNamedToo";
    Integer capturedInteger = 0;
    Integer notCapturedInteger = 0;

    Object[] methodArguments = {
        namedButNotCaptured,
        capturedButNotNamed,
        capturedAndNamed,
        capturedAndNamedToo,
        capturedInteger,
        notCapturedInteger
    };

    MethodInvocation methodInvocation =
        mockMethodInvocation(scopedMethod, methodArguments);

    mocks.replayAll();
    BindingFrame bindingFrame = new BindingFrame();

    nestedScopeInterceptor.scopeMethodArguments(methodInvocation, bindingFrame);

    Key<String> namedButNotCapturedKey =
        Key.get(String.class, Names.named("not_captured"));
    Key<String> capturedButNotNamedKey = Key.get(String.class);
    Key<String> capturedAndNamedKey =
        Key.get(String.class, Names.named("captured"));
    Key<String> capturedAndNamedTooKey =
        Key.get(String.class, Names.named("captured_too"));
    Key<Integer> capturedIntegerKey = Key.get(Integer.class);

    // We expect not to have scoped an argument that was not captured.
    assertNull(
        "The value that should not have been captured was correctly ignored",
        bindingFrame.get(namedButNotCapturedKey));
    assertEquals(
        "The unnamed value was captured correctly",
        capturedButNotNamed,
        bindingFrame.get(capturedButNotNamedKey));
    assertEquals(
        "The named value was captured correctly",
        capturedAndNamed,
        bindingFrame.get(capturedAndNamedKey));
    assertEquals(
        "The second named value was captured correctly",
        capturedAndNamedToo,
        bindingFrame.get(capturedAndNamedTooKey));

    // There were two integers given; we check that we scoped the right one.
    assertEquals(capturedInteger, bindingFrame.get(capturedIntegerKey));

    mocks.verifyAll();
  }

  @SuppressWarnings({ "UnusedDeclaration" })
  @NestedScoped public void scopedMethod(
      @Named("not_captured") String namedButNotCaptured,
      @CaptureInNestedScope String capturedButNotNamed,
      @Named("captured") @CaptureInNestedScope String capturedAndNamed,
      @CaptureInNestedScope @Named("captured_too") String capturedAndNamedToo,
      @CaptureInNestedScope Integer capturedInteger,
      Integer notCapturedInteger) {
  }

  public void testScopedMethodThrowsException() throws Throwable {
    Method scopedMethod =
        getClass().getMethod("simpleScopedMethod", String.class);
    Object[] methodArguments = { "simpleScopedMethodArgument" };

    MethodInvocation methodInvocation =
        mocks.createMock(MethodInvocation.class);
    expect(methodInvocation.getMethod()).andStubReturn(scopedMethod);
    expect(methodInvocation.getArguments()).andStubReturn(methodArguments);
    expect(methodInvocation.proceed()).andThrow(new RuntimeException());

    mocks.replayAll();
    BindingFrame bindingFrame = new BindingFrame();

    nestedScopeInterceptor.scopeMethodArguments(methodInvocation, bindingFrame);

    // If the method throws an exception, we should still have exited the scope.
    assertNull(
        "We expect that there are no frames on the stack",
        nestedScopeImpl.getBindingFrameStack().peek());
  }

  @SuppressWarnings({ "UnusedDeclaration" })
  @NestedScoped
  public void simpleScopedMethod(
      @CaptureInNestedScope String tokenParameter) {
  }


  public void testScopedMethodArguments_collidingKeys() throws Exception {
    Method illegallyScopedMethod = getClass().getMethod(
        "illegallyScopedMethod",
        String.class,
        String.class);

    Object[] methodArguments = { "captured1", "captured2" };

    MethodInvocation methodInvocation =
        mockMethodInvocation(illegallyScopedMethod, methodArguments);

    mocks.replayAll();
    BindingFrame bindingFrame = new BindingFrame();

    try {
      nestedScopeInterceptor.scopeMethodArguments(
          methodInvocation, bindingFrame);
      fail("Expected to see an exception from colliding keys");
    } catch (IllegalArgumentException iae) {
      // expected
    }

    mocks.verifyAll();
  }

  @SuppressWarnings({ "UnusedDeclaration" })
  @NestedScoped public void illegallyScopedMethod(
      @CaptureInNestedScope String captured1,
      @CaptureInNestedScope String captured2) {
  }

  public void testScopedMethodArguments_notAnnotated() throws Exception {
    Method notAnnotatedMethod = getClass().getMethod(
        "notAnnotatedMethod", String.class);

    Object[] methodArguments = { "captured1" };

    MethodInvocation methodInvocation =
        mockMethodInvocation(notAnnotatedMethod, methodArguments);

    mocks.replayAll();
    BindingFrame bindingFrame = new BindingFrame();

    try {
      nestedScopeInterceptor.scopeMethodArguments(
          methodInvocation, bindingFrame);
      fail("Expected to see an exception for lack of @NestedScoped");
    } catch (IllegalArgumentException iae) {
      JUnitAsserts.assertContainsRegex(
          "Unscoped method passed to the NestedScopeInterceptor",
          iae.getMessage());
    }
    mocks.verifyAll();
  }

  @SuppressWarnings({ "UnusedDeclaration" })
  public void notAnnotatedMethod(
      @CaptureInNestedScope String captured1) {
  }

  public void testScopedMethodArguments_tooManyBindingAnnotations()
      throws Exception {
    Method tooManyAnnotationsMethod = getClass().getMethod(
        "tooManyBindingAnnotations", String.class);

    Object[] methodArguments = { "captured1" };

    MethodInvocation methodInvocation =
        mockMethodInvocation(tooManyAnnotationsMethod, methodArguments);

    mocks.replayAll();
    BindingFrame bindingFrame = new BindingFrame();

    try {
      nestedScopeInterceptor.scopeMethodArguments(
          methodInvocation, bindingFrame);
      fail("Expected to see an exception because the method has a parameter " +
          "that has two BindingAnnotations on it.");
    } catch (IllegalArgumentException iae) {
      JUnitAsserts.assertContainsRegex(
          "There must be no more than one binding annotation",
          iae.getMessage());
    }
    mocks.verifyAll();
  }

  @Retention(RUNTIME)
  @Target({ ElementType.PARAMETER })
  @BindingAnnotation
  /** A test binding annotation to use when testing multiple annotations. */
  public static @interface Named2 {
    String value();
  }

  @SuppressWarnings({ "UnusedDeclaration" })
  @NestedScoped public void tooManyBindingAnnotations(
      @CaptureInNestedScope
      @Named("binding_annotation_1")
      @Named2("binding_annotation_2")
      String captured1) {
  }

  /**
   * Uses the given {@link Method} and array of arguments to create a mock
   * {@link MethodInvocation}.
   */
  private MethodInvocation mockMethodInvocation(
      Method method, Object[] methodArguments) {
    MethodInvocation methodInvocation =
        mocks.createMock(MethodInvocation.class);
    expect(methodInvocation.getMethod()).andStubReturn(method);
    expect(methodInvocation.getArguments()).andStubReturn(methodArguments);
    return methodInvocation;
  }
}
