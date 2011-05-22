// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.commonality.subpackage;

import com.google.common.base.Function;

/**
 * Test-based class that provides the simple means to extract the value from a
 * {@link TestItem}.
 *
 * @author ffaber@google.com (Fred Faber)
 */
public class GetValueFromTestItemFunction
    implements Function<TestItem, String> {

  @Override public String apply(TestItem testItem) {
    return testItem.getTestValue();
  }
}
