package roboguice.inject;

import android.content.Context;

import com.google.inject.Inject;
import com.google.inject.Provider;


public class RoboProvider<T> {
    @Inject protected ContextScope scope;
    @Inject protected Provider<T> provider;

    T get(Context context) {
        synchronized (ContextScope.class) {
            scope.enter(context);
            return provider.get();
        }
    }
}
