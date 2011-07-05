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