package roboguice.annotationprocessing;

import roboguice.util.Strings;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@SupportedAnnotationTypes({"com.google.inject.Inject","javax.inject.Inject"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ClassesRequiringScanningProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        // Not sure why, but sometimes we're getting called with an empty list of annotations.
        if(annotations.isEmpty())
            return true;

        final HashSet<TypeElement> classesRequiringScanning = new HashSet<TypeElement>();

        // Get the enclosing class for each annotated method, constructor, or field.
        for( TypeElement annotation : annotations )
            for( Element injectionPoint : roundEnv.getElementsAnnotatedWith(annotation))
                classesRequiringScanning.add( (TypeElement) injectionPoint.getEnclosingElement() );

        try {
            final JavaFileObject jfo = processingEnv.getFiler().createSourceFile( "AnnotationDatabaseImpl" );
            final PrintWriter w = new PrintWriter(jfo.openWriter());
            final ArrayList<String> classNames = new ArrayList<String>(classesRequiringScanning.size());
            for( TypeElement clazz : classesRequiringScanning )
                classNames.add(clazz.getQualifiedName() + ".class");

            w.println("import java.util.*;");
            w.println();
            w.println("public class AnnotationDatabaseImpl extends roboguice.AnnotationDatabase {");
            w.println("    public static final List<Class<?>> classes = Arrays.<Class<?>>asList(");
            w.println("            " + Strings.join(",\n            ",classNames) + "\n");
            w.println("    );");

            w.println();
            w.println("    @Override");
            w.println("    public List<Class<?>> classes() { return classes; }");
            w.println("}");
            w.close();

        } catch( IOException e ) {
            throw new RuntimeException(e);
        }


        return true;
    }
}
