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

import static com.google.gimlet.testing.tl4j.JUnitAsserts.assertContentsInOrder;

import com.google.inject.Key;
import com.google.inject.name.Names;

import junit.framework.TestCase;

/**
 * This class provides tests for the {@link BindingFrame} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class BindingFrameStackTest extends TestCase {

  private BindingFrameStack bindingFrameStack;
  private BindingFrame bindingFrame1;
  private BindingFrame bindingFrame2;

  @Override protected void setUp() throws Exception {
    super.setUp();

    bindingFrameStack = new BindingFrameStack();
    bindingFrame1 = new BindingFrame();
    bindingFrame2 = new BindingFrame();
  }

  /** Tests simple push and pop behaviors. */
  public void testPushAndPop() {
    bindingFrameStack.push(bindingFrame1);
    bindingFrameStack.push(bindingFrame2);

    BindingFrame poppedFrame = bindingFrameStack.pop();
    assertEquals(bindingFrame2, poppedFrame);

    poppedFrame = bindingFrameStack.pop();
    assertEquals(bindingFrame1, poppedFrame);
  }

  /** Tests that the iterator functionality works as expected. */
  public void testIterator() {
    assertContentsInOrder(bindingFrameStack.getBindingFrames());

    bindingFrameStack.push(bindingFrame1);
    assertContentsInOrder(
        bindingFrameStack.getBindingFrames(),
        bindingFrame1);

    bindingFrameStack.push(bindingFrame2);
    assertContentsInOrder(
        bindingFrameStack.getBindingFrames(),
        bindingFrame2, bindingFrame1);

    bindingFrameStack.pop();
    assertContentsInOrder(
        bindingFrameStack.getBindingFrames(),
        bindingFrame1);

    bindingFrameStack.pop();
    assertContentsInOrder(bindingFrameStack.getBindingFrames());
  }

  private void assertLookup(Key<?> key, Object expectedValue) {
    assertEquals(
        "We expect to see the correct value for the key " + key,
        expectedValue,
        bindingFrameStack.lookup(key));
  }

  @SuppressWarnings({ "AssertEqualsBetweenInconvertibleTypes" })
  public void testLookup_simpleNesting() {
    Key<String> stringKey = Key.get(String.class);
    Key<Integer> integerKey = Key.get(Integer.class);
    BindingFrame firstBindingFrame = new BindingFrame();
    firstBindingFrame.put(stringKey, "first");
    firstBindingFrame.put(integerKey, 10);
    bindingFrameStack.push(firstBindingFrame);

    assertLookup(stringKey, "first");
    assertLookup(integerKey, 10);

    BindingFrame secondBindingFrame = new BindingFrame();
    secondBindingFrame.put(stringKey, "second");
    bindingFrameStack.push(secondBindingFrame);

    assertLookup(stringKey, "second");
    assertLookup(integerKey, 10);

    bindingFrameStack.pop();

    assertLookup(stringKey, "first");
    assertLookup(integerKey, 10);
  }

  public void testLookup_differentKeys() {
    Key<String> onlyClassKey = Key.get(String.class);
    Key<String> annotatedKey =
        Key.get(String.class, Names.named("unique_name"));

    BindingFrame firstBindingFrame = new BindingFrame();
    firstBindingFrame.put(onlyClassKey, "first_class_only");
    firstBindingFrame.put(annotatedKey, "first_annotated");
    bindingFrameStack.push(firstBindingFrame);

    assertLookup(onlyClassKey, "first_class_only");
    assertLookup(annotatedKey, "first_annotated");

    BindingFrame secondBindingFrame = new BindingFrame();
    secondBindingFrame.put(annotatedKey, "second_annotated");
    bindingFrameStack.push(secondBindingFrame);

    assertLookup(onlyClassKey, "first_class_only");
    assertLookup(annotatedKey, "second_annotated");

    bindingFrameStack.pop();

    assertLookup(onlyClassKey, "first_class_only");
    assertLookup(annotatedKey, "first_annotated");
  }
}
