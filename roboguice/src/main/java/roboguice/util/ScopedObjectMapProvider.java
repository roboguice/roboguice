package roboguice.util;

import com.google.inject.Key;

import java.util.Map;

public interface ScopedObjectMapProvider {
    Map<Key<?>,Object> getScopedObjectMap();
}
