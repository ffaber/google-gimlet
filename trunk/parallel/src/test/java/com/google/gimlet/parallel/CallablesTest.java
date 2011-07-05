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