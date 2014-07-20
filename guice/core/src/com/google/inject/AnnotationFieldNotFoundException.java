package com.google.inject;

/**
 * Thrown a field is present inside {@link AnnotationDatabase}
 * but not found inside a given class.
 * @author SNI
 */
@SuppressWarnings("serial")
public class AnnotationFieldNotFoundException extends Exception {

	public AnnotationFieldNotFoundException() {
		super();
	}

	public AnnotationFieldNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public AnnotationFieldNotFoundException(String arg0) {
		super(arg0);
	}

	public AnnotationFieldNotFoundException(Throwable arg0) {
		super(arg0);
	}
}
