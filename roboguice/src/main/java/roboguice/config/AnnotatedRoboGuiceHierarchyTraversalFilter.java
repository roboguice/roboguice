package roboguice.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import roboguice.annotationprocessing.InjectionPointDescription;

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
    private static HashMap<String, HashSet<InjectionPointDescription>> mapAnnotationToclassesContainingInjectionPoints;
    private static HashSet<String> classesContainingInjectionPointsSet = new HashSet<String>();

    public  AnnotatedRoboGuiceHierarchyTraversalFilter(HashMap<String, HashSet<InjectionPointDescription>> mapAnnotationToclassesContainingInjectionPoints) {
        if(mapAnnotationToclassesContainingInjectionPoints.isEmpty())
            throw new IllegalStateException("Unable to find Annotation Database which should be output as part of annotation processing");

        AnnotatedRoboGuiceHierarchyTraversalFilter.mapAnnotationToclassesContainingInjectionPoints = mapAnnotationToclassesContainingInjectionPoints;
        for( Entry<String, HashSet<InjectionPointDescription>> entryAnnotationToclassesContainingInjectionPoints : mapAnnotationToclassesContainingInjectionPoints.entrySet() ) {
            for( InjectionPointDescription ip : entryAnnotationToclassesContainingInjectionPoints.getValue() ) {
                classesContainingInjectionPointsSet.add(ip.getClassName());
            }
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
    public List<String> getAllFields(String annotationClassName, Class<?> c) {
        HashSet<InjectionPointDescription> classesContainingInjectionPointsForAnnotation = mapAnnotationToclassesContainingInjectionPoints.get(annotationClassName);
                
        if( c != null && classesContainingInjectionPointsForAnnotation!= null ) {
            for( InjectionPointDescription ip : classesContainingInjectionPointsForAnnotation ) {
                if( ip.getClassName().equals(c.getName())) {
                    return ip.getListOfFieldNames();
                }
            }
        }
        //costly but should not happen
        return new ArrayList<String>();
    }

    public void reset( ) {
        super.reset();
        hasHadInjectionPoints = false;
    }

}