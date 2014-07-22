package roboguice.roboblender;


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
@SupportedAnnotationTypes({"com.google.inject.Inject", "com.google.inject.Provides", "javax.inject.Inject", "roboguice.inject.InjectView", "roboguice.inject.InjectResource", "roboguice.inject.InjectPreference", "roboguice.inject.InjectExtra", "roboguice.inject.InjectFragment", "roboguice.event.Observes", "roboguice.inject.ContentView"})
@SupportedOptions({"guiceAnnotationDatabasePackageName"})
public class RoboGuiceAnnotationProcessor extends GuiceAnnotationProcessor {
	
	@Override
	public SourceVersion getSupportedSourceVersion() {
		//http://stackoverflow.com/a/8188860/693752
		return SourceVersion.latest();
	}
}
