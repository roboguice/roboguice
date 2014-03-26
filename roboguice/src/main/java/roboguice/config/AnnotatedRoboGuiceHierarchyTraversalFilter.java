package roboguice.config;

import java.util.Set;

/**
 * Once a class is detected has having injection points,
 * its super classes are kept as long as they satisfy the filtering operated
 * by {@link RoboGuiceHierarchyTraversalFilter}. Otherwise, the class will be rejected
 * by the filter.
 * @author SNI
 */
public class AnnotatedRoboGuiceHierarchyTraversalFilter extends RoboGuiceHierarchyTraversalFilter {
    private boolean hasHadInjectionPoints;
    private static Set<String> classesContainingInjectionPoints;

    public  AnnotatedRoboGuiceHierarchyTraversalFilter(Set<String> classesContainingInjectionPoints) {
        if(classesContainingInjectionPoints.isEmpty())
            throw new IllegalStateException("Unable to find Annotation Database which should be output as part of annotation processing");

        AnnotatedRoboGuiceHierarchyTraversalFilter.classesContainingInjectionPoints = classesContainingInjectionPoints;
    }
    
    @Override
    public boolean isWorthScanning(Class<?> c) {
        if( hasHadInjectionPoints ) {
            return super.isWorthScanning(c);
        } else if( c != null && classesContainingInjectionPoints.contains(c.getName()) ) {
            hasHadInjectionPoints = true;
            return true;
        }  
        return false;
    }
    
    public void reset( ) {
        super.reset();
        hasHadInjectionPoints = false;
    }

}