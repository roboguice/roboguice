package roboguice.config;

import com.google.inject.HierarchyTraversalFilter;
import com.google.inject.HierarchyTraversalFilterFactory;

public class RoboGuiceHierarchyTraversalFilterFactory extends HierarchyTraversalFilterFactory {
    @Override
    public HierarchyTraversalFilter createHierarchyTraversalFilter() {
        return new RoboGuiceHierarchyTraversalFilter();
    }
}
