package roboguice.inject;

import android.content.Context;
import com.google.inject.Key;
import java.lang.ref.WeakReference;
import java.util.Map;

public class ScopedObjects {
    private WeakReference<Context> contextWeakReference;
    private Map<Key<?>, Object> scopedObjects;

    public ScopedObjects(WeakReference<Context> contextWeakReference, Map<Key<?>, Object> scopedObjects) {
        this.contextWeakReference = contextWeakReference;
        this.scopedObjects = scopedObjects;
    }

    public WeakReference<Context> getContextWeakReference() {
        return contextWeakReference;
    }

    public Map<Key<?>, Object> getScopedObjects() {
        return scopedObjects;
    }
}
