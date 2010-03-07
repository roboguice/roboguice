package roboguice.util;

import android.os.Looper;

/**
 * An extension of {@link roboguice.util.RoboThread} which adds a Looper.
 * This allows other threads to post runnables to this thread, much like
 * the main UI thread.  Useful for testing.
 *
 * Has the same limitations as {@link roboguice.util.RoboThread}.
 */
public class RoboLooperThread extends Thread {

    public RoboLooperThread() {
    }

    public RoboLooperThread(Runnable runnable) {
        super(runnable);
    }

    @Override
    public void start() {
        new RoboThread() {
            public void run() {
                Looper.prepare();
                RoboLooperThread.this.run();
                Looper.loop();
            }
        }.start();

    }
}
