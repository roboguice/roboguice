package roboguice;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AnnotationDatabase {
    abstract protected List<String> classes();

    protected AnnotationDatabase() {}

    // BUG needs a little cleanup and optimization
    public static Set<String> getClasses(String... additionalPackageNames) {
        try {
            final HashSet<String> set = new HashSet<String>(((AnnotationDatabase) Class.forName("AnnotationDatabaseImpl").newInstance()).classes());

            for( String pkg : additionalPackageNames )
                set.addAll(((AnnotationDatabase) Class.forName( pkg + ".AnnotationDatabaseImpl").newInstance()).classes());

            return set;
        } catch( Exception e ) {
            throw new RuntimeException(e);
        }

    }

}
