package roboguice.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

/**
 * Will filter in or out classes based on the information gathered by the annotation
 * preprocessor of RoboGuice. A class is filtered in if it contains an injection point
 * or its super classes contain an injection point.<br/>
 * Once a class is filtered in has having injection points,
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
        } else if( c != null ) {
            do {
                if( classesContainingInjectionPointsSet.contains(c.getName()) ) {
                    hasHadInjectionPoints = true;
                    return true;
                }
                c = c.getSuperclass();
            } while( super.isWorthScanning(c) );
        }  
        return false;
    }
    
    @Override
    public boolean isWorthScanning(String annotationClassName, Class<?> c) {
        if( hasHadInjectionPoints ) {
            return super.isWorthScanning(c);
        } else if( c != null ) {
            HashSet<String> classesContainingInjectionPointsForAnnotation = mapAnnotationToclassesContainingInjectionPoints.get(annotationClassName);
            if( classesContainingInjectionPointsForAnnotation == null ) {
                return false;
            }
            do {
                if( classesContainingInjectionPointsForAnnotation.contains(c.getName()) ) {
                    hasHadInjectionPoints = true;
                    return true;
                }
                c = c.getSuperclass();
            } while( super.isWorthScanning(c) );
        }  
        return false;
    }
    
    @Override
    public void reset( ) {
        super.reset();
        hasHadInjectionPoints = false;
    }

}