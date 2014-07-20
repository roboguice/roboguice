package roboguice.inject;

import com.google.inject.Injector;


/**
 * Describes an enriched interface for RoboGuice injections.
 * Note to maintainers : this class should compile and be accessible to projects 
 * that use support or not.
 * @author Michael Burton
 */
public interface RoboInjector extends Injector {
    
    /**
     * Inject view members inside a given instance according to following rules.
     * <ul>
     *  <li> If the instance is a Context, it must be an Activity.
     *  <li> it can also be a Fragment (support or native)
     *  <li> it can also be a View (with nesting views)
     * </ul>
     * Note that this method must be loosly typed in order to accomodate apps built without the support-library.
     * @param instance the Activity, Fragment or View.
     * @see {@link InjectView}.
     */
    void injectViewMembers(Object instance);

    /**
     * Inject members, except views, in any kind of Ojbect.
     * @param instance a Java object that will receive RoboGuice injections.
     */
    void injectMembersWithoutViews(Object instance);
}
