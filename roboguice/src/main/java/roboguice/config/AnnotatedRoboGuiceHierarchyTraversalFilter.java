package roboguice.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
    private static HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToConstructorSet;
    private static HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToMethodSet;
    private static HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToFieldSet;
    private static HashSet<String> classesContainingInjectionPointsSet = new HashSet<String>();

    public  AnnotatedRoboGuiceHierarchyTraversalFilter(HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToFieldSet, HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToMethodSet, HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToConstructorSet) {
        if(mapAnnotationToMapClassWithInjectionNameToFieldSet.isEmpty())
            throw new IllegalStateException("Unable to find Annotation Database which should be output as part of annotation processing");

        AnnotatedRoboGuiceHierarchyTraversalFilter.mapAnnotationToMapClassWithInjectionNameToFieldSet = mapAnnotationToMapClassWithInjectionNameToFieldSet;
        for( Map<String, Set<String>> entryAnnotationToclassesContainingInjectionPoints : mapAnnotationToMapClassWithInjectionNameToFieldSet.values() ) {
            classesContainingInjectionPointsSet.addAll(entryAnnotationToclassesContainingInjectionPoints.keySet());
        }
        AnnotatedRoboGuiceHierarchyTraversalFilter.mapAnnotationToMapClassWithInjectionNameToMethodSet = mapAnnotationToMapClassWithInjectionNameToMethodSet;
        for( Map<String, Set<String>> entryAnnotationToclassesContainingInjectionPoints : mapAnnotationToMapClassWithInjectionNameToMethodSet.values() ) {
            classesContainingInjectionPointsSet.addAll(entryAnnotationToclassesContainingInjectionPoints.keySet());
        }
        AnnotatedRoboGuiceHierarchyTraversalFilter.mapAnnotationToMapClassWithInjectionNameToConstructorSet = mapAnnotationToMapClassWithInjectionNameToConstructorSet;
        for( Map<String, Set<String>> entryAnnotationToclassesContainingInjectionPoints : mapAnnotationToMapClassWithInjectionNameToConstructorSet.values() ) {
            classesContainingInjectionPointsSet.addAll(entryAnnotationToclassesContainingInjectionPoints.keySet());
        }
    }

    @Override
    public boolean isWorthScanning(Class<?> c) {
        if( hasHadInjectionPoints ) {
            return super.isWorthScanning(c);
        } else if( c != null ) {
            do {
                String name = c.getName().replace('$', '.');
                if( classesContainingInjectionPointsSet.contains(name) ) {
                    hasHadInjectionPoints = true;
                    return true;
                }
                c = c.getSuperclass();
            } while( super.isWorthScanning(c) );
        }  
        return false;
    }

    @Override
    public boolean isWorthScanningForFields(String annotationClassName, Class<?> c) {
        Map<String, Set<String>> classesContainingInjectionPointsForAnnotation;

        if( hasHadInjectionPoints ) {
            return super.isWorthScanning(c);
        } else if( c != null ) {
            classesContainingInjectionPointsForAnnotation = mapAnnotationToMapClassWithInjectionNameToFieldSet.get(annotationClassName);
            if( classesContainingInjectionPointsForAnnotation == null ) {
                return false;
            }
            do {
                String name = c.getName().replace('$', '.');
                if( classesContainingInjectionPointsForAnnotation.containsKey(name) ) {
                    hasHadInjectionPoints = true;
                    return true;
                }
                c = c.getSuperclass();
            } while( super.isWorthScanning(c) );
        }  
        return false;
    }

    public Set<Field> getAllFields(String annotationClassName, Class<?> c) {
        Map<String, Set<String>> classesContainingInjectionPointsForAnnotation = mapAnnotationToMapClassWithInjectionNameToFieldSet.get(annotationClassName);

        if( c != null && classesContainingInjectionPointsForAnnotation!= null ) {
            String name = c.getName().replace('$', '.');
            Set<String> fieldNameSet = classesContainingInjectionPointsForAnnotation.get(name);
            if( fieldNameSet != null ) {
                Set<Field> fieldSet = new HashSet<Field>();
                try {
                    for( String fieldName : fieldNameSet ) {
                        fieldSet.add( c.getDeclaredField(fieldName));
                    }
                    return fieldSet;
                } catch( Exception ex ) {
                    ex.printStackTrace();
                }
            }
        }
        //costly but should not happen
        return Collections.emptySet();
    }

    @Override
    public boolean isWorthScanningForMethods(String annotationClassName, Class<?> c) {
        Map<String, Set<String>> classesContainingInjectionPointsForAnnotation;

        if( hasHadInjectionPoints ) {
            return super.isWorthScanning(c);
        } else if( c != null ) {
            classesContainingInjectionPointsForAnnotation = mapAnnotationToMapClassWithInjectionNameToMethodSet.get(annotationClassName);
            if( classesContainingInjectionPointsForAnnotation == null ) {
                return false;
            }
            do {
                String name = c.getName().replace('$', '.');
                if( classesContainingInjectionPointsForAnnotation.containsKey(name) ) {
                    hasHadInjectionPoints = true;
                    return true;
                }
                c = c.getSuperclass();
            } while( super.isWorthScanning(c) );
        }  
        return false;
    }
    
    public Set<Method> getAllMethods(String annotationClassName, Class<?> c) {
        
        //System.out.printf("map of methods : %s \n",mapAnnotationToMapClassWithInjectionNameToMethodSet.toString());
        Map<String, Set<String>> classesContainingInjectionPointsForAnnotation = mapAnnotationToMapClassWithInjectionNameToMethodSet.get(annotationClassName);

        if( c != null && classesContainingInjectionPointsForAnnotation!= null ) {
            String name = c.getName().replace('$', '.');
            Set<String> methodNameSet = classesContainingInjectionPointsForAnnotation.get(name);
            if( methodNameSet != null ) {
                Set<Method> methodSet = new HashSet<Method>();
                try {
                    for( String methodNameAndParamClasses : methodNameSet ) {
                        //System.out.printf("Getting method %s of class %s \n",methodNameAndParamClasses,c.getName());
                        String[] split = methodNameAndParamClasses.split(":");
                        String methodName = split[0];
                        Class[] paramClass = new Class[split.length-1];
                        for( int i=1;i<split.length;i++) {
                            paramClass[i-1] = getClass().getClassLoader().loadClass(split[1]);
                        }
                        methodSet.add( c.getDeclaredMethod(methodName, paramClass));
                    }
                    return methodSet;
                } catch( Exception ex ) {
                    ex.printStackTrace();
                }
            }
        }
        //costly but should not happen
        return Collections.emptySet();
    }

    @Override
    public boolean isWorthScanningForConstructors(String annotationClassName, Class<?> c) {
        Map<String, Set<String>> classesContainingInjectionPointsForAnnotation;

        if( hasHadInjectionPoints ) {
            return super.isWorthScanning(c);
        } else if( c != null ) {
            classesContainingInjectionPointsForAnnotation = mapAnnotationToMapClassWithInjectionNameToConstructorSet.get(annotationClassName);
            if( classesContainingInjectionPointsForAnnotation == null ) {
                return false;
            }
            do {
                String name = c.getName().replace('$', '.');
                if( classesContainingInjectionPointsForAnnotation.containsKey(name) ) {
                    hasHadInjectionPoints = true;
                    return true;
                }
                c = c.getSuperclass();
            } while( super.isWorthScanning(c) );
        }  
        return false;
    }
    
    public Set<Constructor> getAllConstructors(String annotationClassName, Class<?> c) {
        
        //System.out.printf("map of methods : %s \n",mapAnnotationToMapClassWithInjectionNameToConstructorSet.toString());
        Map<String, Set<String>> classesContainingInjectionPointsForAnnotation = mapAnnotationToMapClassWithInjectionNameToConstructorSet.get(annotationClassName);

        if( c != null && classesContainingInjectionPointsForAnnotation!= null ) {
            String name = c.getName().replace('$', '.');
            Set<String> methodNameSet = classesContainingInjectionPointsForAnnotation.get(name);
            if( methodNameSet != null ) {
                Set<Constructor> methodSet = new HashSet<Constructor>();
                try {
                    for( String methodNameAndParamClasses : methodNameSet ) {
                        //System.out.printf("Getting method %s of class %s \n",methodNameAndParamClasses,c.getName());
                        String[] split = methodNameAndParamClasses.split(":");
                        Class[] paramClass = new Class[split.length-1];
                        for( int i=1;i<split.length;i++) {
                            paramClass[i-1] = getClass().getClassLoader().loadClass(split[1]);
                        }
                        methodSet.add( c.getDeclaredConstructor( paramClass));
                    }
                    return methodSet;
                } catch( Exception ex ) {
                    ex.printStackTrace();
                }
            }
        }
        //costly but should not happen
        return Collections.emptySet();
    }


    public void reset( ) {
        super.reset();
        hasHadInjectionPoints = false;
    }

}