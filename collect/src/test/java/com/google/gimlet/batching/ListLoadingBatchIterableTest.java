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

package com.google.gimlet.batching;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gimlet.collect.GimletMaps;
import com.google.gimlet.parallel.CallableTransforms;
import com.google.gimlet.testing.tl4j.JUnitAsserts;

import junit.framework.TestCase;

import java.util.List;
import java.util.Map;

/**
 * Tests for {@link ListLoadingBatchIterable}.
 *
 */
public class ListLoadingBatchIterableTest extends TestCase {

  public void testWithEmptyBatches() {
    assertWithBatches(ImmutableMap.<String, Integer>of(),
        ImmutableMap.<Integer, List<String>>of(
            0, ImmutableList.<String>of()));
  }

  public void testWithOneBatchOneElement() {
    assertWithBatches(
        ImmutableMap.<String, Integer>of(
            "a", 1),
        ImmutableMap.<Integer, List<String>>of(
            0, ImmutableList.of("a"),    // the first batch has one element
            1, ImmutableList.<String>of()), // second batch as none
        "a");
  }

  public void testWithOneBatchOneElementEmtptyBatch() {
    assertWithBatches(
        ImmutableMap.<String, Integer>of(
            "a", 1,        // end of first batch is a and begin of next is 1
            "c", 4),       // end of second batch is c and next begins with 4
        ImmutableMap.<Integer, List<String>>of(
            0, ImmutableList.of("a"),    // the first batch has one element
            1, ImmutableList.of("c"),    // the second batch has one element
            4, ImmutableList.<String>of()), // third batch has none
        "a", "c");
  }

  public void testMultipleBatchMultipleEntries() {
    assertWithBatches(
        ImmutableMap.<String, Integer>of(  // next id map
            "a", 4,
            "d", 7,
            "g", 8),
        ImmutableMap.<Integer, List<String>>of(
            0, ImmutableList.of("a"),
            4, ImmutableList.of("b", "c", "d"),
            7, ImmutableList.of("e", "g"),
            8, ImmutableList.<String>of()),
        "a", "b", "c", "d", "e", "g");
  }


  private void assertWithBatches(Map<String, Integer> nextIds,
      Map<Integer, List<String>> batches, String... results) {
    TestListLoadingBatchIterable testListLoadingBatchIterable
        = new TestListLoadingBatchIterable(nextIds, batches);

    JUnitAsserts.assertContentsInOrder(
        testListLoadingBatchIterable,
        results);
  }

  // Simple extension of ListLoadingBatchIterable to allow us to test
  // the behavior.
  private static class TestListLoadingBatchIterable extends
      ListLoadingBatchIterable<Integer, String> {

    /**
     * Map containing what should be returned as the next id given the previous
     * end.
     */
    private final Map<String, Integer> nextIds;

    /**
     * Map contains what should be returned as the batch given a "beginning"
     * id.
     */
    private final Map<Integer, List<String>> batches;

    private TestListLoadingBatchIterable(
        Map<String, Integer> nextIds,
        Map<Integer, List<String>> batches) {
      super(0, CallableTransforms.getIdentityTransform());
      // make sure that whenever we ask for a key, it exists.
      this.nextIds = GimletMaps.newEnsureKeyExistsMap(nextIds);
      this.batches = GimletMaps.newEnsureKeyExistsMap(batches);
    }

    @Override protected Integer getNextId(String previousEnd) {
      return nextIds.get(previousEnd);
    }

    @Override protected List<String> loadNextBatch(Integer beginIdForBatch) {
      return batches.get(beginIdForBatch);
    }
  }
}
