// Copyright 2008 Google Inc.  All Rights Reserved 

package com.google.gimlet.batching;

import java.util.Iterator;
import java.util.List;

/**
 * An implementation of {@link Iterator} that returns its data in batches.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public interface BatchIterator<T> extends Iterator<Batch<? extends T>> { }