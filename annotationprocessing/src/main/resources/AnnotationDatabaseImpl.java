#if( $packageName ) 
    package $packageName;
#end

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import roboguice.config.AnnotationDatabase;
import roboguice.fragment.FragmentUtil;

public class AnnotationDatabaseImpl extends AnnotationDatabase {

    public void fillAnnotationClassesAndFieldsNames(HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToFieldSet) {

        String annotationClassName = null;
        Map<String, Set<String>> mapClassWithInjectionNameToFieldSet = null;
        Set<String> fieldNameSet = null;

#foreach( $annotationName in $mapAnnotationToMapClassWithInjectionNameToFieldSet.keySet() )

        annotationClassName = "$annotationName";
        mapClassWithInjectionNameToFieldSet = mapAnnotationToMapClassWithInjectionNameToFieldSet.get(annotationClassName);
        if( mapClassWithInjectionNameToFieldSet == null ) {
            mapClassWithInjectionNameToFieldSet = new HashMap<String, Set<String>>();
            mapAnnotationToMapClassWithInjectionNameToFieldSet.put(annotationClassName, mapClassWithInjectionNameToFieldSet);
        }

#foreach( $className in $mapAnnotationToMapClassWithInjectionNameToFieldSet.get($annotationName).keySet() ) 
        fieldNameSet = new HashSet<String>();
#foreach( $fieldName in $mapAnnotationToMapClassWithInjectionNameToFieldSet.get($annotationName).get($className) ) 
        fieldNameSet.add("$fieldName");
#end
        mapClassWithInjectionNameToFieldSet.put("$className", fieldNameSet);

#end
#end

    }
    
    public void fillAnnotationClassesAndMethods(HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToMethodsSet) {

        String annotationClassName = null;
        Map<String, Set<String>> mapClassWithInjectionNameToMethodSet = null;
        Set<String> methodSet = null;

#foreach( $annotationName in $mapAnnotationToMapClassWithInjectionNameToMethodSet.keySet() )

        annotationClassName = "$annotationName";
        mapClassWithInjectionNameToMethodSet = mapAnnotationToMapClassWithInjectionNameToMethodsSet.get(annotationClassName);
        if( mapClassWithInjectionNameToMethodSet == null ) {
            mapClassWithInjectionNameToMethodSet = new HashMap<String, Set<String>>();
            mapAnnotationToMapClassWithInjectionNameToMethodsSet.put(annotationClassName, mapClassWithInjectionNameToMethodSet);
        }

#foreach( $className in $mapAnnotationToMapClassWithInjectionNameToMethodSet.get($annotationName).keySet() ) 
        methodSet = new HashSet<String>();
#foreach( $method in $mapAnnotationToMapClassWithInjectionNameToMethodSet.get($annotationName).get($className) ) 
        methodSet.add("$method");
#end
        mapClassWithInjectionNameToMethodSet.put("$className", methodSet);

#end
#end

    }

    public void fillInjectableClasses(HashSet<String> injectedClasses) {
#foreach( $className in $injectedClasses )
        injectedClasses.add("$className");
#end

        if(FragmentUtil.hasNative) {
            injectedClasses.add("android.app.FragmentManager");
        }

        if(FragmentUtil.hasSupport) {
            injectedClasses.add("android.support.v4.app.FragmentManager");
        }

    }

}
