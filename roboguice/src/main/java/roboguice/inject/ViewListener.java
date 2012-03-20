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
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.WeakHashMap;

@Singleton
public class ViewListener implements TypeListener {
    
    protected static Class fragmentClass = null;
    protected static Class fragmentManagerClass = null;
    protected static Method fragmentGetViewMethod = null;
    protected static Method fragmentFindFragmentByIdMethod = null;
    protected static Method fragmentFindFragmentByTagMethod = null;

    static {
        try {
            fragmentClass = Class.forName("android.support.v4.app.Fragment");
            fragmentManagerClass = Class.forName("android.support.v4.app.FragmentManager");
            fragmentGetViewMethod = fragmentClass.getDeclaredMethod("getView");
            fragmentFindFragmentByIdMethod = fragmentManagerClass.getMethod("findFragmentById", int.class);
            fragmentFindFragmentByTagMethod = fragmentManagerClass.getMethod("findFragmentByTag",Object.class);
        } catch( Throwable ignored ) {}
    }

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
                    else if( fragmentClass!=null && !fragmentClass.isAssignableFrom(field.getType()))
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
        protected Provider fragmentManagerProvider;
        protected Provider<Activity> activityProvider;
        

        public ViewMembersInjector(Field field, Annotation annotation, TypeEncounter<T> typeEncounter ) {
            this.field = field;
            this.annotation = annotation;
            this.activityProvider = typeEncounter.getProvider(Activity.class);

            if( fragmentManagerClass!=null )
                this.fragmentManagerProvider = typeEncounter.getProvider(fragmentManagerClass);

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
                final Object key = fragmentClass!=null && fragmentClass.isInstance(instance) ? instance : activity;
                if( key==null )
                    return;

                // Add a view injector for the key
                ArrayList<ViewMembersInjector<?>> injectors = viewMembersInjectors.get(key);
                if( injectors==null ) {
                    injectors = new ArrayList<ViewMembersInjector<?>>();
                    viewMembersInjectors.put(key, injectors);
                }
                injectors.add(this);




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
         *
         * I don't like all the hacks I had to put into this method.  Instance is the object you're
         * injecting into.  ActivityOrFragment is the activity or fragment that you're injecting
         * views into.  Instance must equal activityOrFragment is activityOrFragment is a fragment,
         * but they may differ if activityOrFragment is an activity.  They should be allowed to differ
         * so that you can inject views into arbitrary objects, but I don't know how to determine whether
         * to get the view from the fragment or the activity for an arbitrary object, so I'm artificially
         * limiting injection targets to the fragment itself for fragments.
         *
         * @param activityOrFragment an activity or fragment
         */
        protected void reallyInjectMemberViews(Object activityOrFragment) {

            final T instance = fragmentClass!=null && fragmentClass.isInstance(activityOrFragment) ? (T)activityOrFragment : instanceRef.get();
            if( instance==null )
                return;

            if( activityOrFragment instanceof Context && !(activityOrFragment instanceof Activity ))
                throw new UnsupportedOperationException("Can't inject view into a non-Activity context");

            View view = null;

            try {
                final InjectView injectView = (InjectView) annotation;
                final int id = injectView.value();

                if( id>=0 )
                    view = fragmentClass!=null && fragmentClass.isInstance(activityOrFragment) ? ((View)fragmentGetViewMethod.invoke(activityOrFragment)).findViewById(id) : ((Activity)activityOrFragment).findViewById(id);

                else
                    view = fragmentClass!=null && fragmentClass.isInstance(activityOrFragment) ? ((View)fragmentGetViewMethod.invoke(activityOrFragment)).findViewWithTag(injectView.tag()) : ((Activity)activityOrFragment).getWindow().getDecorView().findViewWithTag(injectView.tag());


                if (view == null && Nullable.notNullable(field))
                    throw new NullPointerException(String.format("Can't inject null value into %s.%s when field is not @Nullable", field.getDeclaringClass(), field.getName()));

                field.setAccessible(true);
                field.set(instance, view);

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);

            } catch (InvocationTargetException e) {
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

            Object fragment = null;

            try {
                final InjectFragment injectFragment = (InjectFragment) annotation;
                final int id = injectFragment.value();
                
                if( id>=0 )
                    fragment = fragmentFindFragmentByIdMethod.invoke(fragmentManagerProvider.get(), id);
                
                else
                    fragment = fragmentFindFragmentByTagMethod.invoke(fragmentManagerProvider.get(), injectFragment.tag());

                if (fragment == null && Nullable.notNullable(field))
                    throw new NullPointerException(String.format("Can't inject null value into %s.%s when field is not @Nullable", field.getDeclaringClass(), field.getName()));

                field.setAccessible(true);
                field.set(instance, fragment);

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);

            } catch (InvocationTargetException e) {
                throw new RuntimeException( e );

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
