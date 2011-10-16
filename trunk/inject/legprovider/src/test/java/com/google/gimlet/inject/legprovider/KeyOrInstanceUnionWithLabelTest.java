// Copyright 2011 Google Inc.  All Rights Reserved 

package com.google.gimlet.inject.legprovider;

import com.google.common.collect.Lists;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import junit.framework.TestCase;

import java.util.Collections;
import java.util.List;

/**
 * Tests the {@link KeyOrInstanceUnionWithLabel} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class KeyOrInstanceUnionWithLabelTest extends TestCase {

  public void testGetTypeLiteral_withUnlabledSimpleKey() {
    Key<String> key = Key.get(String.class);
    KeyOrInstanceUnionWithLabel<String> keyOrInstanceUnionWithLabel =
        KeyOrInstanceUnionWithLabel.ofKey(key);
    assertEquals(
        key.getTypeLiteral(), keyOrInstanceUnionWithLabel.getTypeLiteral());
  }

  public void testGetTypeLiteral_withLabledSimpleKey() {
    Key<String> key = Key.get(String.class);
    KeyOrInstanceUnionWithLabel<String> keyOrInstanceUnionWithLabel =
        KeyOrInstanceUnionWithLabel.ofKey(key, "non-empty-label");
    assertEquals(
        key.getTypeLiteral(), keyOrInstanceUnionWithLabel.getTypeLiteral());
  }

  public void testGetTypeLiteral_withUnlabledComplexKey() {
    Key<List<String>> key = Key.get(new TypeLiteral<List<String>>(){});
    KeyOrInstanceUnionWithLabel<List<String>> keyOrInstanceUnionWithLabel =
        KeyOrInstanceUnionWithLabel.ofKey(key);
    assertEquals(
        key.getTypeLiteral(), keyOrInstanceUnionWithLabel.getTypeLiteral());
  }

  public void testGetTypeLiteral_withLabledComplexKey() {
    Key<List<String>> key = Key.get(new TypeLiteral<List<String>>(){});
    KeyOrInstanceUnionWithLabel<List<String>> keyOrInstanceUnionWithLabel =
        KeyOrInstanceUnionWithLabel.ofKey(key, "non-empty-label");
    assertEquals(
        key.getTypeLiteral(), keyOrInstanceUnionWithLabel.getTypeLiteral());
  }

  public void testGetTypeLiteral_withUnlabledSimpleInstance() {
    String instance = "";
    KeyOrInstanceUnionWithLabel<String> keyOrInstanceUnionWithLabel =
        KeyOrInstanceUnionWithLabel.ofInstance(instance);
    assertEquals(
        TypeLiteral.get(String.class),
        keyOrInstanceUnionWithLabel.getTypeLiteral());
  }

  public void testGetTypeLiteral_withLabledSimpleInstance() {
    String instance = "";
    KeyOrInstanceUnionWithLabel<String> keyOrInstanceUnionWithLabel =
        KeyOrInstanceUnionWithLabel.ofInstance(instance, "non-empty-label");
    assertEquals(
        TypeLiteral.get(String.class),
        keyOrInstanceUnionWithLabel.getTypeLiteral());
  }

  // TODO(ffaber): re-add these in when there is logic that allows us to give
  // a TypeLiteral explicitly because inference is plain broken.

  public void NO_testGetTypeLiteral_withUnlabledComplexInstance() {
    List<String> instance = Collections.emptyList();
    KeyOrInstanceUnionWithLabel<List<String>> keyOrInstanceUnionWithLabel =
        KeyOrInstanceUnionWithLabel.ofInstance(instance);
    assertEquals(
        new TypeLiteral<List<String>>(){},
        keyOrInstanceUnionWithLabel.getTypeLiteral());
  }

  public void NO_testGetTypeLiteral_withLabledComplexInstance() {
    List<String> instance = Collections.emptyList();
    KeyOrInstanceUnionWithLabel<List<String>> keyOrInstanceUnionWithLabel =
        KeyOrInstanceUnionWithLabel.ofInstance(instance, "non-empty-label");
    assertEquals(
        new TypeLiteral<List<String>>(){},
        keyOrInstanceUnionWithLabel.getTypeLiteral());
  }
}