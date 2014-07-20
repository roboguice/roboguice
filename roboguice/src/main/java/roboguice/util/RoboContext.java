package roboguice.util;

import java.util.Map;

import com.google.inject.Key;

public interface RoboContext {
    Map<Key<?>,Object> getScopedObjectMap();
}
