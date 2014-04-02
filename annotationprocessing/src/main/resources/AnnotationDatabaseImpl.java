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
