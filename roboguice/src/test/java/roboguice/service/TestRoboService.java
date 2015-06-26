package roboguice.service;

import android.app.Service;
import com.google.inject.Injector;
import com.google.inject.Key;
import java.util.HashMap;
import roboguice.RoboGuice;

/**
 * A {@link TestRoboService} extends from {@link Service} to provide dynamic
 * injection of collaborators, using Google Guice.<br /> <br />
 *
 * Your own services that usually extend from {@link Service} should now extend from
 * {@link TestRoboService}.<br /> <br />
 *
 * If we didn't provide what you need, you have two options : either post an issue on <a
 * href="http://code.google.com/p/roboguice/issues/list">the bug tracker</a>, or
 * implement it yourself. Have a look at the source code of this class (
 * {@link TestRoboService}), you won't have to write that much changes. And of
 * course, you are welcome to contribute and send your implementations to the
 * RoboGuice project.<br /> <br />
 *
 * @author Mike Burton
 * @author Christine Karman
 */
public abstract class TestRoboService extends Service {

    protected Injector injector;

    @Override
    public void onCreate() {
        super.onCreate();
        if (injector == null) {
            injector = RoboGuice.getInjector(this);
        }
        injector.injectMembers(this);
    }

    @Override
    public void onDestroy() {
        RoboGuice.destroyInjector(this);
        super.onDestroy();
    }
}
