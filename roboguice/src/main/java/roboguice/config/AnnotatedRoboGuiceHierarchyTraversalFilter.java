package roboguice.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

/**
 * Once a class is detected has having injection points,
 * its super classes are kept as long as they satisfy the filtering operated
 * by {@link RoboGuiceHierarchyTraversalFilter}. Otherwise, the class will be rejected
 * by the filter.
 * @author SNI
 */
public class AnnotatedRoboGuiceHierarchyTraversalFilter extends RoboGuiceHierarchyTraversalFilter {
    private boolean hasHadInjectionPoints;
    private static HashMap<String, HashSet<String>> mapAnnotationToclassesContainingInjectionPoints;
    private static HashSet<String> classesContainingInjectionPointsSet = new HashSet<String>();

    public  AnnotatedRoboGuiceHierarchyTraversalFilter(HashMap<String, HashSet<String>> mapAnnotationToclassesContainingInjectionPoints) {
        if(mapAnnotationToclassesContainingInjectionPoints.isEmpty())
            throw new IllegalStateException("Unable to find Annotation Database which should be output as part of annotation processing");

        AnnotatedRoboGuiceHierarchyTraversalFilter.mapAnnotationToclassesContainingInjectionPoints = mapAnnotationToclassesContainingInjectionPoints;
        for( Entry<String, HashSet<String>> entryAnnotationToclassesContainingInjectionPoints : mapAnnotationToclassesContainingInjectionPoints.entrySet() ) {
            classesContainingInjectionPointsSet.addAll(entryAnnotationToclassesContainingInjectionPoints.getValue());
        }
    }
    
    @Override
    public boolean isWorthScanning(Class<?> c) {
        if( hasHadInjectionPoints ) {
            return super.isWorthScanning(c);
        } else if( c != null && classesContainingInjectionPointsSet.contains(c.getName()) ) {
            hasHadInjectionPoints = true;
            return true;
        }  
        return false;
    }
    
    @Override
    public boolean isWorthScanning(String annotationClassName, Class<?> c) {
        HashSet<String> classesContainingInjectionPointsForAnnotation;
        if( hasHadInjectionPoints ) {
            return super.isWorthScanning(c);
        } else if( c != null && (classesContainingInjectionPointsForAnnotation = mapAnnotationToclassesContainingInjectionPoints.get(annotationClassName)) != null && classesContainingInjectionPointsForAnnotation.contains(c.getName()) ) {
            hasHadInjectionPoints = true;
            return true;
        } else 
        return false;
    }
    
    public void reset( ) {
        super.reset();
        hasHadInjectionPoints = false;
    }

}