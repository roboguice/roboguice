package roboguice.annotationprocessing;


import java.io.IOException;
import java.io.PrintWriter;
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
import javax.tools.JavaFileObject;

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


        final HashSet<String> classesRequiringScanning = new HashSet<String>();
        final HashSet<String> injectedClasses = new HashSet<String>();

        for( TypeElement annotation : annotations ) {
            for( Element injectionPoint : roundEnv.getElementsAnnotatedWith(annotation)) {
                // Get the enclosing class for each annotated method, constructor, or field.
                classesRequiringScanning.add( ((TypeElement) injectionPoint.getEnclosingElement()).getQualifiedName().toString() );

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

        try {
            final JavaFileObject jfo = processingEnv.getFiler().createSourceFile( "AnnotationDatabaseImpl" );
            final PrintWriter w = new PrintWriter(jfo.openWriter());

            if( packageName!=null )
                w.println("package " + packageName + ";");

            w.println("import java.util.*;");
            w.println("import roboguice.fragment.FragmentUtil;");
            w.println();
            w.println("public class AnnotationDatabaseImpl extends roboguice.AnnotationDatabase {");
            w.println("    public static final List<String> classes = Arrays.<String>asList(");

            int i=0;
            for( String name : classesRequiringScanning ) {
                w.println("            \"" + name + (i < classesRequiringScanning.size() - 1 ? "\"," : "\""));
                ++i;
            }

            w.println("    );");
            w.println();
            w.println("    public static final List<String> injectedClasses = new ArrayList(Arrays.<String>asList(");

            i=0;
            for( String name : injectedClasses ) {
                w.println("            \"" + name + (i < injectedClasses.size() - 1 ? "\"," : "\""));
                ++i;
            }

            w.println("    ));");
            w.println();

            // BUG HACK, need to figure out why i have to manually add these
            w.println("   static {");
            w.println("        if(FragmentUtil.hasNative) {");
            w.println("            injectedClasses.add(\"android.app.FragmentManager\");");
            w.println("        }");
            
            w.println("        if(FragmentUtil.hasSupport) {");
            w.println("            injectedClasses.add(\"android.support.v4.app.FragmentManager\");");
            w.println("        }");
            w.println("   }");
            w.println();
            
            w.println("    /** The classes that have fields, methods, or constructors annotated with RoboGuice annotations */");
            w.println("    @Override");
            w.println("    public List<String> classes() { return classes; }");
            w.println();
            w.println("    /** The types that can be injected in fields, methods, or constructors */");
            w.println("    @Override");
            w.println("    public List<String> injectedClasses() { return injectedClasses; }");
            w.println("}");
            w.close();

        } catch( IOException e ) {
            throw new RuntimeException(e);
        }


        return true;
    }
}
