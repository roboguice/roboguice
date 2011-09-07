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
import android.support.v4.app.Fragment;
import android.view.View;

import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import javax.inject.Singleton;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.WeakHashMap;

@Singleton
public class ViewListener implements TypeListener {


    public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {

        for( Class<?> c = typeLiteral.getRawType(); c!=Object.class; c=c.getSuperclass() )
            for (Field field : c.getDeclaredFields())
                if (field.isAnnotationPresent(InjectView.class))
                    if( Modifier.isStatic(field.getModifiers()) )
                        throw new UnsupportedOperationException("Views may not be statically injected");
                    else if( !View.class.isAssignableFrom(field.getType()))
                        throw new UnsupportedOperationException("You may only use @InjectView on fields descended from type View");
                    else
                        typeEncounter.register(new ViewMembersInjector<I>(field, field.getAnnotation(InjectView.class)));

    }


    public void injectViews(Object instance) {
        ViewMembersInjector.injectViews(instance);
    }



    



    public static class ViewMembersInjector<T> implements MembersInjector<T> {
        protected static WeakHashMap<Object,ArrayList<ViewMembersInjector<?>>> viewMembersInjectors = new WeakHashMap<Object, ArrayList<ViewMembersInjector<?>>>();

        protected Field field;
        protected InjectView annotation;

        public ViewMembersInjector(Field field, InjectView annotation) {
            this.field = field;
            this.annotation = annotation;
        }

        public void injectMembers(T activityOrFragmentOrView) {
            synchronized (ViewMembersInjector.class) {
                ArrayList<ViewMembersInjector<?>> injectors = viewMembersInjectors.get(activityOrFragmentOrView);
                if( injectors ==null ) {
                    injectors = new ArrayList<ViewMembersInjector<?>>();
                    viewMembersInjectors.put(activityOrFragmentOrView, injectors);
                }
                injectors.add(this);
            }
        }

        public void reallyInjectMembers(Object activityOrFragmentOrView) {

            Object value = activityOrFragmentOrView;

            try {
                final int[] viewIds = annotation.value();
                
                if( viewIds.length<1 )
                    throw new NullPointerException(String.format("Cant inject view into %s.%s when view id is not specified", field.getDeclaringClass(), field.getName()));

                for (int viewId : viewIds)
                    value = value instanceof View ? ((View) value).findViewById(viewId) : value instanceof Fragment ? ((Fragment)value).getView().findViewById(viewId) : ((Activity) value).findViewById(viewId);

                if (value == activityOrFragmentOrView && Nullable.notNullable(field))
                    throw new NullPointerException(String.format("Can't inject null value into %s.%s when field is not @Nullable", field.getDeclaringClass(), field.getName()));

                field.setAccessible(true);
                field.set(activityOrFragmentOrView, value);

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);

            } catch (IllegalArgumentException f) {
                throw new IllegalArgumentException(String.format("Can't assign %s value %s to %s field %s", value != null ? value.getClass() : "(null)", value,
                        field.getType(), field.getName()));
            }
        }

        protected static void injectViews(Object instance) {
            synchronized ( ViewMembersInjector.class ) {
                final ArrayList<ViewMembersInjector<?>> injectors = viewMembersInjectors.get(instance);
                if(injectors!=null)
                    for(ViewMembersInjector<?> viewMembersInjector : injectors)
                        viewMembersInjector.reallyInjectMembers(instance);
            }
        }




    }

}
