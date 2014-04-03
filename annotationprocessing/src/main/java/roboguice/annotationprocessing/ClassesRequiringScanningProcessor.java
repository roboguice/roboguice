package roboguice.annotationprocessing;


import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * An annotation processor that detects classes that need to receive injections.
 * @author MikeBurton
 */
@SupportedAnnotationTypes({"com.google.inject.Inject", "com.google.inject.Provides", "javax.inject.Inject", "roboguice.inject.InjectView", "roboguice.inject.InjectResource", "roboguice.inject.InjectPreference", "roboguice.inject.InjectExtra", "roboguice.inject.InjectFragment", "roboguice.event.Observes"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ClassesRequiringScanningProcessor extends AbstractProcessor {

    private AnnotationDatabaseGenerator annotationDatabaseGenerator = new AnnotationDatabaseGenerator();
    
    /**
     * Maps each annotation name to an inner map.
     * The inner map maps classes (holding injection points) names to the list of field names.
     */
    private HashMap<String, Map<String, Set<String>> > mapAnnotationToMapClassWithInjectionNameToFieldSet;
    private HashMap<String, Map<String, Set<String>> > mapAnnotationToMapClassWithInjectionNameToMethodSet;
    private HashSet<String> injectedClasses;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        // Not sure why, but sometimes we're getting called with an empty list of annotations.
        if(annotations.isEmpty())
            return true;


        // Normally we use the root package for our AnnotationDatabase.
        // This works great if there's only one.  However, if your app uses
        // a library which also uses roboguice, we'll need to put the library's
        // AnnotationDatabase somewhere else.  This can be specified using
        // the @AnnotationDatabasePackage package-level annotation
        // noinspection unchecked
        final Set<PackageElement> packages = (Set<PackageElement>) roundEnv.getElementsAnnotatedWith(AnnotationDatabasePackage.class);
        final String packageName = packages!=null && packages.size()>0 ? packages.iterator().next().getQualifiedName().toString() : null;


        mapAnnotationToMapClassWithInjectionNameToFieldSet = new HashMap<String, Map<String,Set<String>> >();
        mapAnnotationToMapClassWithInjectionNameToMethodSet = new HashMap<String, Map<String,Set<String>> >();
        injectedClasses = new HashSet<String>();

        for( TypeElement annotation : annotations ) {
            String annotationClassName = annotation.getQualifiedName().toString();
            for( Element injectionPoint : roundEnv.getElementsAnnotatedWith(annotation)) {
                // Get the enclosing class for each annotated method, constructor, or field.
                //might need some looping for @Observes as the enclosing element is a method

                String injectionPointName = "";
                // Get the injected field types
                if( injectionPoint.getEnclosingElement() instanceof TypeElement && injectionPoint instanceof VariableElement ) {

                    String injectedClassName = getTypeName(injectionPoint);
                    injectedClasses.add( injectedClassName );
                    injectionPointName = injectionPoint.getSimpleName().toString();

                    // Get the injected method and constructor types
                } else if( injectionPoint.getEnclosingElement() instanceof ExecutableElement && injectionPoint instanceof VariableElement ) {
                    Element enclosing = injectionPoint.getEnclosingElement();
                    injectionPointName = enclosing.getSimpleName().toString();
                    for( VariableElement variable : ((ExecutableElement)enclosing).getParameters() ) {
                        String parameterTypeName = getTypeName(variable);
                        injectedClasses.add( parameterTypeName );
                        injectionPointName += ":"+parameterTypeName;
                    }
                } else if( injectionPoint instanceof ExecutableElement ) {
                    injectionPointName = injectionPoint.getSimpleName().toString();
                    for( VariableElement variable : ((ExecutableElement)injectionPoint).getParameters() ) {
                        String parameterTypeName = ((TypeElement)((DeclaredType)variable.asType()).asElement()).getQualifiedName().toString();
                        injectedClasses.add( parameterTypeName );
                        injectionPointName += ":"+parameterTypeName;
                    }
                }
                
                if( injectionPoint.getEnclosingElement() instanceof TypeElement && injectionPoint instanceof VariableElement) {
                    TypeElement typeElementRequiringScanning = (TypeElement) injectionPoint.getEnclosingElement();
                    String typeElementName = typeElementRequiringScanning.getQualifiedName().toString();
                    addToInjectedFields(annotationClassName, typeElementName, injectionPointName);
                } else if ( injectionPoint.getEnclosingElement() instanceof ExecutableElement ) {
                    TypeElement typeElementRequiringScanning = (TypeElement) ((ExecutableElement) injectionPoint.getEnclosingElement()).getEnclosingElement();
                    String typeElementName = typeElementRequiringScanning.getQualifiedName().toString();
                    addToInjectedMethods(annotationClassName, typeElementName, injectionPointName );
                } else if ( injectionPoint instanceof ExecutableElement && !injectionPointName.startsWith("<init>")) {
                    TypeElement typeElementRequiringScanning = (TypeElement) injectionPoint.getEnclosingElement();
                    String typeElementName = typeElementRequiringScanning.getQualifiedName().toString();
                    addToInjectedMethods(annotationClassName, typeElementName, injectionPointName );
                }
            }
        }

        JavaFileObject jfo;
        try {
            jfo = processingEnv.getFiler().createSourceFile( "AnnotationDatabaseImpl" );
            annotationDatabaseGenerator.generateAnnotationDatabase(jfo, packageName, mapAnnotationToMapClassWithInjectionNameToFieldSet, mapAnnotationToMapClassWithInjectionNameToMethodSet, injectedClasses);
        } catch (IOException e) {
            e.printStackTrace();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }

        return true;
    }

    private String getTypeName(Element injectionPoint) {
        String injectedClassName = null;
        final TypeMirror fieldTypeMirror = injectionPoint.asType();
        if( fieldTypeMirror instanceof DeclaredType ) {
            injectedClassName = ((TypeElement)((DeclaredType)fieldTypeMirror).asElement()).getQualifiedName().toString();
        } else if( fieldTypeMirror instanceof PrimitiveType ) {
            injectedClassName = fieldTypeMirror.getKind().name();
        }
        return injectedClassName;
    }

    private void addToInjectedMethods(String annotationClassName, String typeElementName, String injectionPointName) {
        Map<String, Set<String>> mapClassWithInjectionNameToMethodSet = mapAnnotationToMapClassWithInjectionNameToMethodSet.get( annotationClassName );
        if( mapClassWithInjectionNameToMethodSet == null ) {
            mapClassWithInjectionNameToMethodSet = new HashMap<String, Set<String>>();
            mapAnnotationToMapClassWithInjectionNameToMethodSet.put(annotationClassName, mapClassWithInjectionNameToMethodSet);
        }
        
        Set<String> methodsNamesAndParamsSet = mapClassWithInjectionNameToMethodSet.get(typeElementName);
        if( methodsNamesAndParamsSet == null ) {
            methodsNamesAndParamsSet = new HashSet<String>();
            mapClassWithInjectionNameToMethodSet.put(typeElementName, methodsNamesAndParamsSet);
        }
        methodsNamesAndParamsSet.add(injectionPointName);
    }

    private void addToInjectedFields(String annotationClassName, String typeElementName, String injectionPointName) {
        Map<String, Set<String>> mapClassWithInjectionNameToFieldSet = mapAnnotationToMapClassWithInjectionNameToFieldSet.get( annotationClassName );
        if( mapClassWithInjectionNameToFieldSet == null ) {
            mapClassWithInjectionNameToFieldSet = new HashMap<String, Set<String>>();
            mapAnnotationToMapClassWithInjectionNameToFieldSet.put(annotationClassName, mapClassWithInjectionNameToFieldSet);
        }
        
        Set<String> fieldsNamesSet = mapClassWithInjectionNameToFieldSet.get(typeElementName);
        if( fieldsNamesSet == null ) {
            fieldsNamesSet = new HashSet<String>();
            mapClassWithInjectionNameToFieldSet.put(typeElementName, fieldsNamesSet);
        }
        fieldsNamesSet.add(injectionPointName);
    }
}
