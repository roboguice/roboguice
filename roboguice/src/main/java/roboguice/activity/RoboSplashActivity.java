package roboguice.activity;

import roboguice.RoboGuice;
import roboguice.inject.ContextScope;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

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

        final Handler handler = new Handler();

        // Give the UI thread a few ms to display the splash, then initialize roboguice
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RoboGuice.getInjector(RoboSplashActivity.this).getInstance(ContextScope.class);
            }
        }, 50);

        // Start the next activity and finish this one after minDisplayMs
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startNextActivity();
                andFinishThisOne();
            }
        },minDisplayMs);

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
