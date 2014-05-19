package roboguice.roboblender;


import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

import com.google.inject.blender.GuiceAnnotationProcessor;

/**
 * An annotation processor that detects classes that need to receive injections.
 * It is a {@link GuiceAnnotationProcessor} that is triggered for all the annotations 
 * of both Guice and RoboGuice. 
 * @author MikeBurton
 * @author SNI
 */
@SupportedAnnotationTypes({"com.google.inject.Inject", "com.google.inject.Provides", "javax.inject.Inject", "roboguice.inject.InjectView", "roboguice.inject.InjectResource", "roboguice.inject.InjectPreference", "roboguice.inject.InjectExtra", "roboguice.inject.InjectFragment", "roboguice.event.Observes", "roboguice.inject.ContentView"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedOptions({"guiceAnnotationDatabasePackageName"})
public class RoboGuiceAnnotationProcessor extends GuiceAnnotationProcessor {
}
