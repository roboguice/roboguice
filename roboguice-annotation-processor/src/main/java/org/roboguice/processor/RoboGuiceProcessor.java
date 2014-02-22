package org.roboguice.processor;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

import lombok.extern.java.Log;

/*
 * Annotation processor http://blog.retep
 * .org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
 * https://forums.oracle.com/thread/1184190
 */
/**
 * @author SNI
 */
@SupportedAnnotationTypes("com.google.inject")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@Log
public class RoboGuiceProcessor {

}
