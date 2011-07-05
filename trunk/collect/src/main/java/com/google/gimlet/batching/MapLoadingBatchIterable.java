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

import static com.google.common.collect.Iterables.getOnlyElement;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.gimlet.parallel.CallableTransform;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper classes that facilitates loading {@link KeyedBatch keyed batches}.
 * <p>
 * This is useful when trying to load all of foo for a given bar for all bars
 * from disk.
 *
 * @param <K> the type of "bar" that are being loaded.
 * @param <V> the type of "foos" that are being loaded.
 * @param <I> the type of id for bars so that the next id can be determined.
 */
public abstract class MapLoadingBatchIterable<K, V, I>
    extends ListLoadingBatchIterable<I, KeyedBatch<K, V>> {

  private static final Logger logger = 
      Logger.getLogger(MapLoadingBatchIterable.class.getCanonicalName());

  /** Function that converts a map entry to a batch element. */
  private final Function<Entry<K, List<V>>, KeyedBatch<K, V>>
      mapEntryToBatchElementFunction =
      new Function<Entry<K, List<V>>, KeyedBatch<K, V>>() {
        @Override public KeyedBatch<K, V> apply(Entry<K, List<V>> from) {
          return KeyedBatch.of(
              from.getKey(), Batches.forCollection(from.getValue()));
        }
      };

  private final Integer batchSize;

  /**
   * @param initialValue an initial value to represent the first "bar" to load.
   * @param loadCallableTransform transform that gets applied when
   * {@link #loadNextBatch} get called within its own callable. This allows one
   * to decorate this call.
   * @param batchSize The max number of "foos" that get returned for a given
   */
  protected MapLoadingBatchIterable(
      I initialValue,
      CallableTransform loadCallableTransform,
      Integer batchSize) {
    super(initialValue, loadCallableTransform);
    this.batchSize = batchSize;
  }

  /**
   * Loads a batch of entries starting with the given beginIdForBatch. The sum
   * of the sizes of values for enries should at most be batch size. One does
   * not need to worry if the "last" K (bar) according to the ordering is
   * incomplete or not. It will automatically be removed and the
   * nextIdFunction will be applied to the previous element which should result
   * in the last "K" being loaded.
   */
  protected abstract Map<K, List<V>> loadBatch(I beginIdForBatch,
      Integer batchSize);

  @Override protected final List<KeyedBatch<K, V>> loadNextBatch(
      I beginIdForBatch) {
    Map<K, List<V>> map = loadBatch(beginIdForBatch, batchSize);

    // exit early if we didn't load any entries.
    if (map.isEmpty()) {
      return ImmutableList.of();
    }

    List<KeyedBatch<K, V>> keyedBatches =
        Lists.newArrayList(Iterables.transform(
            map.entrySet(), mapEntryToBatchElementFunction));

    Collections.sort(keyedBatches,
        // sort the list based in the key of the KeyedBatches.
        getOrdering().onResultOf(
            new Function<KeyedBatch<K, V>, K>() {
              @Override
              public K apply(KeyedBatch<K, V> from) {
                return from.getKey();
              }
            }));

    // if there is only one element, make sure that this one key doesn't
    // have too many values. if it does log an exception. We will also just
    // return this single element list.

    if (keyedBatches.size() == 1) {
      KeyedBatch<K, V> keyedBatch = getOnlyElement(keyedBatches);
      if (keyedBatch.getBatch().size() >= batchSize) {
        logger.log(Level.SEVERE, String.format(
            "%s has gone over batch size %s", keyedBatch.getKey(), batchSize),
            new Exception());
      }
      return keyedBatches;
    }

    // since there is more than one entry in the map, we remove the last entry
    // so that we may load it again since it may have not been complete.
    keyedBatches.remove(keyedBatches.size() - 1);
    return keyedBatches;
  }

  /**
   * Returns an ordering for how to sort the map keys so that we can return
   * them in the correct order.
   */
  protected abstract Ordering<K> getOrdering();

  /**
   * Returns the nextId that should be use to load the next batch, based on
   * the last key that was part of the previous batch.
   */
  protected abstract I innerGetNextId(K previousEnd);

  @Override protected I getNextId(KeyedBatch<K, V> previousEnd) {
    return innerGetNextId(previousEnd.getKey());
  }
}
