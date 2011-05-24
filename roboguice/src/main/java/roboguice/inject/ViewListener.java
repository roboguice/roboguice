/*
 * Copyright 2009 Michael Burton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package roboguice.inject;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.google.inject.MembersInjector;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Nullable;
import com.google.inject.spi.TypeEncounter;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 
 * @author Mike Burton
 */
public class ViewListener implements StaticTypeListener {
    protected Provider<Context> contextProvider;
    protected Application application;
    protected ContextScope scope;

    public ViewListener(Provider<Context> contextProvider, Application application, ContextScope scope) {
        this.contextProvider = contextProvider;
        this.application = application;
        this.scope = scope;
    }

    public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
        Class<?> c = typeLiteral.getRawType();
        while (c != null) {
            for (Field field : c.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) && field.isAnnotationPresent(InjectView.class)) {
                    typeEncounter.register(new ViewMembersInjector<I>(field, contextProvider, field.getAnnotation(InjectView.class), scope));
                }
            }
            c = c.getSuperclass();
        }
    }

    @SuppressWarnings("unchecked")
    public void requestStaticInjection(Class<?>... types) {
        for (Class<?> c : types) {
            while (c != null) {
                for (Field field : c.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers()) && field.isAnnotationPresent(InjectView.class)) {
                        new ViewMembersInjector(field, contextProvider, field.getAnnotation(InjectView.class), scope).injectMembers(null);
                    }
                }
                c = c.getSuperclass();
            }
        }

    }
}

class ViewMembersInjector<T> implements MembersInjector<T> {
    protected Field field;
    protected Provider<Context> contextProvider;
    protected InjectView annotation;
    protected ContextScope scope;
    protected WeakReference<T> instanceRef;

    public ViewMembersInjector(Field field, Provider<Context> contextProvider, InjectView annotation, ContextScope scope) {
        this.field = field;
        this.annotation = annotation;
        this.contextProvider = contextProvider;
        this.scope = scope;
    }

    public void injectMembers(T instance) {
        // Mark instance for injection during setContentView
        this.instanceRef = new WeakReference<T>(instance);
        scope.registerViewForInjection(this);
    }

    public void reallyInjectMembers() {

        final T instance = instanceRef.get();
        if( instance ==null )
            return;

        Object value = null;

        try {

            value = ((Activity) contextProvider.get()).findViewById(annotation.value());

            if (value == null && field.getAnnotation(Nullable.class) == null)
                throw new NullPointerException(String.format("Can't inject null value into %s.%s when field is not @Nullable", field.getDeclaringClass(), field.getName()));

            field.setAccessible(true);
            field.set(instance, value);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);

        } catch (IllegalArgumentException f) {
            throw new IllegalArgumentException(String.format("Can't assign %s value %s to %s field %s", value != null ? value.getClass() : "(null)", value,
                    field.getType(), field.getName()));
        }
    }
}
