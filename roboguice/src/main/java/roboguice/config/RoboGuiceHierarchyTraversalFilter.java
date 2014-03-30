package roboguice.config;

import roboguice.RoboGuice;

import com.google.inject.HierarchyTraversalFilter;

/**
 * Monitors the state of type hierarchy when injecting a given type via Guice.
 * This class allows to prune the type hierarchy when looking for injection points.
 * It is based on the following observations : 
 * <pre> <i>
 * If a class A extends B and B extends C,
 * then if B is inside android package, then B can't 
 * contains injections points.
 *
 * If B is inside RoboGuice package, or "subpackages", then it may
 * receive injections. A is probably a custom app class or a roboguice test
 * and it may contain injections as well. But if C doesn't belong to roboguice,
 * then we went too high inside the type hierarchy and it's not worth having a look
 * at class C and ascendants.
 * </i></pre>
 * @author SNI
 */
public class RoboGuiceHierarchyTraversalFilter extends HierarchyTraversalFilter {

    private static final String ANDROID_PACKAGE = "android";
    protected static final String ROBOGUICE_PACKAGE = RoboGuice.class.getPackage().getName();

    private boolean isInRoboGuicePackage = false;

    @Override
    public boolean isWorthScanning(Class<?> c) {
        if( c == null || c == Object.class) {
            return false;
        }
        String className = c.getName();
        if( className.startsWith(ANDROID_PACKAGE) ) {
            return false;
        } else if( className.startsWith(ROBOGUICE_PACKAGE)) {
            isInRoboGuicePackage = true;
        } else if( isInRoboGuicePackage ) {
            return false;
        }
        return true;
    }

    @Override
    public void reset( ) {
        super.reset();
        isInRoboGuicePackage = false;
    }
}

