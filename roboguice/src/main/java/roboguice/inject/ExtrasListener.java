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

import roboguice.RoboGuice;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.inject.*;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.google.inject.util.Types;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Map;

/**
 * 
 * @author Mike Burton
 * @author Pierre-Yves Ricau (py.ricau+roboguice@gmail.com)
 */
public class ExtrasListener implements TypeListener {
    protected Provider<Context> contextProvider;

    public ExtrasListener(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {

        for( Class<?> c = typeLiteral.getRawType(); c!=Object.class; c=c.getSuperclass() )
            for (Field field : c.getDeclaredFields())
                if (field.isAnnotationPresent(InjectExtra.class) )
                    if( Modifier.isStatic(field.getModifiers()) )
                        throw new UnsupportedOperationException("Extras may not be statically injected");
                    else
                        typeEncounter.register(new ExtrasMembersInjector<I>(field, contextProvider, field.getAnnotation(InjectExtra.class)));


    }




    protected static class ExtrasMembersInjector<T> implements MembersInjector<T> {
        protected Field field;
        protected Provider<Context> contextProvider;
        protected InjectExtra annotation;

        public ExtrasMembersInjector(Field field, Provider<Context> contextProvider, InjectExtra annotation) {
            this.field = field;
            this.contextProvider = contextProvider;
            this.annotation = annotation;
        }

        public void injectMembers(T instance) {
            final Context context = contextProvider.get();

            if( !(context instanceof Activity ) )
                throw new UnsupportedOperationException(String.format("Extras may not be injected into contexts that are not Activities (error in class %s)",contextProvider.get().getClass().getSimpleName()));


            final Activity activity = (Activity) context;
            Object value;

            final String id = annotation.value();
            final Bundle extras = activity.getIntent().getExtras();

            if (extras == null || !extras.containsKey(id)) {
                // If no extra found and the extra injection is optional, no
                // injection happens.
                if (annotation.optional()) {
                    return;
                } else {
                    throw new IllegalStateException(String.format("Can't find the mandatory extra identified by key [%s] on field %s.%s", id, field
                            .getDeclaringClass(), field.getName()));
                }
            }

            value = extras.get(id);

            value = convert(field, value, RoboGuice.getBaseApplicationInjector(activity.getApplication()));

            /*
             * Please notice : null checking is done AFTER conversion. Having
             *
             * @Nullable on a field means "the injected value might be null", ie
             * "the converted value might be null". Which also means that if you
             * don't use @Nullable and a converter returns null, an exception will
             * be thrown (which I find to be the most logic behavior).
             */
            if (value == null && Nullable.notNullable(field) ) {
                throw new NullPointerException(String.format("Can't inject null value into %s.%s when field is not @Nullable", field.getDeclaringClass(), field
                        .getName()));
            }

            field.setAccessible(true);
            try {

                field.set(instance, value);

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);

            } catch (IllegalArgumentException f) {
                throw new IllegalArgumentException(String.format("Can't assign %s value %s to %s field %s", value != null ? value.getClass() : "(null)", value,
                        field.getType(), field.getName()));
            }
        }

        @SuppressWarnings("unchecked")
        protected Object convert(Field field, Object value, Injector injector) {

            // Don't try to convert null or primitives
            if (value == null || field.getType().isPrimitive()) {
                return value;
            }

            // Building parameterized converter type
            // Please notice that the extra type and the field type must EXACTLY
            // match the declared converter parameter types.
            ParameterizedType pt = Types.newParameterizedType(ExtraConverter.class, value.getClass(), field.getType());
            Key<?> key = Key.get(pt);

            // Getting bindings map to check if a binding exists
            // We DO NOT currently check for injector's parent bindings. Should we ?
            Map<Key<?>, Binding<?>> bindings = injector.getBindings();

            if (bindings.containsKey(key)) {
                ExtraConverter converter = (ExtraConverter) injector.getInstance(key);
                value = converter.convert(value);
            }

            return value;

        }
    }

}

