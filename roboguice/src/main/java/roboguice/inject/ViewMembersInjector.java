package roboguice.inject;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.WeakHashMap;

import roboguice.fragment.FragmentUtil;

import com.google.inject.MembersInjector;
import com.google.inject.Provider;
import com.google.inject.spi.TypeEncounter;

import android.app.Activity;
import android.content.Context;
import android.view.View;

/**
 * This class gets twice as many providers as necessary to do its job, look into optimizations in the future if this is a bottleneck
 */
public class ViewMembersInjector<T> implements MembersInjector<T> {
    protected static final WeakHashMap<Object,ArrayList<ViewMembersInjector<?>>> VIEW_MEMBERS_INJECTORS = new WeakHashMap<Object, ArrayList<ViewMembersInjector<?>>>();

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
            boolean isValidFragment = fragUtils != null && fragUtils.fragmentType().isInstance(instance);
            final Object key = isValidFragment || instance instanceof View ? instance : activity;
            if( key==null )
                return;

            // Add a view injector for the key
            ArrayList<ViewMembersInjector<?>> injectors = VIEW_MEMBERS_INJECTORS.get(key);
            if( injectors==null ) {
                injectors = new ArrayList<ViewMembersInjector<?>>();
                VIEW_MEMBERS_INJECTORS.put(key, injectors);
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
     * @param target an activity or fragment or a view.
     */
    protected void reallyInjectMemberViews(Object target) {

        boolean isValidFragment = fragUtils != null && fragUtils.fragmentType().isInstance(target);
        final T instance = isValidFragment ? (T)target : instanceRef.get();
        if( instance==null )
            return;

        View view = null;

        final InjectView injectView = (InjectView) annotation;
        final int id = injectView.value();
        //contains the view to inject, as a layout container, not nceseraily a data member.
        View containerView = null;
        containerView = extractContainerView(target, isValidFragment);

        if( id>=0 ) {
            view = containerView.findViewById(id);
        } else {
            view = containerView.findViewWithTag(injectView.tag());
        }

        if (view == null && Nullable.notNullable(field))
            throw new NullPointerException(String.format("Can't inject null value into %s.%s when field is not @Nullable", field.getDeclaringClass(), field.getName()));

        try {
            field.setAccessible(true);
            field.set(instance, view);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }  catch (IllegalArgumentException f) {
            throw new IllegalArgumentException(String.format("Can't assign %s value %s to %s field %s", view != null ? view.getClass() : "(null)", view,
                    field.getType(), field.getName()), f);
        }
    }

    private View extractContainerView(Object target, boolean isValidFragment) {
        View containerView;
        if( isValidFragment ) {
            containerView =  fragUtils.getView(target);
        } else if( target instanceof View ) {
            containerView = (View) target;
        } else if( target instanceof Activity ) {
            //it must be an activity so
            containerView = ((Activity)target).getWindow().getDecorView();
        } else {
            throw new UnsupportedOperationException("Can't inject view into something that is not a Fragment, Activity or View.");
        }
        return containerView;
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

            final ArrayList<ViewMembersInjector<?>> injectors = VIEW_MEMBERS_INJECTORS.get(activityOrFragment);
            if(injectors!=null)
                for(ViewMembersInjector<?> viewMembersInjector : injectors)
                    viewMembersInjector.reallyInjectMembers(activityOrFragment);
        }
    }
}
