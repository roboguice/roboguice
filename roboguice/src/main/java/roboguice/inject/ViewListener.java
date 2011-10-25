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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.google.inject.MembersInjector;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
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
                    else if( Context.class.isAssignableFrom(field.getDeclaringClass()) && !Activity.class.isAssignableFrom(field.getDeclaringClass()))
                        throw new UnsupportedOperationException("You may only use @InjectView in Activity contexts");
                    else
                        typeEncounter.register(new ViewMembersInjector<I>(field, field.getAnnotation(InjectView.class), typeEncounter));
                
                
                else if (field.isAnnotationPresent(InjectFragment.class))
                    if( Modifier.isStatic(field.getModifiers()) )
                        throw new UnsupportedOperationException("Fragments may not be statically injected");
                    else if( !Fragment.class.isAssignableFrom(field.getType()))
                        throw new UnsupportedOperationException("You may only use @InjectFragment on fields descended from type Fragment");
                    else if( Context.class.isAssignableFrom(field.getDeclaringClass()) && !Activity.class.isAssignableFrom(field.getDeclaringClass()))
                        throw new UnsupportedOperationException("You may only use @InjectFragment in Activity contexts");
                    else
                        typeEncounter.register(new ViewMembersInjector<I>(field, field.getAnnotation(InjectFragment.class), typeEncounter));

    }


    /**
     * This class gets twice as many providers as necessary to do its job, look into optimizations in the future if this is a bottleneck
     */
    public static class ViewMembersInjector<T> implements MembersInjector<T> {
        protected static WeakHashMap<Object,ArrayList<ViewMembersInjector<?>>> viewMembersInjectors = new WeakHashMap<Object, ArrayList<ViewMembersInjector<?>>>();

        protected Field field;
        protected Annotation annotation;
        protected WeakReference<T> instanceRef;
        protected Provider<FragmentManager> fragmentManagerProvider;
        protected Provider<Activity> activityProvider;
        

        public ViewMembersInjector(Field field, Annotation annotation, TypeEncounter<T> typeEncounter ) {
            this.field = field;
            this.annotation = annotation;
            this.activityProvider = typeEncounter.getProvider(Activity.class);
            this.fragmentManagerProvider = typeEncounter.getProvider(FragmentManager.class); 

        }

        /**
         * This is called when instance is injected by guice.  Because the views may or may not be set up yet,
         * we don't do the real view injection until later.
         *
         * @param instance the instance being injected by guice
         */
        public void injectMembers(T instance) {
            synchronized (ViewMembersInjector.class) {
                final Activity activity = activityProvider.get();

                // Add a view injector for the activity
                ArrayList<ViewMembersInjector<?>> injectors = viewMembersInjectors.get(activity);
                if( injectors ==null ) {
                    injectors = new ArrayList<ViewMembersInjector<?>>();
                    viewMembersInjectors.put(activity, injectors);
                }
                injectors.add(this);


                // Add a view injector for the fragment, if appropriate
                if( instance instanceof Fragment ) {
                    injectors = viewMembersInjectors.get(instance);
                    if( injectors ==null ) {
                        injectors = new ArrayList<ViewMembersInjector<?>>();
                        viewMembersInjectors.put(instance, injectors);
                    }
                    injectors.add(this);
                }


                this.instanceRef = new WeakReference<T>(instance);
            }
        }
        
        public void reallyInjectMembers( Object activityOrFragment ) {
            if( annotation instanceof InjectView )
                reallyInjectMemberViews(activityOrFragment);
            else
                reallyInjectMemberFragments(activityOrFragment);
        }

        /**
         * This is when the view references are actually evaluated.
         * @param activityOrFragment an activity or fragment
         */
        protected void reallyInjectMemberViews(Object activityOrFragment) {

            final T instance = instanceRef.get();
            if( instance==null )
                return;

            if( activityOrFragment instanceof Context && !(activityOrFragment instanceof Activity ))
                throw new UnsupportedOperationException("Can't inject view into a non-Activity context");

            View view = null;

            try {
                final int id = ((InjectView)annotation).value();
                
                view = activityOrFragment instanceof Fragment ? ((Fragment)activityOrFragment).getView().findViewById(id) : ((Activity)activityOrFragment).findViewById(id);

                if (view == null && Nullable.notNullable(field))
                    throw new NullPointerException(String.format("Can't inject null value into %s.%s when field is not @Nullable", field.getDeclaringClass(), field.getName()));

                field.setAccessible(true);
                field.set(instance, view);

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);

            } catch (IllegalArgumentException f) {
                throw new IllegalArgumentException(String.format("Can't assign %s value %s to %s field %s", view != null ? view.getClass() : "(null)", view,
                        field.getType(), field.getName()), f);
            }
        }

        /**
         * This is when the view references are actually evaluated.
         * @param activityOrFragment an activity or fragment
         */
        protected void reallyInjectMemberFragments(Object activityOrFragment) {

            final T instance = instanceRef.get();
            if( instance==null )
                return;

            if( activityOrFragment instanceof Context && !(activityOrFragment instanceof Activity ))
                throw new UnsupportedOperationException("Can't inject fragment into a non-Activity context");

            Fragment fragment = null;

            try {
                final int id = ((InjectFragment)annotation).value();
                
                fragment = fragmentManagerProvider.get().findFragmentById(id);

                if (fragment == null && Nullable.notNullable(field))
                    throw new NullPointerException(String.format("Can't inject null value into %s.%s when field is not @Nullable", field.getDeclaringClass(), field.getName()));

                field.setAccessible(true);
                field.set(instance, fragment);

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);

            } catch (IllegalArgumentException f) {
                throw new IllegalArgumentException(String.format("Can't assign %s value %s to %s field %s", fragment != null ? fragment.getClass() : "(null)", fragment,
                        field.getType(), field.getName()), f);
            }
        }

        
        protected static void injectViews(Object activityOrFragment) {
            synchronized ( ViewMembersInjector.class ) {

                final ArrayList<ViewMembersInjector<?>> injectors = viewMembersInjectors.get(activityOrFragment);
                if(injectors!=null)
                    for(ViewMembersInjector<?> viewMembersInjector : injectors)
                        viewMembersInjector.reallyInjectMembers(activityOrFragment);
            }
        }




    }

}
