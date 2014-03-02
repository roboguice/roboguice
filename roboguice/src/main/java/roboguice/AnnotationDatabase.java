package roboguice;

import java.util.List;

public abstract class AnnotationDatabase {
    abstract protected List<Class<?>> classes();

    private static final AnnotationDatabase instance;

    static {
        try {
            instance = (AnnotationDatabase) Class.forName("AnnotationDatabaseImpl").newInstance();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    protected AnnotationDatabase() {}

    public static List<Class<?>> getClasses() {
        return instance.classes();
    }

}
