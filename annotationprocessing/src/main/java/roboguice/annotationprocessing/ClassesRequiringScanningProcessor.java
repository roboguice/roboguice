package roboguice.annotationprocessing;


import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

@SupportedAnnotationTypes({"com.google.inject.Inject", "com.google.inject.Provides", "javax.inject.Inject", "roboguice.inject.InjectView", "roboguice.inject.InjectResource", "roboguice.inject.InjectPreference", "roboguice.inject.InjectExtra", "roboguice.inject.InjectFragment"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ClassesRequiringScanningProcessor extends AbstractProcessor {

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
        //noinspection unchecked
        final Set<PackageElement> packages = (Set<PackageElement>) roundEnv.getElementsAnnotatedWith(AnnotationDatabasePackage.class);
        final String packageName = packages!=null && packages.size()>0 ? packages.iterator().next().getQualifiedName().toString() : null;


        final HashSet<TypeElement> classesRequiringScanning = new HashSet<TypeElement>();

        // Get the enclosing class for each annotated method, constructor, or field.
        for( TypeElement annotation : annotations )
            for( Element injectionPoint : roundEnv.getElementsAnnotatedWith(annotation))
                classesRequiringScanning.add( (TypeElement) injectionPoint.getEnclosingElement() );

        try {
            final JavaFileObject jfo = processingEnv.getFiler().createSourceFile( "AnnotationDatabaseImpl" );
            final PrintWriter w = new PrintWriter(jfo.openWriter());

            if( packageName!=null )
                w.println("package " + packageName + ";");

            w.println("import java.util.*;");
            w.println();
            w.println("public class AnnotationDatabaseImpl extends roboguice.AnnotationDatabase {");
            w.println("    public static final List<String> classes = Arrays.<String>asList(");

            int i=0;
            for( TypeElement clazz : classesRequiringScanning ) {
                w.println("            \"" + clazz.getQualifiedName() + (i < classesRequiringScanning.size() - 1 ? "\"," : "\""));
                ++i;
            }

            w.println("    );");

            w.println();
            w.println("    @Override");
            w.println("    public List<String> classes() { return classes; }");
            w.println("}");
            w.close();

        } catch( IOException e ) {
            throw new RuntimeException(e);
        }


        return true;
    }
}
