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

import roboguice.fragment.FragmentUtil;
import roboguice.fragment.FragmentUtil.f;

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
@SuppressWarnings("unchecked")
public class ViewListener implements TypeListener {
       
    @Override
    public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {

        for (Class<?> c = typeLiteral.getRawType(); c != Object.class; c = c.getSuperclass())
            for (Field field : c.getDeclaredFields()) {
                if (field.isAnnotationPresent(InjectView.class))
                    if (Modifier.isStatic(field.getModifiers()))
                        throw new UnsupportedOperationException("Views may not be statically injected");
                    else if (!View.class.isAssignableFrom(field.getType()))
                    	throw new UnsupportedOperationException("You may only use @InjectView on fields descended from type View");
                    else if (Context.class.isAssignableFrom(field.getDeclaringClass()) && !Activity.class.isAssignableFrom(field.getDeclaringClass()))
                    	throw new UnsupportedOperationException("You may only use @InjectView in Activity contexts");
                    else {
                    	final f<?,?> utils = FragmentUtil.hasSupport 
                    			&& (FragmentUtil.supportActivity.isAssignableFrom(field.getDeclaringClass()) 
                    					|| FragmentUtil.supportFrag.fragmentType().isAssignableFrom(field.getDeclaringClass()))
                    					? FragmentUtil.supportFrag : FragmentUtil.nativeFrag;

                    	typeEncounter.register(new ViewMembersInjector<I>(
                    			field, field.getAnnotation(InjectView.class),
                    			typeEncounter, utils));
                    }

                else if (field.isAnnotationPresent(InjectFragment.class))
                    if (!FragmentUtil.hasNative && !FragmentUtil.hasSupport) {
                        throw new RuntimeException(new ClassNotFoundException("No fragment classes were available"));
                    } else if (Modifier.isStatic(field.getModifiers())) {
                    	throw new UnsupportedOperationException("Fragments may not be statically injected");

	                } else {
	                    final boolean assignableFromNative = FragmentUtil.hasNative && FragmentUtil.nativeFrag.fragmentType().isAssignableFrom(field.getType());
	                    final boolean assignableFromSupport = FragmentUtil.hasSupport && FragmentUtil.supportFrag.fragmentType().isAssignableFrom(field.getType());
	                    final boolean isSupportActivity = FragmentUtil.hasSupport && FragmentUtil.supportActivity.isAssignableFrom(field.getDeclaringClass());
	                    final boolean isNativeActivity = !isSupportActivity && Activity.class.isAssignableFrom(field.getDeclaringClass());
	
	                    if ((isNativeActivity && assignableFromNative) || (isSupportActivity && assignableFromSupport)) {
	                        typeEncounter.register(new ViewMembersInjector<I>(field, field.getAnnotation(InjectFragment.class), typeEncounter, isNativeActivity ? FragmentUtil.nativeFrag:FragmentUtil.supportFrag));
	                    }
	                    // Error messages - these filters are comprehensive. The
	                    // final else block will never execute.
	                    else if (isNativeActivity && !assignableFromNative) {
	                        throw new UnsupportedOperationException(
	                                "You may only use @InjectFragment in native activities if fields are descended from type android.app.Fragment");
	                    } else if (!isSupportActivity && !isNativeActivity) {
	                        throw new UnsupportedOperationException("You may only use @InjectFragment in Activity contexts");
	                    } else if (isSupportActivity && !assignableFromSupport) {
	                        throw new UnsupportedOperationException(
	                                "You may only use @InjectFragment in support activities if fields are descended from type android.support.v4.app.Fragment");
	                    } else {
	                        throw new RuntimeException("This should never happen.");
	                    }
	                }
            }
    }


    /**
     * This class gets twice as many providers as necessary to do its job, look into optimizations in the future if this is a bottleneck
     */
    public static class ViewMembersInjector<T> implements MembersInjector<T> {
        protected static WeakHashMap<Object,ArrayList<ViewMembersInjector<?>>> viewMembersInjectors = new WeakHashMap<Object, ArrayList<ViewMembersInjector<?>>>();

        protected Field field;
        protected Annotation annotation;
        protected WeakReference<T> instanceRef;
        @SuppressWarnings("rawtypes")
		protected FragmentUtil.f fragUtils;
        @SuppressWarnings("rawtypes")
		protected Provider fragManager;
        protected Provider<Activity> activityProvider;
        

        public ViewMembersInjector(Field field, Annotation annotation, TypeEncounter<T> typeEncounter, FragmentUtil.f<?,?> utils) {
            this.field = field;
            this.annotation = annotation;
            this.activityProvider = typeEncounter.getProvider(Activity.class);

            if( utils !=null ) {
                this.fragUtils = utils;
                this.fragManager = typeEncounter.getProvider(utils.fragmentManagerType());
            }

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
                final Object key = fragUtils != null && fragUtils.fragmentType().isInstance(instance) ? instance : activity;
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

            final T instance = fragUtils != null && fragUtils.fragmentType().isInstance(activityOrFragment) ? (T)activityOrFragment : instanceRef.get();
            if( instance==null )
                return;

            if( activityOrFragment instanceof Context && !(activityOrFragment instanceof Activity ))
                throw new UnsupportedOperationException("Can't inject view into a non-Activity context");

            View view = null;

            try {
                final InjectView injectView = (InjectView) annotation;
                final int id = injectView.value();

                if( id>=0 )
                    view = fragUtils != null && fragUtils.fragmentType().isInstance(activityOrFragment) ? (fragUtils.getView(activityOrFragment)).findViewById(id) : ((Activity)activityOrFragment).findViewById(id);

                else
                    view = fragUtils !=null && fragUtils.fragmentType().isInstance(activityOrFragment) ? (fragUtils.getView(activityOrFragment)).findViewWithTag(injectView.tag()) : ((Activity)activityOrFragment).getWindow().getDecorView().findViewWithTag(injectView.tag());

                if (view == null && Nullable.notNullable(field))
                    throw new NullPointerException(String.format("Can't inject null value into %s.%s when field is not @Nullable", field.getDeclaringClass(), field.getName()));

                field.setAccessible(true);
                field.set(instance, view);

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);

            }  catch (IllegalArgumentException f) {
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
                    fragment = fragUtils.findFragmentById(fragManager.get(), id);
                else
                    fragment = fragUtils.findFragmentByTag(fragManager.get(),injectFragment.tag());
                
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
