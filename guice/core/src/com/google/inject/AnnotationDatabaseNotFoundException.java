package com.google.inject;

/**
 * Thrown when an annotation database is not found inside
 * current class loader / classpath.
 * @author SNI
 */
@SuppressWarnings("serial")
public class AnnotationDatabaseNotFoundException extends Exception {

	public AnnotationDatabaseNotFoundException() {
		super();
	}

	public AnnotationDatabaseNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public AnnotationDatabaseNotFoundException(String message) {
		super(message);
	}

	public AnnotationDatabaseNotFoundException(Throwable cause) {
		super(cause);
	}
}
