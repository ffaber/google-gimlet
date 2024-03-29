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

package com.google.gimlet.inject.nestedscope;

import com.google.inject.Key;

import junit.framework.TestCase;

/**
 * This class provides tests for the {@link BindingFrame} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class BindingFrameTest extends TestCase {

  /** Tests simple scope lookups. */
  public void testScoping() {
    BindingFrame bindingFrame = new BindingFrame();
    Key<String> key = Key.get(String.class);
    bindingFrame.put(key, "hello");
    assertEquals("hello", bindingFrame.get(key));

    bindingFrame.put(key, "goodbye");
    assertEquals("goodbye", bindingFrame.get(key));

    assertNull(bindingFrame.get(Key.get(Integer.class)));
  }
}
