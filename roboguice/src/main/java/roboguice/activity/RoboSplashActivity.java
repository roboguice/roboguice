package roboguice.activity;

import roboguice.RoboGuice;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * An activity that can be used to display a splash page while initializing the
 * guice injector in the background.
 * 
 * Use of this class is definitely not required in order to use RoboGuice, but
 * it can be useful if your app startup times are longer than desired.
 * 
 * To use, simply override onCreate to call setContentView. Then override
 * startNextActivity to specify where to go next.
 * 
 * @author Mike Burton
 * 
 */
public abstract class RoboSplashActivity extends Activity {
    protected int minDisplayMs = (int) (2.5 * 1000);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final long start = System.currentTimeMillis();

        new Thread(new Runnable() {
            public void run() {
                // Set up a new thread since app.getBaseApplicationInjector() takes so long
                // Set the execution context for this thread in case the user
                // want to use the injector
                final Application app = getApplication();
                RoboGuice.getBaseApplicationInjector(getApplication());


                doStuffInBackground(app);

                // Make sure we display splash for MIN_DISPLAY_MS
                final long duration = System.currentTimeMillis() - start;
                if (duration < minDisplayMs) {
                    try {
                        Thread.sleep(minDisplayMs - duration);
                    } catch (InterruptedException e) {
                        Thread.interrupted();
                    }
                }

                startNextActivity();
                andFinishThisOne();

            }

        }).start();
    }

    /**
     * Is there anything you want to do in the background? Add it here.
     * 
     * @param app
     */
    @SuppressWarnings({"UnusedParameters"})
    protected void doStuffInBackground(Application app) {
    }

    /**
     * It's expected that most splash pages will want to finish after they start
     * the next activity, but in case this isn't true you can override this
     * method to change the behavior.
     */
    protected void andFinishThisOne() {
        finish();
    }

    /**
     * This method should call startActivity to launch a new activity.
     */
    protected abstract void startNextActivity();

}
