package roboguice.annotationprocessing;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.tools.JavaFileObject;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * Generates a AnnotationDatabase implementation for RoboGuice.
 * @author Mike Burton
 * @author SNI TODO use javawriter
 */
public class AnnotationDatabaseGenerator {

    public void generateAnnotationDatabase(JavaFileObject jfo, final String packageName, final HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToFieldSet,
            HashMap<String, Map<String, Set<String>>> mapAnnotationToMapClassWithInjectionNameToMethodSet, final HashSet<String> injectedClasses) throws IOException {

        Properties props = new Properties();
        props.put("resource.loader", "class");
        props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(props);

        
        VelocityContext context = new VelocityContext();

        context.put("packageName", packageName);
        context.put("mapAnnotationToMapClassWithInjectionNameToFieldSet", mapAnnotationToMapClassWithInjectionNameToFieldSet);
        context.put("mapAnnotationToMapClassWithInjectionNameToMethodSet", mapAnnotationToMapClassWithInjectionNameToMethodSet);
        context.put("injectedClasses", injectedClasses);

        Template template = null;

        PrintWriter w =  null;
        try {
            template = Velocity.getTemplate("AnnotationDatabaseImpl.java");
            w = new PrintWriter(jfo.openWriter());
            template.merge(context, w);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if( w != null ) {
                try {
                    w.close();
                } catch( Exception ex ) {
                    ex.printStackTrace();
                }
            }
        }

        // final PrintWriter w = new PrintWriter(jfo.openWriter());
        //
        // if (packageName != null)
        // w.println("package " + packageName + ";");
        //
        // w.println("import java.util.*;");
        // w.println("import roboguice.fragment.FragmentUtil;");
        // w.println("import roboguice.annotationprocessing.InjectionPointDescription;");
        // w.println();
        // w.println("public class AnnotationDatabaseImpl extends roboguice.config.AnnotationDatabase {");
        // w.println("    public static final HashMap<String, Map<String, Set<String>> classesRequiringScanning = new HashMap<String, Map<String, Set<String> >();");
        //
        //
        // w.println();
        // w.println("    public static final List<String> injectedClasses = new ArrayList<String>(Arrays.<String>asList(");
        //
        // int i = 0;
        // for (String name : injectedClasses) {
        // w.println("            \"" + name + (i < injectedClasses.size() - 1 ? "\"," : "\""));
        // ++i;
        // }
        //
        // w.println("    ));");
        // w.println();
        //
        // w.println("    public void fill(HashMap<String, Map<String, Set<String>> map) {");
        // w.println("        String annotationClassName = \"\";");
        // w.println("        Map<String, Set<String> mapClassWithInjectionNameToFieldSet = null;");
        // for( Map.Entry<String, Map<String, Set<String>>> classesRequiringScanningForAnnotation :
        // mapAnnotationToMapClassWithInjectionNameToFieldSet.entrySet() ) {
        // w.println("        String annotationClassName = \"" +
        // classesRequiringScanningForAnnotation.getKey()+"\";");
        // w.println("        Map<String, Set<String> mapClassWithInjectionNameToFieldSet = null;");
        // }
        // w.println("   }");
        // // BUG HACK, need to figure out why i have to manually add these
        // w.println("   static {");
        // w.println("        String annotationClassName = \"\";");
        // w.println("        Map<String, Set<String> mapClassWithInjectionNameToFieldSet = null;");
        // for( Map.Entry<String, Map<String, Set<String>>> classesRequiringScanningForAnnotation :
        // mapAnnotationToMapClassWithInjectionNameToFieldSet.entrySet() ) {
        // w.println();
        // w.println("        classesRequiringScanningForAnnotation = new ArrayList<InjectionPointDescription>();");
        // w.println("        annotationClassName = \"" +
        // classesRequiringScanningForAnnotation.getKey()+"\";" );
        // if( !classesRequiringScanningForAnnotation.getValue().isEmpty() ) {
        // w.println("        classesRequiringScanningForAnnotation.addAll(Arrays.<InjectionPointDescription>asList(");
        // i = 0;
        // for (InjectionPointDescription injectionPointDescription :
        // classesRequiringScanningForAnnotation.getValue()) {
        // String commaOrNot = i < classesRequiringScanningForAnnotation.getValue().size() - 1 ? ","
        // : "";
        // w.println("            new InjectionPointDescription(\"" +
        // injectionPointDescription.getClassName() + "\", Arrays.<String>asList(");
        // int j = 0;
        // for (String fieldName : injectionPointDescription.getListOfFieldNames() ) {
        // String commaOrNot2 = j < injectionPointDescription.getListOfFieldNames().size() - 1 ? ","
        // : "";
        // w.println("            \"" + fieldName + "\"" + commaOrNot2);
        // ++j;
        // }
        // w.println("            ))"+ commaOrNot);
        // ++i;
        // }
        // w.println( "        ));" );
        // }
        // w.println("        AnnotationDatabaseImpl.classesRequiringScanning.put(annotationClassName, classesRequiringScanningForAnnotation);");
        // }
        //
        // w.println();
        // w.println("        if(FragmentUtil.hasNative) {");
        // w.println("            injectedClasses.add(\"android.app.FragmentManager\");");
        // w.println("        }");
        //
        // w.println("        if(FragmentUtil.hasSupport) {");
        // w.println("            injectedClasses.add(\"android.support.v4.app.FragmentManager\");");
        // w.println("        }");
        // w.println("   }");
        // w.println();
        //
        // w.println("    /** The classes that have fields, methods, or constructors annotated with RoboGuice annotations */");
        // w.println("    @Override");
        // w.println("    public HashMap<String, List<InjectionPointDescription> > getClassesContainingInjectionPoints() { return classesRequiringScanning; }");
        // w.println();
        // w.println("    /** The types that can be injected in fields, methods, or constructors */");
        // w.println("    @Override");
        // w.println("    public List<String> getInjectedClasses() { return injectedClasses; }");
        // w.println("}");
        // w.close();
    }
}
