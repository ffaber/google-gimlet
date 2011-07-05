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

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

import junit.framework.TestCase;

import java.util.Map;

/**
 * Tests the {@link ClassGraph} class.
 * 
 * @author ffaber@gmail.com (Fred Faber)
 */
public class ClassGraphTest extends TestCase {

  /**
   * This test illustrates how we can use {@link Map#containsKey(Object)} within
   * the {@link ClassGraph#containsNode(Class)} method.
   */
  public void testMapMakerKeySet() {
    Map<String, String> stringMap = new MapMaker().makeComputingMap(
        new Function<String, String>() {
          @Override public String apply(String input) {
            return String.valueOf(input) + "-suffix";
          }
        });
    assertFalse(stringMap.containsKey("anything"));
    // After we call the get, the key will appear in the map.
    stringMap.get("anything");
    assertTrue(stringMap.containsKey("anything"));
  }
}