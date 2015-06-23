package roboguice.service;

import android.app.IntentService;
import com.google.inject.Injector;
import com.google.inject.Key;
import java.util.HashMap;
import java.util.Map;
import roboguice.RoboGuice;
import roboguice.util.RoboContext;

/**
 * A {@link RoboIntentService} extends from {@link IntentService} to provide dynamic
 * injection of collaborators, using Google Guice.<br /> <br />
 * <p/>
 * Your own services that usually extend from {@link IntentService} should now extend from
 * {@link RoboIntentService}.<br /> <br />
 * <p/>
 * If we didn't provide what you need, you have two options : either post an issue on <a
 * href="http://code.google.com/p/roboguice/issues/list">the bug tracker</a>, or
 * implement it yourself. Have a look at the source code of this class (
 * {@link RoboIntentService}), you won't have to write that much changes. And of
 * course, you are welcome to contribute and send your implementations to the
 * RoboGuice project.<br /> <br />
 * <p/>
 *
 * @author Donn Felker
 */
public abstract class RoboIntentService extends IntentService implements RoboContext {

    protected HashMap<Key<?>, Object> scopedObjects = new HashMap<Key<?>, Object>();
    protected Injector injector;

    public RoboIntentService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        injector = RoboGuice.getInjector(this);
        injector.injectMembers(this);
    }

    @Override
    public Map<Key<?>, Object> getScopedObjectMap() {
        return scopedObjects;
    }
}
