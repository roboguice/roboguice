package roboguice.config;

import java.util.HashMap;
import java.util.List;

import roboguice.annotationprocessing.InjectionPointDescription;

public abstract class AnnotationDatabase {
    //TODO add the additional packages here and load database impl classes.
    protected AnnotationDatabase() {}

    //TODO rename methods
    abstract protected HashMap<String, List<InjectionPointDescription>> getClassesContainingInjectionPoints();
    abstract protected List<String> getInjectedClasses();


}
