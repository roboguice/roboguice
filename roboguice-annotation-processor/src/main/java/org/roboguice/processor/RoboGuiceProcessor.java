package org.roboguice.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

import lombok.extern.java.Log;

import org.apache.commons.io.IOUtils;

/*
 * Annotation processor http://blog.retep
 * .org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
 * https://forums.oracle.com/thread/1184190
 */
/**
 * @author SNI
 */
@SupportedAnnotationTypes({ "com.google.inject.Inject", "javax.inject.Inject" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@Log
public class RoboGuiceProcessor extends AbstractProcessor {

    private static final String PACKAGE_SEPARATOR = ".";

    private Filer filer;
    private Messager messager;
    private Elements elements;

    private RoboGuiceInjectScanner roboGuiceInjectScanner = new RoboGuiceInjectScanner();
    private RoboModuleWriter roboModuleWriter = new RoboModuleWriter();

    @Override
    public void init(ProcessingEnvironment env) {
        filer = env.getFiler();
        messager = env.getMessager();
        elements = env.getElementUtils();
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnvironment) {
        // Get all classes that has the annotation
        // Get all classes that has the annotation
        Set<? extends Element> variableElements = roundEnvironment.getElementsAnnotatedWith(com.google.inject.Inject.class);
        Set<? extends Element> variableElements2 = roundEnvironment.getElementsAnnotatedWith(javax.inject.Inject.class);
        // For each class that has the annotation
        for (final Element variableElement : variableElements) {
            roboGuiceInjectScanner.scan(variableElement);
        }
        for (final Element variableElement : variableElements2) {
            roboGuiceInjectScanner.scan(variableElement);
        }

        for (String clazz : roboGuiceInjectScanner.getAndroidServiceClassList()) {
            System.out.println("Class detected : " + clazz);
        }

        // TODO
        String roboModulePackageName = "com.octo.android.askbob";
        String roboModuleClassName = "CustomRoboModule";
        roboModuleWriter.setAndroidServiceClassList(roboGuiceInjectScanner.getAndroidServiceClassList());
        roboModuleWriter.setRoboModulePackageName(roboModulePackageName);
        roboModuleWriter.setRoboModuleClassName(roboModuleClassName);

        // write meta model to java class file
        Writer sourceWriter = null;
        try {
            String roboModuleClassFQN = roboModulePackageName.isEmpty() ? roboModuleClassName : roboModulePackageName + PACKAGE_SEPARATOR + roboModuleClassName;
            JavaFileObject sourceFile = filer.createSourceFile(roboModuleClassFQN, (Element[]) null);
            sourceWriter = sourceFile.openWriter();

            roboModuleWriter.writeRoboModule(sourceWriter);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (sourceWriter != null) {
                IOUtils.closeQuietly(sourceWriter);
            }
        }

        return true;
    }

}
