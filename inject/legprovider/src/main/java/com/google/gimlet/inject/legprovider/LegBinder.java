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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.getOnlyElement;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.PrivateModule;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionPoint;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * This class is very loosely based on {@code FactoryProvider2}.
 * <p>
 * Its main functionality is how its {@link #bindTo(Key)} method returns a
 * {@link Module module} that binds a given {@link TypeLiteral} to a
 * particular profile of a target class.  This "profile" is defined by the
 * set of values given as {@link LabeledKey}s, each of which corresponds to
 * a constructor parameter on the target class that is annotated with
 * {@link Leg}.
 *
 */
class LegBinder<T> {

  private final Constructor constructor;
  private final TypeLiteral<? extends T> implementationType;
  private final Map<Key<?>, KeyOrInstanceUnionWithLabel<?>>
      configKeyToValueUnionMap;

  LegBinder(
      TypeLiteral<? extends T> implementationType,
      Set<KeyOrInstanceUnionWithLabel<?>> valueSet) {
    this.implementationType = implementationType;

    // TODO: Consider implementing support for AssistedInject
    // constructors. The challenge lies in writing code that will find matching
    // constructors based on types and labels.
    // Find a matching constructor
    InjectionPoint ctorInjectionPoint =
        InjectionPoint.forConstructorOf(implementationType);
    this.constructor = (Constructor) checkNotNull(
        ctorInjectionPoint.getMember(),
        "No suitable constructor was found in %s", implementationType);

    // We need to create some configuration keys (aka keys that appear on the
    // constructor of the implementation class).
    this.configKeyToValueUnionMap =
        mapConfigKeysToValueUnions(constructor, valueSet);
  }

  /**
   * This method maps the {@link Key keys} given to the {@code using()} methods
   * of {@link LegModuleBuilder} to the {@link Leg} parameters on the
   * constructor of the target class.
   * <p>
   * Note that this doesn't really need to return the union of <em>labeled</em>
   * keys and instances, because no code beyond this method actually cares
   * about the label.  This is done mainly for re-use of the data structure.
   * If we were more pedantic, this method would return a [key, instance]
   * union.
   */
  private Map<Key<?>, KeyOrInstanceUnionWithLabel<?>> mapConfigKeysToValueUnions(
      Constructor constructor,
      Set<KeyOrInstanceUnionWithLabel<?>> valueSet) {
    Type[] paramTypes = constructor.getGenericParameterTypes();
    Class<?>[] paramClasses = constructor.getParameterTypes();
    Annotation[][] paramAnnotations = constructor.getParameterAnnotations();

    Map<Key<?>, KeyOrInstanceUnionWithLabel<?>> configKeyToValueUnionMap =
        Maps.newHashMap();

    // Extract the configuration keys
    Map<LabeledKey, Key<?>> configLabeledKeyToConfigKeyMap = Maps.newHashMap();
    for (int i = 0; i < paramTypes.length; i++) {
      for (int j = 0; j < paramAnnotations[i].length; j++) {
        if (paramAnnotations[i][j].annotationType().equals(Leg.class)) {
          // If the configuration type is a Provider, then we must make sure
          // to use the type of the Provider instead
          final TypeLiteral<?> configTypeLiteral;
          if (paramClasses[i].equals(Provider.class)
              || paramClasses[i].equals(javax.inject.Provider.class)) {
            checkState(
                paramTypes[i] instanceof ParameterizedType,
                "%s should be a ParameterizedType, but instead is %s",
                paramClasses[i],
                paramTypes[i]);
            ParameterizedType paramType = (ParameterizedType) paramTypes[i];
            configTypeLiteral = TypeLiteral.get(getOnlyElement(
                Arrays.asList(paramType.getActualTypeArguments())));
          } else {
            configTypeLiteral = TypeLiteral.get(paramTypes[i]);
          }

          Leg annotation = (Leg) paramAnnotations[i][j];
          configLabeledKeyToConfigKeyMap.put(
              LabeledKey.of(Key.get(configTypeLiteral), annotation.value()),
              Key.get(configTypeLiteral, annotation));
        }
      }
    }

    // Now pair up configuration keys with value keys
    Set<Key<?>> configKeysForLogging =
        ImmutableSet.copyOf(configLabeledKeyToConfigKeyMap.values());

    checkState(
        configKeysForLogging.size() == valueSet.size(),
        "The number of value keys does not match the number of configuration "
        + "keys. Number of value keys: %s Number of configuration keys: %s",
        configKeysForLogging.size(), valueSet.size());

    for (KeyOrInstanceUnionWithLabel<?> keyOrInstanceUnionWithLabel : valueSet) {
      final LabeledKey<?> configLookupKey;
      if (keyOrInstanceUnionWithLabel.key != null) {
        configLookupKey = LabeledKey.of(
            Key.get(keyOrInstanceUnionWithLabel.key.getTypeLiteral()),
            keyOrInstanceUnionWithLabel.label);
      } else {
        // TODO: we might want to get a little more clever with generic types
        Key<?> key = Key.get(keyOrInstanceUnionWithLabel.instance.getClass());
        configLookupKey = LabeledKey.of(key, keyOrInstanceUnionWithLabel.label);
      }

      Key<?> configKey = checkNotNull(
          configLabeledKeyToConfigKeyMap.get(configLookupKey),
          "\nValue key %s\n"
           + "does not have a corresponding configuration key in:\n"
           + "%s",
          configLookupKey, configKeysForLogging);
      configLabeledKeyToConfigKeyMap.remove(configLookupKey);

      configKeyToValueUnionMap.put(configKey, keyOrInstanceUnionWithLabel);
    }

    checkState(
        configLabeledKeyToConfigKeyMap.isEmpty(),
        "Config keys %s did not have a matching value key in %s",
        configLabeledKeyToConfigKeyMap.values(),
        valueSet);

    return configKeyToValueUnionMap;
  }

  Module bindTo(final Key<T> returnValueKey) {
    return new PrivateModule() {
      @SuppressWarnings({ "unchecked", "RedundantCast" })
      // raw keys are necessary for the args array and return value
      @Override protected void configure() {
        Binder binder = binder();

        for (Key<?> configKey : configKeyToValueUnionMap.keySet()) {
          KeyOrInstanceUnionWithLabel<?> unionWithLabel =
              configKeyToValueUnionMap.get(configKey);

          if (unionWithLabel.key != null) {
            binder.bind((Key) configKey).to(unionWithLabel.key);
          } else {
            binder.bind((Key) configKey).toInstance(unionWithLabel.instance);
          }
        }

        binder.bind(returnValueKey)
            .toConstructor(constructor, (TypeLiteral) implementationType);

        expose(returnValueKey);
      }
    };
  }
}
