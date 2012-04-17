package roboguice.application;

import roboguice.util.RoboContext;

import android.app.Application;

import com.google.inject.Key;

import java.util.HashMap;
import java.util.Map;

public class RoboApplication extends Application implements RoboContext {
    protected HashMap<Key<?>,Object> scopedObjects = new HashMap<Key<?>, Object>();

    @Override
    public Map<Key<?>, Object> getScopedObjectMap() {
        return scopedObjects;
    }

}
