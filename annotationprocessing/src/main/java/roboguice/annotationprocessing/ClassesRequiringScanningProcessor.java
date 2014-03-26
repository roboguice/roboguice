package roboguice.annotationprocessing;


import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
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
    private HashMap<String, HashSet<String> > classesRequiringScanning;
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


        classesRequiringScanning = new HashMap<String, HashSet<String> >();
        injectedClasses = new HashSet<String>();

        for( TypeElement annotation : annotations ) {
            String annotationClassName = annotation.getQualifiedName().toString();
            for( Element injectionPoint : roundEnv.getElementsAnnotatedWith(annotation)) {
                // Get the enclosing class for each annotated method, constructor, or field.
                //might need some looping for @Observes as the enclosing element is a method
                Element enclosing = injectionPoint;
                while( ! (enclosing.getEnclosingElement() instanceof TypeElement) ) {
                    enclosing = enclosing.getEnclosingElement();
                }
                TypeElement typeElementRequiringScanning = (TypeElement) enclosing.getEnclosingElement();
                String typeElementName = typeElementRequiringScanning.getQualifiedName().toString();
                
                HashSet<String> classesRequiringScanningForAnnotation = classesRequiringScanning.get( annotationClassName );
                if( classesRequiringScanningForAnnotation == null ) {
                    classesRequiringScanningForAnnotation = new HashSet<String>();
                }
                classesRequiringScanningForAnnotation.add(typeElementName);
                classesRequiringScanning.put(annotationClassName, classesRequiringScanningForAnnotation);

                // Get the injected field types
                if( injectionPoint instanceof VariableElement ) {
                    final TypeMirror fieldTypeMirror = injectionPoint.asType();
                    if( fieldTypeMirror instanceof DeclaredType )
                        injectedClasses.add( ((TypeElement)((DeclaredType)fieldTypeMirror).asElement()).getQualifiedName().toString() );
                    else if( fieldTypeMirror instanceof PrimitiveType )
                        injectedClasses.add( fieldTypeMirror.getKind().name() );


                // Get the injected method and constructor types
                } else if( injectionPoint instanceof ExecutableElement ) {
                    for( VariableElement variable : ((ExecutableElement)injectionPoint).getParameters() )
                        injectedClasses.add( ((TypeElement)((DeclaredType)variable.asType()).asElement()).getQualifiedName().toString() );
                }

            }
        }

        JavaFileObject jfo;
        try {
            jfo = processingEnv.getFiler().createSourceFile( "AnnotationDatabaseImpl" );
            annotationDatabaseGenerator.generateAnnotationDatabase(jfo, packageName, classesRequiringScanning, injectedClasses);
        } catch (IOException e) {
            e.printStackTrace();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }

        return true;
    }
}
