// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.commonality.subpackage;

/**
 * Provides a simple implementation of {@link TestItem}.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public final class SimpleTestItem implements TestItem {

  private final String value;

  public static SimpleTestItem of(String value) {
    return new SimpleTestItem(value);
  }

  private SimpleTestItem(String value) {
    this.value = value;
  }

  @Override public String getTestValue() {
    return value;
  }
}
