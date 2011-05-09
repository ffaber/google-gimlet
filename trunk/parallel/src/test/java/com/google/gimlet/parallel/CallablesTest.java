// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.parallel;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.inject.Provider;
import com.google.inject.util.Providers;

import junit.framework.TestCase;

import java.util.concurrent.Callable;

/**
 * Tests the {@link Callables} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class CallablesTest extends TestCase {

  public void testFromProvider() throws Exception {
    Object providedObject = new Object();
    Provider<Object> provider = Providers.of(providedObject);
    Callable<Object> callable = Callables.fromProvider(provider);
    assertSame(providedObject, callable.call());
  }

  public void testCallableFor() throws Exception {
    Object object = new Object();
    Callable<Object> callable = Callables.callableFor(object);
    assertSame(object, callable.call());
  }

  public void testReturnValueAsCallable() throws Exception {
    Function<Object, String> toString = Functions.toStringFunction();
    Function<Object, Callable<String>> toCallableString =
        Callables.returnValueAsCallable(toString);
    Callable<String> callableString = toCallableString.apply(1);
    assertEquals("1", callableString.call());
  }
}