/*
 * Copyright 2010 Rodrigo Damazio
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

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.google.inject.MembersInjector;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Nullable;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.lang.reflect.Field;

/**
 * Listener for preference injection.
 *
 * @author Rodrigo Damazio
 */
public class PreferenceListener implements TypeListener {
    protected Provider<Context> contextProvider;

    public PreferenceListener(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
        Class<?> c = typeLiteral.getRawType();
        while (c != null) {
            for (Field field : c.getDeclaredFields()) {
                if (field.isAnnotationPresent(InjectPreference.class)) {
                    typeEncounter.register(new PreferenceMembersInjector<I>(field, contextProvider,
                        field.getAnnotation(InjectPreference.class)));
                }
            }
            c = c.getSuperclass();
        }
    }
}

class PreferenceMembersInjector<T> implements MembersInjector<T> {
    protected final Field field;
    protected final Provider<Context> contextProvider;
    protected final InjectPreference annotation;

    public PreferenceMembersInjector(Field field, Provider<Context> contextProvider,
                                     InjectPreference annotation) {
        this.field = field;
        this.contextProvider = contextProvider;
        this.annotation = annotation;
    }

    public void injectMembers(T instance) {
        final Context context = contextProvider.get();

        if (!(context instanceof PreferenceActivity)) {
            throw new IllegalArgumentException(String.format(
                "Can't inject a preference into a non-subclass of PreferenceAcvitity (%s)",
                field.getDeclaringClass()));
        }

        final PreferenceActivity activity = (PreferenceActivity) context;
        final String key = annotation.value();

        Preference value = activity.findPreference(key);

        if (value == null && !field.isAnnotationPresent(Nullable.class)) {
            throw new NullPointerException(
                String.format("Can't inject null value into %s.%s when field is not @Nullable",
                              field.getDeclaringClass(), field.getName()));
        }

        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException f) {
            throw new IllegalArgumentException(
                String.format("Can't assign %s value %s to %s field %s",
                              value != null ? value.getClass() : "(null)",
                              value, field.getType(), field.getName()));
        }
    }
}
