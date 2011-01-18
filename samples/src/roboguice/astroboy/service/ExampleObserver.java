package roboguice.astroboy.service;

import roboguice.activity.event.OnCreateEvent;
import roboguice.activity.event.OnDestroyEvent;
import roboguice.event.Observes;
import roboguice.util.Ln;

import android.content.Context;

import com.google.inject.Inject;

/**
 * Example of the @Observes usage with the defined RoboActivity Events
 *
 * @author John Ericksen
 */
public class ExampleObserver {

    @Inject protected Context context;

    public void logOnCreate(@Observes OnCreateEvent event) {
        Ln.v("onCreate");
    }

    public void logOnDestroy(@Observes OnDestroyEvent event){
        Ln.v("onDestroy");
    }
}
