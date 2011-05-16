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

import java.util.Collection;
import java.util.Iterator;

/**
 * This class contains utility methods to work with {@link Batch} objects.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public final class Batches {
  private Batches() { }

  public static <T> Batch<T> forCollection(final Collection<T> collection) {
    return new Batch<T>() {
      @Override public int size() {
        return collection.size();
      }

      @Override public boolean isEmpty() {
        return collection.isEmpty();
      }

      @Override public boolean contains(Object o) {
        return collection.contains(o);
      }

      @Override public Iterator<T> iterator() {
        return collection.iterator();
      }

      @Override public Object[] toArray() {
        return collection.toArray();
      }

      @Override public <T> T[] toArray(T[] ts) {
        return collection.toArray(ts);
      }

      @Override public boolean add(T t) {
        return collection.add(t);
      }

      @Override public boolean remove(Object o) {
        return collection.remove(o);
      }

      @Override public boolean containsAll(Collection<?> objects) {
        return collection.containsAll(objects);
      }

      @Override public boolean addAll(Collection<? extends T> objects) {
        return collection.addAll(objects);
      }

      @Override public boolean removeAll(Collection<?> objects) {
        return collection.removeAll(objects);
      }

      @Override public boolean retainAll(Collection<?> objects) {
        return collection.retainAll(objects);
      }

      @Override public void clear() {
        collection.clear();
      }
    };
  }
}
