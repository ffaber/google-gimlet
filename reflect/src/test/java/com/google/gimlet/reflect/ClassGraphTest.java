// Copyright 2011 Google Inc.  All Rights Reserved 

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