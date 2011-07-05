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

import static com.google.gimlet.commonality.CommonalityDetectors.detectCommonalityIntrospectively;
import static com.google.gimlet.commonality.CommonalityDetectors.detectCommonalityWithFunction;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.gimlet.commonality.subpackage.GetValueFromTestItemFunction;
import com.google.gimlet.commonality.subpackage.SimpleTestItem;
import com.google.gimlet.commonality.subpackage.TestItem;

import junit.framework.TestCase;


/**
 * Tests the {@link CommonalityDetectors} class.
 * <p>
 * This class primarily tests that the methods on {@link CommonalityDetector}
 * return the objects or perform the behavior that each promises.  It performs
 * some simple tests to do this.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class CommonalityDetectorsTest extends TestCase {

  private final static String TEST_VALUE = "test-value";
  private final static String METHOD_NAME = "getTestValue";

  private final Function<TestItem, String> VALUE_EXTRACTOR =
      new GetValueFromTestItemFunction();

  private final Iterable<TestItem> TEST_ITEMS =
      ImmutableList.<TestItem>of(SimpleTestItem.of(TEST_VALUE));

  public void testForFunction() {
    CommonalityDetector<TestItem, String> commonalityDetector =
        CommonalityDetectors.forFunction(VALUE_EXTRACTOR);
    assertEquals(TEST_VALUE, commonalityDetector.detectCommonality(TEST_ITEMS));
  }

  public void testNewReflexiveCommonalityDetector() {
    CommonalityDetector<TestItem, String> commonalityDetector =
        CommonalityDetectors.newReflexiveCommonalityDetector(METHOD_NAME);
    assertEquals(TEST_VALUE, commonalityDetector.detectCommonality(TEST_ITEMS));
  }
  
  public void testDetectCommonalityWithFunction() {
    assertEquals(
        TEST_VALUE,
        detectCommonalityWithFunction(TEST_ITEMS, VALUE_EXTRACTOR));
  }

  public void testDetectCommonalityIntrospectively() {
    assertEquals(
        TEST_VALUE, detectCommonalityIntrospectively(METHOD_NAME, TEST_ITEMS));
  }
}

