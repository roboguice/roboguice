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
import android.content.Context;
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
    protected WeakHashMap<Context,ArrayList<ViewMembersInjector<?>>> viewMembersInjectors = new WeakHashMap<Context, ArrayList<ViewMembersInjector<?>>>();


    public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {

        for( Class<?> c = typeLiteral.getRawType(); c!=Object.class; c=c.getSuperclass() )
            for (Field field : c.getDeclaredFields())
                if (field.isAnnotationPresent(InjectView.class))
                    if( Modifier.isStatic(field.getModifiers()) )
                        throw new UnsupportedOperationException("Views may not be statically injected");
                    else if( !View.class.isAssignableFrom(field.getType()))
                        throw new UnsupportedOperationException("You may only use @InjectView on fields descended from type View");
                    else
                        //noinspection unchecked
                        typeEncounter.register(new ViewMembersInjector(field, field.getAnnotation(InjectView.class)));

    }


    public void injectViews(Activity activity, View root) {
        synchronized ( ViewListener.class ) {
            final ArrayList<ViewMembersInjector<?>> viewMembersInjectors = this.viewMembersInjectors.get(activity);
            if(viewMembersInjectors!=null)
                for(ViewMembersInjector<?> viewMembersInjector : viewMembersInjectors)
                    viewMembersInjector.reallyInjectMembers(activity,root);
        }
    }



    class ViewMembersInjector<T extends Activity> implements MembersInjector<T> {
        protected Field field;
        protected InjectView annotation;

        public ViewMembersInjector(Field field, InjectView annotation) {
            this.field = field;
            this.annotation = annotation;
        }

        public void injectMembers(T context) {
            synchronized (ViewListener.class) {
                ArrayList<ViewMembersInjector<?>> injectors = ViewListener.this.viewMembersInjectors.get(context);
                if( injectors ==null ) {
                    injectors = new ArrayList<ViewMembersInjector<?>>();
                    ViewListener.this.viewMembersInjectors.put(context, injectors);
                }
                injectors.add(this);
            }
        }

        public void reallyInjectMembers(Activity activity, View root) {

            Object value = null;

            try {

                value = root!=null ? root.findViewById(annotation.value()) : activity.findViewById(annotation.value());

                if (value == null && Nullable.notNullable(field))
                    throw new NullPointerException(String.format("Can't inject null value into %s.%s when field is not @Nullable", field.getDeclaringClass(), field.getName()));

                field.setAccessible(true);
                field.set(activity, value);

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);

            } catch (IllegalArgumentException f) {
                throw new IllegalArgumentException(String.format("Can't assign %s value %s to %s field %s", value != null ? value.getClass() : "(null)", value,
                        field.getType(), field.getName()));
            }
        }


    }

}
