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

import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.gimlet.parallel.CallableTransform;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Base class that makes it possible to present a flat iterable view of
 * multiple loads that each result in their own list being loaded. Each call to
 * get the iterable will result in a new iterators that starts at the beginning
 * of the "batches" to load.
 *
 * @param <I> represents the type of id that are used in fetching.
 * @param <T> represents the type of objects that being loaded.
 */
public abstract class ListLoadingBatchIterable<I, T>
    implements Iterable<T> {

  private final I initialValue;
  private final CallableTransform loadingCallableTransform;

  /**
   * Constructor.
   *
   * @param initialValue the initial value to use when getting the first batch.
   * @param loadingCallableTransform a transform to apply to the callable which
   * calls {@link #loadNextBatch(Object)}  that so that one may decorate it with
   * specific logic such as loading in a separate thread.
   */
  public ListLoadingBatchIterable(
      I initialValue,
      CallableTransform loadingCallableTransform) {
    this.initialValue = initialValue;
    this.loadingCallableTransform = loadingCallableTransform;
  }

  /**
   * Returns the nextId that will used as the beginning of the next batch given
   * the last entry returned from the current batch.
   */
  protected abstract I getNextId(T previousEnd);

  /** Loads a batch of items starting with beginIdForBatch. */
  protected abstract List<T> loadNextBatch(I beginIdForBatch);

  @Override public final Iterator<T> iterator() {
    return new ListLoadingIterator();
  }

  /**
   * Helper class that allows us to return a new iterator starts the beginning
   * for each call to get the iterator on the Iterable.
   */
  private class ListLoadingIterator extends AbstractIterator<T> {

    /**
     * Holds the next id that we'll use to
     * {@link ListLoadingBatchIterable#loadNextBatch(Object)}.
     */
    I idForNextBatch;

    /**
     * Keeps a reference to the current batch that we're returning entries from.
     * When this iterator gets exhausted, we'll load another batch to replace
     * it.
     */
    private Iterator<T> currentBatch;

    private ListLoadingIterator() {
      idForNextBatch = Preconditions.checkNotNull(
          initialValue, "Can't have a null initial value");
    }

    private Iterator<T> getNextBatch() {
      checkState(idForNextBatch != null,
          "Can't get next batch after an empty batch");
      Callable<List<T>> loadingCallable = new Callable<List<T>>() {
        @Override
        public List<T> call() throws Exception {
          return loadNextBatch(idForNextBatch);
        }
      };

      final List<T> batch;
      try {
        batch = loadingCallableTransform.transform(loadingCallable).call();
      } catch (Exception e) {
        throw new RuntimeException(
            "Could not load batch with id " + idForNextBatch, e);
      }

      if (!batch.isEmpty()) {
        idForNextBatch = getNextId(batch.get(batch.size() - 1));
      } else {
        idForNextBatch = null;
      }
      return batch.iterator();
    }

    @Override protected T computeNext() {
      if (currentBatch == null) {
        currentBatch = getNextBatch();
        if (!currentBatch.hasNext()) {
          // the very first batch was empty.
          return endOfData();
        }
      }
      if (!currentBatch.hasNext()) {
        currentBatch = getNextBatch();
        // we exhausted one batch, and tried to get the next which had none,
        // this means we have reached the end.
        if (!currentBatch.hasNext()) {
          return endOfData();
        }
      }
      return currentBatch.next();
    }
  }
}
