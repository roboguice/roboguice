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

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceActivity;

import com.google.inject.MembersInjector;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * 
 * @author Mike Burton
 */
public class PreferenceListener implements TypeListener {
    protected ArrayList<PreferenceMembersInjector<?>> preferencesForInjection = new ArrayList<PreferenceMembersInjector<?>>();

    protected Provider<Context> contextProvider;
    protected Application application;
    protected ContextScope scope;

    public PreferenceListener(Provider<Context> contextProvider, Application application, ContextScope scope) {
        this.contextProvider = contextProvider;
        this.application = application;
        this.scope = scope;
    }

    public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
        for( Class<?> c = typeLiteral.getRawType(); c!=Object.class; c=c.getSuperclass() )
            for (Field field : c.getDeclaredFields())
                if ( field.isAnnotationPresent(InjectPreference.class))
                    if( Modifier.isStatic(field.getModifiers()) )
                        throw new UnsupportedOperationException("Preferences may not be statically injected");
                    else
                        typeEncounter.register(new PreferenceMembersInjector<I>(field, contextProvider, field.getAnnotation(InjectPreference.class), scope));

    }


    public void registerPreferenceForInjection(PreferenceMembersInjector<?> injector) {
        preferencesForInjection.add(injector);
    }

    public void injectPreferenceViews() {
        for (int i = preferencesForInjection.size() - 1; i >= 0; --i)
            preferencesForInjection.remove(i).reallyInjectMembers();
    }



    class PreferenceMembersInjector<T> implements MembersInjector<T> {
        protected Field field;
        protected Provider<Context> contextProvider;
        protected InjectPreference annotation;
        protected ContextScope scope;
        protected WeakReference<T> instanceRef;

        public PreferenceMembersInjector(Field field, Provider<Context> contextProvider, InjectPreference annotation, ContextScope scope) {
            this.field = field;
            this.annotation = annotation;
            this.contextProvider = contextProvider;
            this.scope = scope;
        }

        public void injectMembers(T instance) {
            // Mark instance for injection during setContentView
            this.instanceRef = new WeakReference<T>(instance);
            registerPreferenceForInjection(this);
        }

        public void reallyInjectMembers() {
            final T instance = instanceRef.get();
            if( instance==null )
                return;

            Object value = null;

            try {

                value = ((PreferenceActivity) contextProvider.get()).findPreference(annotation.value());

                if (value == null && Nullable.notNullable(field) )
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
}
