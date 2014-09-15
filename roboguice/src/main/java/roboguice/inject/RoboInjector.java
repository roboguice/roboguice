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
     * Inject members, except views, in any kind of Ojbect.
     * @param instance a Java object that will receive RoboGuice injections.
     */
    void injectMembersWithoutViews(Object instance);
}
