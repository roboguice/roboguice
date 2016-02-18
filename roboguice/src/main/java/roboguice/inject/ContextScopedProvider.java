package roboguice.inject;

import com.google.inject.Key;
import java.util.Map;
import roboguice.RoboGuice;

import com.google.inject.Inject;
import com.google.inject.Provider;

import android.content.Context;


public class ContextScopedProvider<T> {
    @Inject protected Provider<T> provider;

    public T get(Context context) {
        //see https://github.com/roboguice/roboguice/issues/112
        final ContextScopedRoboInjector contextScopedRoboInjector = RoboGuice.getInjector(context);
        final ContextScope scope = contextScopedRoboInjector.getContextScope();
        final Map<Key<?>, Object> scopedObjects = contextScopedRoboInjector.getScopedObjects();
        synchronized (ContextScope.class) {
            scope.enter(context, scopedObjects);
            try {
                return provider.get();
            } finally {
                scope.exit(context);
            }
        }
    }
}
