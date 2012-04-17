package roboguice.util;

import com.google.inject.Key;

import java.util.Map;

public interface RoboContext {
    Map<Key<?>,Object> getScopedObjectMap();
}
