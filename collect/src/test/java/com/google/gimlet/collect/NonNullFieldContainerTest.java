// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.collect;

import junit.framework.TestCase;

/**
 * Tests the {@link NonNullFieldContainer} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class NonNullFieldContainerTest extends TestCase {

  static class TestNonNullFieldContainer extends NonNullFieldContainer {
    Integer integerField;
    String stringField;
  }

  private TestNonNullFieldContainer nonNullFieldContainer;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    nonNullFieldContainer = new TestNonNullFieldContainer();
  }

  public void testCheckInitialized_oneFieldIsNullByDefault() {
    nonNullFieldContainer.integerField = 0;
    try {
      nonNullFieldContainer.checkInitialized();
      fail();
    } catch (NullPointerException npe) {
      // TODO(ffaber): add testing method as implied below
      // assertHasChainedMessageThatContains("stringField", npe);
    }
  }

  public void testCheckInitialized_oneFieldIsNullExplicitly() {
    nonNullFieldContainer.integerField = null;
    nonNullFieldContainer.stringField = "hello";
    try {
      nonNullFieldContainer.checkInitialized();
      fail();
    } catch (NullPointerException npe) {
      // TODO(ffaber): add testing method as implied below
      // assertHasChainedMessageThatContains("integerField", npe);
    }
  }

  public void testCheckInitialized_noNullFields() {
    nonNullFieldContainer.integerField = 1;
    nonNullFieldContainer.stringField = "hello";
    nonNullFieldContainer.checkInitialized();
  }
}