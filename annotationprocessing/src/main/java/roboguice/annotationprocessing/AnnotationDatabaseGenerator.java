package roboguice.annotationprocessing;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

import javax.tools.JavaFileObject;

/**
 * Generates a AnnotationDatabase implementation for RoboGuice.
 * @author Mike Burton
 * @author SNI
 * TODO use javawriter
 */
public class AnnotationDatabaseGenerator {
    
    public void generateAnnotationDatabase(JavaFileObject jfo, final String packageName, final HashSet<String> classesRequiringScanning, final HashSet<String> injectedClasses) throws IOException {
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
        w.println("    public static final List<String> injectedClasses = new ArrayList<String>(Arrays.<String>asList(");

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
    }
}
