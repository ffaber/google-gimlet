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

package com.google.gimlet.reflect;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.Lists;
import com.google.gimlet.collect.DefaultingMaps;

import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * This is a minimally functional graph class that is specifically (and
 * probably exclusively) useful to store the relationships among classes in
 * a type hierarchy.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public final class ClassGraph {

  private final Map<Class<?>, List<Class<?>>> nodesToParents
      = DefaultingMaps.newListValuedMap();

  public boolean containsNode(Class<?> node) {
    return nodesToParents.containsKey(node);
  }

  public void addEdge(Class<?> from, Class<?> to) {
    List<Class<?>> parents = nodesToParents.get(from);
    checkArgument(
        parents.add(to),
        "Edge from %s to %s already exists within %s", from, to, parents);
  }

  public List<Class<?>> getNodesViaBreadthFirstSearch(Class<?> start) {
    checkArgument(
        containsNode(start),
        "Node represented by %s is not within graph %s", start, this);

    List<Class<?>> breadthFirstVisits = Lists.newLinkedList();
    Queue<Class<?>> nodesToVisit = Lists.newLinkedList();

    nodesToVisit.offer(start);
    breadthFirstVisits.add(start);

    while (!nodesToVisit.isEmpty()) {
      Class<?> node = nodesToVisit.remove();
      List<Class<?>> parents = nodesToParents.get(node);
      for (Class<?> parent : parents) {
        if (!breadthFirstVisits.contains(parent)) {
          breadthFirstVisits.add(parent);
          nodesToVisit.offer(parent);
        }
      }
    }

    return breadthFirstVisits;
  }

  @Override public String toString() {
    return "Graph contents are: " + nodesToParents;
  }
}
