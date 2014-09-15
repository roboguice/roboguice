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

/**
 * This class gets twice as many providers as necessary to do its job, look into optimizations in the future if this is a bottleneck
 */
public class FragmentMembersInjector<T> implements MembersInjector<T> {
    protected static final WeakHashMap<Object,ArrayList<FragmentMembersInjector<?>>> VIEW_MEMBERS_INJECTORS = new WeakHashMap<Object, ArrayList<FragmentMembersInjector<?>>>();

    protected Field field;
    protected Annotation annotation;
    protected WeakReference<T> instanceRef;
    @SuppressWarnings("rawtypes")
    protected FragmentUtil.f fragUtils;
    @SuppressWarnings("rawtypes")
    protected Provider fragManager;
    protected Provider<Activity> activityProvider;


    public FragmentMembersInjector(Field field, Annotation annotation, TypeEncounter<T> typeEncounter, FragmentUtil.f<?,?> utils) {
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
        synchronized (FragmentMembersInjector.class) {
            boolean isValidFragment = fragUtils != null && fragUtils.fragmentType().isInstance(instance);
            final Object key = isValidFragment ? instance : activityProvider.get();
            if( key==null )
                return;

            // Add a view injector for the key
            ArrayList<FragmentMembersInjector<?>> injectors = VIEW_MEMBERS_INJECTORS.get(key);
            if( injectors==null ) {
                injectors = new ArrayList<FragmentMembersInjector<?>>();
                VIEW_MEMBERS_INJECTORS.put(key, injectors);
            }
            injectors.add(this);

            this.instanceRef = new WeakReference<T>(instance);
        }
    }
    
}
