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

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Returns a {@link ClassGraph} that represents the class hierarchy, starting at
 * a given source class.
 * <p>
 * The returned graph includes links to all parent classes and implemented
 * interfaces.  Visiting the graph breadth-first follows this ordering for each
 * node:
 * <ol>
 *  <li> the parent class of the node
 *  <li> all implemented interfaces, in the order in which each appears in
 *       the {@code implements} clause
 * </ol>
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
class GetClassGraph implements Function<Class<?>, ClassGraph> {

  private static final Logger logger =
      Logger.getLogger(GetClassGraph.class.getCanonicalName());

  @Override public ClassGraph apply(Class<?> source) {
    ClassGraph classGraph = new ClassGraph();
    Set<Class<?>> alreadyVisited = Sets.newHashSet();
    return buildGraph(source, alreadyVisited, classGraph);
  }

  ClassGraph buildGraph(
      Class<?> source,
      Set<Class<?>> alreadyVisited,
      ClassGraph classGraph) {

    // Stop here if we've already visited the node.
    if (alreadyVisited.contains(source)) {
      logger.fine("Already visited node: " + source.getSimpleName());
      return classGraph;
    }

    // Add all parents of the node to the graph as immediate parents.
    Iterable<Class<?>> parents = getParents(source);
    for (Class<?> parent : parents) {
      classGraph.addEdge(source, parent);
    }

    // Mark the node as visited.
    alreadyVisited.add(source);

    // Then visit the parents.
    for (Class<?> parent : parents) {
      buildGraph(parent, alreadyVisited, classGraph);
    }

    return classGraph;
  }

  private ImmutableList<Class<?>> getParents(Class<?> source) {
    ImmutableList.Builder<Class<?>> builder = ImmutableList.builder();

    Class<?> superClass = source.getSuperclass();
    if (superClass != null) {
      builder.add(superClass);
    }

    builder.addAll(Arrays.asList(source.getInterfaces()));

    return builder.build();
  }
}
