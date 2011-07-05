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

package com.google.gimlet.commonality;

import static org.easymock.EasyMock.expect;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.gimlet.commonality.CommonalityDetector.InconsistentCommonalityException;
import com.google.gimlet.commonality.subpackage.GetValueFromTestItemFunction;
import com.google.gimlet.commonality.subpackage.TestItem;
import com.google.gimlet.testing.easymock.Mocca;

import junit.framework.TestCase;

import java.util.Collection;


/**
 * Tests the {@link FunctionDrivenCommonalityDetector} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class FunctionDrivenCommonalityDetectorTest extends TestCase {

  private static final String VALUE_1 = "value1";
  private static final String VALUE_2 = "value2";

  public static final Function<TestItem, String> GET_VALUE_FUNCTION =
      new GetValueFromTestItemFunction();

  TestItem testItem1;
  TestItem testItem2;
  TestItem testItem3;
  Collection<TestItem> testItems;

  private Mocca mocca;

  private FunctionDrivenCommonalityDetector<TestItem, String>
      functionDrivenCommonalityDetector;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mocca = new Mocca();

    testItem1 = mocca.createMock(TestItem.class);
    testItem2 = mocca.createMock(TestItem.class);
    testItem3 = mocca.createMock(TestItem.class);
    testItems = ImmutableList.of(testItem1, testItem2, testItem3);

    functionDrivenCommonalityDetector =
        new FunctionDrivenCommonalityDetector<TestItem, String>(
            GET_VALUE_FUNCTION);
  }

  public void testDetectCommonality_emptyInput() {
    try {
      functionDrivenCommonalityDetector.detectCommonality(
          ImmutableList.<TestItem>of());
      fail();
    } catch (IllegalArgumentException iae) {
      // expected
    }
  }

  public void testDetectCommonality_differentCommonalities() {
    expect(testItem1.getTestValue()).andStubReturn(VALUE_1);
    expect(testItem2.getTestValue()).andStubReturn(VALUE_2);
    expect(testItem3.getTestValue()).andStubReturn(VALUE_1);

    mocca.replayAll();
    try {
      functionDrivenCommonalityDetector.detectCommonality(testItems);
      fail();
    } catch (InconsistentCommonalityException ise) {
      // expected
    }

    mocca.verifyAll();
  }

  @SuppressWarnings("RedundantStringConstructorCall") // for "new String()" 
  public void testDetectCommonality_sameCommonalities() {
    expect(testItem1.getTestValue()).andReturn(VALUE_1).times(2);
    expect(testItem2.getTestValue()).andReturn(VALUE_1).once();
    // This ensures == would fail if used within the function.
    expect(testItem3.getTestValue()).andReturn(new String(VALUE_1)).once();

    mocca.replayAll();

    String value =
        functionDrivenCommonalityDetector.detectCommonality(testItems);
    assertEquals("value1", value);

    mocca.verifyAll();
  }
}