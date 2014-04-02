package roboguice.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    private static HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToFieldSet;
    private static HashSet<String> classesContainingInjectionPointsSet = new HashSet<String>();

    public  AnnotatedRoboGuiceHierarchyTraversalFilter(HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToFieldSet) {
        if(mapAnnotationToMapClassWithInjectionNameToFieldSet.isEmpty())
            throw new IllegalStateException("Unable to find Annotation Database which should be output as part of annotation processing");

        AnnotatedRoboGuiceHierarchyTraversalFilter.mapAnnotationToMapClassWithInjectionNameToFieldSet = mapAnnotationToMapClassWithInjectionNameToFieldSet;
        for( Map<String, Set<String>> entryAnnotationToclassesContainingInjectionPoints : mapAnnotationToMapClassWithInjectionNameToFieldSet.values() ) {
            classesContainingInjectionPointsSet.addAll(entryAnnotationToclassesContainingInjectionPoints.keySet());
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
        Map<String, Set<String>> classesContainingInjectionPointsForAnnotation;

        if( hasHadInjectionPoints ) {
            return super.isWorthScanning(c);
        } else if( c != null && (classesContainingInjectionPointsForAnnotation = mapAnnotationToMapClassWithInjectionNameToFieldSet.get(annotationClassName)) != null ) {
            if( classesContainingInjectionPointsForAnnotation.containsKey(c.getName())) {
                hasHadInjectionPoints = true;
                return true;
            }
        }
        return false;
    }

    public Set<String> getAllFields(String annotationClassName, Class<?> c) {
        Map<String, Set<String>> classesContainingInjectionPointsForAnnotation = mapAnnotationToMapClassWithInjectionNameToFieldSet.get(annotationClassName);

        if( c != null && classesContainingInjectionPointsForAnnotation!= null ) {
            return classesContainingInjectionPointsForAnnotation.get(c.getName());
        }
        //costly but should not happen
        return Collections.emptySet();
    }

    public void reset( ) {
        super.reset();
        hasHadInjectionPoints = false;
    }

}