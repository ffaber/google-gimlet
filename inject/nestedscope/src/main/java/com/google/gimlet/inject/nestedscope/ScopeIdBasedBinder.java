// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.inject.nestedscope;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.util.Types;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;

/**
 * This binder makes it simple to configure {@link ScopeId}-based bindings.
 * Some usage examples are below.
 * <p>
 * The example below assumes that the sample code is part of the configuration
 * for a guice module.
 *
 * Also, we assume that an instance of {@link ScopeId} is instantiated.
 *
 * <pre>
 *       ScopeId SCOPE_ID1 = ...
 * </pre>
 *
 * Example:
 *
 * <pre>
 *       ScopeIdBasedBinder
 *         .newScopeIdBasedBinder(binder(), SCOPE_ID1)
 *         .bind(Foo.class)
 *         .to(FooImpl.class);
 * </pre>
 * <strong> Note that the ESDL of the {@link LinkedBindingBuilder} returned
 * from calls to {@code bind()} is not fully implemented.
 *
 */
public final class ScopeIdBasedBinder {
  /**
   * This set contains the {@link Key}s that have been seen for the
   * configuration of a single {@code Injector}. This set is cleared via
   * static injection once an injector is created.
   */
  private static final Set<Key<?>> existingKeys = Sets.newHashSet();

  /**
   * The key of each entry in this map is a {@code Type} of an element that
   * is the parameterized type of a {@link ScopeIdBasedProvider} that is
   * bound to a {@link Key} given to this binder.  For instance, the type to
   * provide in-scope could be {@code Apple}, and this would correspond to a
   * key of {@code ScopeIdBasedBinder<Apple>}.  Each value is the
   * map of {@link ScopeId} to {@link Key} entries that represent which
   * implementation is provided for a given {@code Type}, under the
   * corresponding {@link ScopeId}.  In this example, it would be a map of the
   * types of {@code Apple} that correspond to each scope.
   */
  private static final Map<Object, Map<ScopeId, Key>> bindingsPerType =
      new MapMaker().makeComputingMap(
          new Function<Object, Map<ScopeId, Key>>() {
            @Override
            public Map<ScopeId, Key> apply(Object from) {
              return Maps.newHashMap();
            }
          });

  private static Boolean needsToRequestInjection = false;

  private final Binder binder;
  private final ScopeId scopeId;

  @SuppressWarnings("ThrowableInstanceNeverThrown")
  public static ScopeIdBasedBinder newScopeIdBasedBinder(
      Binder binder, ScopeId scopeId) {
    Object source = new Throwable().getStackTrace()[1];
    return newScopeIdBasedBinder(binder, scopeId, source);
  }

  public static ScopeIdBasedBinder newScopeIdBasedBinder(
      Binder binder, ScopeId scopeId, Object source) {
    return new ScopeIdBasedBinder(binder, scopeId, source);
  }

  private ScopeIdBasedBinder(Binder binder, ScopeId scopeId, Object source) {
    this.binder = binder.withSource(source);
    this.scopeId = scopeId;
    if (!needsToRequestInjection) {
      needsToRequestInjection = true;
      binder.requestStaticInjection(ScopeIdBasedBinder.class);
    }
  }

  public <T> LinkedBindingBuilder<T> bind(TypeLiteral<T> valueType) {
    return bind(Key.get(valueType));
  }

  public <T> LinkedBindingBuilder<T> bind(
      TypeLiteral<T> valueType, Class<? extends Annotation> annotationType) {
    return bind(Key.get(valueType, annotationType));
  }

  public <T> LinkedBindingBuilder<T> bind(
      Class<T> clazz, Class<? extends Annotation> annotationType) {
    return bind(Key.get(clazz, annotationType));
  }

  public <T> LinkedBindingBuilder<T> bind(Class<T> clazz) {
    return bind(Key.get(clazz));
  }

  @SuppressWarnings({"unchecked"})
  public <T> LinkedBindingBuilder<T> bind(Key<T> key) {
    TypeLiteral<T> typeLiteralToBind = key.getTypeLiteral();

    Key providerKey  = Key.get(
          Types.newParameterizedType(
              ScopeIdBasedProvider.class, typeLiteralToBind.getType()));
    Map<ScopeId, Key> bindingsForScope = bindingsPerType.get(providerKey);

    if (!existingKeys.contains(key)) {
      binder.bind(key).toProvider(
          new ScopeIdBasedProvider(key, bindingsForScope));
      existingKeys.add(key);
    }

    return new CharlieBrownChristmasTreeLinkedBindingBuilder<T>(
        scopeId, bindingsForScope);
  }

  @Inject
  static void injectorCreated() {
    needsToRequestInjection = false;
    existingKeys.clear();
  }

  /**
   * This class represents a paltry facsimile of a real implementation of
   * {@link LinkedBindingBuilder}. A few of its methods are implemented, and
   * over time, we expect it to fill out.  For now, it does the job of
   * satisfying the very fundamental needs of basic scope-id based bindings.
   */
  static class CharlieBrownChristmasTreeLinkedBindingBuilder<T>
      extends UoeScopedBindingBuilder
      implements LinkedBindingBuilder<T> {

    private final ScopeId scopeId;
    private final Map<ScopeId, Key> bindingsForScope;

    CharlieBrownChristmasTreeLinkedBindingBuilder(
        ScopeId scopeId, Map<ScopeId, Key> bindingsForScope) {
      this.scopeId = scopeId;
      this.bindingsForScope = bindingsForScope;
    }

    @Override
    public ScopedBindingBuilder to(Class<? extends T> implementation) {
      return to(Key.get(implementation));
    }

    @Override
      public ScopedBindingBuilder to(TypeLiteral<? extends T> implementation) {
      return to(Key.get(implementation));
    }

    @Override
      public ScopedBindingBuilder to(Key<? extends T> targetKey) {
      bindingsForScope.put(scopeId, targetKey);
      return new UoeScopedBindingBuilder();
    }

    // --- methods below are not implemented ---

    @Override public void toInstance(T instance) {
      throw new UnsupportedOperationException();
    }

    @Override public ScopedBindingBuilder toProvider(
        Provider<? extends T> provider) {
      throw new UnsupportedOperationException();
    }

    @Override public ScopedBindingBuilder toProvider(
        Class<? extends javax.inject.Provider<? extends T>> providerType) {
      throw new UnsupportedOperationException();
    }

    @Override public ScopedBindingBuilder toProvider(
        TypeLiteral<? extends javax.inject.Provider<? extends T>> type) {
      throw new UnsupportedOperationException();
    }

    @Override public ScopedBindingBuilder toProvider(
        Key<? extends javax.inject.Provider<? extends T>> providerKey) {
      throw new UnsupportedOperationException();
    }

    @Override public <S extends T> ScopedBindingBuilder toConstructor(
        Constructor<S> constructor) {
      throw new UnsupportedOperationException();
    }

    @Override public <S extends T> ScopedBindingBuilder toConstructor(
        Constructor<S> constructor,
        TypeLiteral<? extends S> type) {
      throw new UnsupportedOperationException();
    }
  }
}
