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

package com.google.gimlet.inject.legprovider;

import static com.google.common.collect.Iterables.getOnlyElement;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.PrivateModule;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.InjectionPoint;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is very loosely based on {@code FactoryProvider2}.
 * <p>
 * Its main functionality is how its {@link #bindTo(Key)} method returns a
 * {@link Module module} that binds a given {@link TypeLiteral} to a
 * particular profile of a target class.  This "profile" is defined by the
 * set of values given as in the {@link LegModuleBuilder} DSL.
 *
 * @author andrei.g.matveev@gmail.com [original]
 * @author ffaber@gmail.com [refactor to SPI]
 */
class LegBinder<T> {

  private final TypeLiteral<? extends T> implementationType;
  private final Map<Key<?>, KeyOrInstanceUnionWithLabel<?>>
      declaredKeysToActualBindings = Maps.newHashMap();

  LegBinder(
      TypeLiteral<? extends T> implementationType,
      Set<KeyOrInstanceUnionWithLabel<?>> valueSet) {
    this.implementationType = implementationType;

    Set<InjectionPoint> allInjectionPoints = Sets.newHashSet();
    allInjectionPoints.add(InjectionPoint.forConstructorOf(implementationType));
    allInjectionPoints.addAll(
        InjectionPoint.forInstanceMethodsAndFields(implementationType));
    allInjectionPoints.addAll(
        InjectionPoint.forStaticMethodsAndFields(implementationType));

    // For all injection points we match up the "declared" types on the
    // @Foot-annotated class with the "actual" types to use.
    for (InjectionPoint injectionPoint : allInjectionPoints) {
      List<Dependency<?>> dependencies = injectionPoint.getDependencies();
      for (Dependency<?> dependency : dependencies) {
        Key<?> declaredKey = dependency.getKey();
        Class<? extends Annotation> annotationClass =
            declaredKey.getAnnotationType();
        if (annotationClass != Foot.class) {
          continue;
        }

        Foot foot = (Foot) declaredKey.getAnnotation();
        String declaredLabel = foot.value();
        TypeLiteral<?> declaredTypeLiteral = declaredKey.getTypeLiteral();

        // For providers, we don't use "Provider" as the type, because we want
        // to use the type of the thing a Provider provides as the type.
        TypeLiteral<?> nonProviderDeclaredTypeLiteral =
            stripProviderType(declaredTypeLiteral);
        Key<?> declaredKeyToUse = Key.get(nonProviderDeclaredTypeLiteral, foot);

        for (KeyOrInstanceUnionWithLabel<?> actualBinding :
            Sets.newHashSet(valueSet)) {
          TypeLiteral<?> actualTypeLiteral = actualBinding.getTypeLiteral();
          String actualLabel = actualBinding.label;

          if (actualTypeLiteral.equals(nonProviderDeclaredTypeLiteral)
              && declaredLabel.equals(actualLabel)) {
            declaredKeysToActualBindings.put(declaredKeyToUse, actualBinding);
            valueSet.remove(actualBinding);
            break;
          }
        }
      }
    }

    if (!valueSet.isEmpty()) {
      throw new IllegalStateException(
          "Too many binding definitions given.  Excessive bindings are: "
          + valueSet);
    }
  }

  /**
   * This method returns the given {@code declaredTypeLiteral} in all cases
   * except that in which the raw type of the type literal is
   * {@code Provider}, in which case this method returns a {@link TypeLiteral}
   * that represents the actual type argument of the provider.
   */
  private TypeLiteral<?> stripProviderType(TypeLiteral<?> declaredTypeLiteral) {
    Class<?> clazz = declaredTypeLiteral.getRawType();
    final TypeLiteral<?> nonProviderDeclaredTypeLiteral;
    if (clazz.equals(Provider.class)
        || clazz.equals(javax.inject.Provider.class)) {
      ParameterizedType providerType =
          (ParameterizedType) declaredTypeLiteral.getType();
      Type underlyingType = getOnlyElement(
          Arrays.asList(providerType.getActualTypeArguments()));
      nonProviderDeclaredTypeLiteral = TypeLiteral.get(underlyingType);
    } else {
      nonProviderDeclaredTypeLiteral = declaredTypeLiteral;
    }
    return nonProviderDeclaredTypeLiteral;
  }

  Module bindTo(final Key<T> returnValueKey) {
    return new PrivateModule() {
      @SuppressWarnings({ "unchecked", "RedundantCast" })
      // raw keys are necessary for the args array and return value
      @Override protected void configure() {
        Binder binder = binder();

        for (Key<?> configKey : declaredKeysToActualBindings.keySet()) {
          KeyOrInstanceUnionWithLabel<?> actualBinding =
              declaredKeysToActualBindings.get(configKey);

          if (actualBinding.key != null) {
            binder.bind((Key) configKey).to(actualBinding.key);
          } else {
            binder.bind((Key) configKey).toInstance(actualBinding.instance);
          }
        }

        // We don't use .toConstructor() to support users of Guice 2.0
        if (returnValueKey.equals(Key.get(implementationType))) {
          binder.bind(returnValueKey);
        } else {
          binder.bind(returnValueKey).to(implementationType);
        }

        expose(returnValueKey);
      }
    };
  }
}
