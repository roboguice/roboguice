package roboguice.annotationprocessing;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.tools.JavaFileObject;

/**
 * Generates a AnnotationDatabase implementation for RoboGuice.
 * @author Mike Burton
 * @author SNI TODO use javawriter
 */
public class AnnotationDatabaseGenerator {

    public void generateAnnotationDatabase(JavaFileObject jfo, final String packageName, final HashMap<String, HashSet<String> > classesRequiringScanning, final HashSet<String> injectedClasses) throws IOException {
        final PrintWriter w = new PrintWriter(jfo.openWriter());

        if (packageName != null)
            w.println("package " + packageName + ";");

        w.println("import java.util.*;");
        w.println("import roboguice.fragment.FragmentUtil;");
        w.println();
        w.println("public class AnnotationDatabaseImpl extends roboguice.config.AnnotationDatabase {");
        w.println("    public static final HashMap<String, List<String> > classesRequiringScanning = new HashMap<String, List<String> >();");

        
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
        w.println("        List<String> classesRequiringScanningForAnnotation = new ArrayList<String>();");
        for( Map.Entry<String, HashSet<String>> classesRequiringScanningForAnnotation : classesRequiringScanning.entrySet() ) {
            w.println("        annotationClassName = \"" + classesRequiringScanningForAnnotation.getKey()+"\";" );
            w.println("        classesRequiringScanningForAnnotation.addAll(Arrays.<String>asList(");
            i = 0;
            for (String name : classesRequiringScanningForAnnotation.getValue()) {
                String commaOrNot = i < classesRequiringScanningForAnnotation.getValue().size() - 1 ? "," : "";
                w.println("            \"" + name + "\"" + commaOrNot);
                ++i;
            }
            w.println("        ));");
            w.println("        AnnotationDatabaseImpl.classesRequiringScanning.put(annotationClassName, classesRequiringScanningForAnnotation);");
            w.println("        classesRequiringScanningForAnnotation = new ArrayList<String>();");
        }
        
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
        w.println("    public HashMap<String, List<String> > getClassesContainingInjectionPoints() { return classesRequiringScanning; }");
        w.println();
        w.println("    /** The types that can be injected in fields, methods, or constructors */");
        w.println("    @Override");
        w.println("    public List<String> getInjectedClasses() { return injectedClasses; }");
        w.println("}");
        w.close();
    }
}
