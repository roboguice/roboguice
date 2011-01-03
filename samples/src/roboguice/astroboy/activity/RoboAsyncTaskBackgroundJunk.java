package roboguice.astroboy.activity;

import roboguice.activity.event.OnDestroyEvent;
import roboguice.activity.event.OnKeyDownEvent;
import roboguice.event.Observes;
import roboguice.util.Ln;
import roboguice.util.RoboAsyncTask;

/**
 * @author John Ericksen
 */
public class RoboAsyncTaskBackgroundJunk extends RoboAsyncTask<Void> {

    public Void call() throws Exception {
        Ln.d("Doing some junk in background thread %s", this);
        Thread.sleep(10*1000);
        return null;
    }

    public void onKeyDown(@Observes OnKeyDownEvent keyDownEvent){
        Ln.v("onKeyDown");
    }

    protected void onActivityDestroy(@Observes OnDestroyEvent ignored ) {
        Ln.d("Killing background thread %s", this);
        cancel(true);
    }
}
