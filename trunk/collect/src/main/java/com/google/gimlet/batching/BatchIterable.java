// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.gimlet.batching;

import java.util.List;

/**
 * An {@link Iterable} that provides its results in batches.
 *
 */
public interface BatchIterable<T> extends Iterable<Batch<? extends T>> {
}
