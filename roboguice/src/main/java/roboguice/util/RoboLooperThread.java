package roboguice.util;

import android.os.Looper;

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
