package roboguice;

import java.util.List;

public abstract class AnnotationDatabase {
    //TODO add the additional packages here and load database impl classes.
    protected AnnotationDatabase() {}

    //TODO rename methods
    abstract protected List<String> getClassesContainingInjectionPoints();
    abstract protected List<String> getInjectedClasses();


}
