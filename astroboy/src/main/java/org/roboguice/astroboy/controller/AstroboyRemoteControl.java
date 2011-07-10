package org.roboguice.astroboy.controller;

import roboguice.inject.ContextScoped;
import roboguice.util.Ln;

import android.app.Activity;
import android.widget.Toast;

import com.google.inject.Inject;

/**
 * A class to control Astroboy remotely.
 *
 * This class uses the current context, so we must make it @ContextScoped.
 * This means that there will be one AstroboyRemoteControl for every activity or
 * service that requires one.
 * Note that we actually ask for the Activity, rather than the Context (which is
 * the same thing), because we need access to some activity-related methods and this
 * saves us from having to downcast to an Activity manually.
 *
 * It also asks RoboGuice to inject the Astroboy instance so we can control him.
 */
@ContextScoped
public class AstroboyRemoteControl {
    @Inject Astroboy astroboy;
    @Inject Activity activity; // equivalent to @Inject Context context, also requires @ContextScope

    public void brushTeeth() {
        // More info about logging available here: http://code.google.com/p/roboguice/wiki/Logging
        Ln.d("Sent brushTeeth command to Astroboy");
        astroboy.brushTeeth();
    }

    public void say( String something ) {
        Ln.d("Sent say(%d) command to Astroboy",something);
        astroboy.say(something);
    }

    public void selfDestruct() {
        Toast.makeText(activity, "Your evil remote control has exploded! Now Astroboy is FREEEEEEEEEE!", Toast.LENGTH_LONG).show();
        activity.finish();
    }


    // Note: this method may take a long time to execute, so you
    // probably want to run it in a background thread
    public void fightForcesOfEvil() {

    }



}
