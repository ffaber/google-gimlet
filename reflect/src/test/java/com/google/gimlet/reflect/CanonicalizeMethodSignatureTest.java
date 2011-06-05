// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.reflect;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import junit.framework.TestCase;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Tests the {@link CanonicalizeMethodSignature} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class CanonicalizeMethodSignatureTest extends TestCase {

  static abstract class TestClass {

    private String privateMethod() { return null; }

    void packagePrivateMethod(Integer value) { }

    abstract String abstractPackagePrivateMethod();

    protected Integer protectedMethod() { return null; }

    public final void publicNoArgMethod() { }

    public void publicWithArgMethod(String string) { }

    public static synchronized void staticMethod() { }
  }

  static final String PACKAGE =
      CanonicalizeMethodSignatureTest.class.getPackage().getName();

  static final String TEST_CLASS_FQN = TestClass.class.getSimpleName();

  CanonicalizeMethodSignature canonicalizeMethodSignature;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    canonicalizeMethodSignature = new CanonicalizeMethodSignature();
  }

  public void testPrivateMethod() throws Exception {
    innerTestSignature("privateMethod");
  }
  
  public void testPackageProtectedMethod() throws Exception {
    innerTestSignature("packagePrivateMethod", Integer.class);
  }

  public void testAbstractPackagePrivateMethod() throws Exception {
    innerTestSignature("abstractPackagePrivateMethod");
  }

  public void testProtectedMethod() throws Exception {
    innerTestSignature("protectedMethod");
  }

  public void testPublicNoArgSignature() throws Exception {
    innerTestSignature("publicNoArgMethod");
  }

  public void testPublicWithArgSignature() throws Exception {
    innerTestSignature("publicWithArgMethod", String.class);
  }

  public void testStaticMethod() throws Exception {
    innerTestSignature("staticMethod");
  }

  private void innerTestSignature(
      String methodName, Class<?>... argTypes)
      throws Exception {
    Method method = TestClass.class.getDeclaredMethod(methodName, argTypes);
    String signature = canonicalizeMethodSignature.apply(method);
    String expectedSignature = String.format(
        "%s(%s)", methodName,  Joiner.on(",").join(toClassNames(argTypes)));
    assertEquals(
        "Expected signature: " + expectedSignature,
        expectedSignature, signature);
  }

  private Iterable<String> toClassNames(Class<?>... classes) {
    return Lists.transform(
        Arrays.asList(classes),
        new Function<Class<?>, String>() {
          @Override
          public String apply(Class<?> from) {
            return from.getName();
          }
        });
  }
}