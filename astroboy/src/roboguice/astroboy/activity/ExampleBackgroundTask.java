package roboguice.astroboy.activity;

import roboguice.activity.event.OnDestroyEvent;
import roboguice.event.Observes;
import roboguice.util.Ln;
import roboguice.util.RoboAsyncTask;

/**
 * @author John Ericksen
 */
public class ExampleBackgroundTask extends RoboAsyncTask<Void> {

    public Void call() throws Exception {
        Ln.d("Doing some junk in background thread %s", this);
        Thread.sleep(10*1000);
        return null;
    }

    @Override
    protected void onInterrupted(Exception e) {
        Ln.d("Interrupting background task %s", this);
    }

    // If the activity is destroyed, this handler will make sure
    // that this background task gets canceled.
    protected void onActivityDestroy(@Observes OnDestroyEvent ignored ) {
        Ln.d("Killing background thread %s", this);
        cancel(true);
    }
}
