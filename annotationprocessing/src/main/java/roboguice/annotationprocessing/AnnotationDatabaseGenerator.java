package roboguice.annotationprocessing;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.tools.JavaFileObject;
import roboguice.annotationprocessing.InjectionPointDescription;

/**
 * Generates a AnnotationDatabase implementation for RoboGuice.
 * @author Mike Burton
 * @author SNI TODO use javawriter
 */
public class AnnotationDatabaseGenerator {

    public void generateAnnotationDatabase(JavaFileObject jfo, final String packageName, final HashMap<String, List<InjectionPointDescription>> classesRequiringScanning, final HashSet<String> injectedClasses) throws IOException {
        final PrintWriter w = new PrintWriter(jfo.openWriter());

        if (packageName != null)
            w.println("package " + packageName + ";");

        w.println("import java.util.*;");
        w.println("import roboguice.fragment.FragmentUtil;");
        w.println("import roboguice.annotationprocessing.InjectionPointDescription;");
        w.println();
        w.println("public class AnnotationDatabaseImpl extends roboguice.config.AnnotationDatabase {");
        w.println("    public static final HashMap<String, List<InjectionPointDescription> > classesRequiringScanning = new HashMap<String, List<InjectionPointDescription> >();");


        w.println();
        w.println("    public static final List<String> injectedClasses = new ArrayList<String>(Arrays.<String>asList(");

        int i = 0;
        for (String name : injectedClasses) {
            w.println("            \"" + name + (i < injectedClasses.size() - 1 ? "\"," : "\""));
            ++i;
        }

        w.println("    ));");
        w.println();

        // BUG HACK, need to figure out why i have to manually add these
        w.println("   static {");
        w.println("        String annotationClassName = \"\";");
        w.println("        List<InjectionPointDescription> classesRequiringScanningForAnnotation = null;");
        for( Map.Entry<String, List<InjectionPointDescription>> classesRequiringScanningForAnnotation : classesRequiringScanning.entrySet() ) {
            w.println();
            w.println("        classesRequiringScanningForAnnotation = new ArrayList<InjectionPointDescription>();");
            w.println("        annotationClassName = \"" + classesRequiringScanningForAnnotation.getKey()+"\";" );
            if( !classesRequiringScanningForAnnotation.getValue().isEmpty() ) {
                w.println("        classesRequiringScanningForAnnotation.addAll(Arrays.<InjectionPointDescription>asList(");
                i = 0;
                for (InjectionPointDescription injectionPointDescription : classesRequiringScanningForAnnotation.getValue()) {
                    String commaOrNot = i < classesRequiringScanningForAnnotation.getValue().size() - 1 ? "," : "";
                    w.println("            new InjectionPointDescription(\"" + injectionPointDescription.getClassName() + "\", Arrays.<String>asList(");
                    int j = 0;
                    for (String fieldName : injectionPointDescription.getListOfFieldNames() ) {
                        String commaOrNot2 = j < injectionPointDescription.getListOfFieldNames().size() - 1 ? "," : "";
                        w.println("            \"" + fieldName + "\"" + commaOrNot2);
                        ++j;
                    }
                    w.println("            ))"+ commaOrNot);
                    ++i;
                }
                w.println( "        ));" );
            }
            w.println("        AnnotationDatabaseImpl.classesRequiringScanning.put(annotationClassName, classesRequiringScanningForAnnotation);");
        }

        w.println();
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
        w.println("    public HashMap<String, List<InjectionPointDescription> > getClassesContainingInjectionPoints() { return classesRequiringScanning; }");
        w.println();
        w.println("    /** The types that can be injected in fields, methods, or constructors */");
        w.println("    @Override");
        w.println("    public List<String> getInjectedClasses() { return injectedClasses; }");
        w.println("}");
        w.close();
    }
}
