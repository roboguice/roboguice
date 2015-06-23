package roboguice.roboblender;


import com.google.inject.blender.AnnotationDatabaseGenerator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;

import com.google.inject.blender.GuiceAnnotationProcessor;

/**
 * An annotation processor that detects classes that need to receive injections.
 * It is a {@link GuiceAnnotationProcessor} that is triggered for all the annotations
 * of both Guice and RoboGuice.
 * @author MikeBurton
 * @author SNI
 */
@SupportedAnnotationTypes({"com.google.inject.Inject", "com.google.inject.Provides", "javax.inject.Inject"})
@SupportedOptions({"guiceAnnotationDatabasePackageName"})
public class RoboGuiceAnnotationProcessor extends GuiceAnnotationProcessor {

    public static final String TEMPLATE_ANNOTATION_DATABASE_PATH = "templates/RGAnnotationDatabaseImpl.vm";

	@Override
	public SourceVersion getSupportedSourceVersion() {
		//http://stackoverflow.com/a/8188860/693752
		return SourceVersion.latest();
	}

    @Override
    protected AnnotationDatabaseGenerator createAnnotationDatabaseGenerator() {
        return new RoboGuiceAnnotationDatabaseGenerator();
    }

    @Override
    protected void configure(AnnotationDatabaseGenerator annotationDatabaseGenerator) {
        super.configure(annotationDatabaseGenerator);
        annotationDatabaseGenerator.setTemplatePath(TEMPLATE_ANNOTATION_DATABASE_PATH);
    }

}
