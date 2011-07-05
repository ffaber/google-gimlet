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

import com.google.common.base.Throwables;
import com.google.gimlet.inject.nestedscope.NestedScope;
import com.google.gimlet.inject.nestedscope.testing.TestNestedScope;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.ScopeAnnotation;

import junit.framework.TestCase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Test the {@link IntrospectingBinder} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class IntrospectingBinderTest extends TestCase {

  private static final Provider<?> OUT_OF_SCOPE_PROVIDER =
      new Provider() {
        @Override
        public Object get() {
          throw new UnsupportedOperationException();
        }
      };

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  @ScopeAnnotation
  @interface TestScoped { }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.PARAMETER)
  @BindingAnnotation
  @interface ReturnsIntegerValue { }

  private static final Key<String> STRING_KEY = Key.get(String.class);

  private static final Key<Integer> INTEGER_KEY =
      Key.get(Integer.class, ReturnsIntegerValue.class);

  static class IntrospectionTarget {

    @CaptureInScope
    String getStringValue() {
      throw new UnsupportedOperationException();
    }

    @CaptureInScope(ReturnsIntegerValue.class)
    Integer getIntegerValue() {
      throw new UnsupportedOperationException();
    }
  }

  private NestedScope testScope;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    IntrospectingBinder.clearKeys();
    testScope = new TestNestedScope();
  }

  public void testBindIntrospectivelyInScope() {
    Module module = new AbstractModule() {
      @Override
      protected void configure() {
        // The module is created to bind all elements annotated with
        // CaptureInScope.
        IntrospectingBinder introspectingBinder =
            IntrospectingBinder.newIntrospectingBinder(binder());
        introspectingBinder.bindIntrospectivelyInScope(
            IntrospectionTarget.class,
            TestScoped.class);

        bindScope(TestScoped.class, testScope);
      }
    };
    innerTestBindIntrospectively(module);
  }

  public void testBindIntrospectively() {
    Module module = new AbstractModule() {
      @Override
      protected void configure() {
        // The module is created to bind all elements annotated with
        // CaptureInScope.
        IntrospectingBinder introspectingBinder =
            IntrospectingBinder.newIntrospectingBinder(binder());
        introspectingBinder.bindIntrospectively(
            IntrospectionTarget.class,
            TestScoped.class,
            OUT_OF_SCOPE_PROVIDER);

        bindScope(TestScoped.class, testScope);
      }
    };
    innerTestBindIntrospectively(module);
  }

  private void innerTestBindIntrospectively(Module overrideModuleToUse) {
    Injector injector = Guice.createInjector(overrideModuleToUse);

    testScope.enterNew();

    // We expect to have introspectively bound these keys
    assertNotNull(injector.getBinding(STRING_KEY));
    assertNotNull(injector.getBinding(INTEGER_KEY));

    try {
      // Since no value exists in scope, we expect an exception to be thrown
      // by the OutOfScope provider when we request a value for the string key.
      injector.getInstance(STRING_KEY);
    } catch (ProvisionException pe) {
      Throwable rootCause = Throwables.getRootCause(pe);
      assertTrue(
          "Cause class is unexpected: " + rootCause.getClass()
              + " with trace:\n" + Throwables.getStackTraceAsString(rootCause),
          rootCause instanceof UnsupportedOperationException);
    }

    // Once values exist in scope, we expect the injector to return them.
    String stringValue = "StringValue";
    testScope.put(STRING_KEY, stringValue);
    assertSame(stringValue, injector.getInstance(STRING_KEY));

    Integer integerValue = 33;
    testScope.put(INTEGER_KEY, integerValue);
    assertSame(integerValue, injector.getInstance(INTEGER_KEY));
  }
}
