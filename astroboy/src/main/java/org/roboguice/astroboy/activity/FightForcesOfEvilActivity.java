package org.roboguice.astroboy.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.animation.AnimationUtils;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.Random;

import javax.inject.Inject;

import org.roboguice.astroboy.R;
import org.roboguice.astroboy.controller.Astroboy;

import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.widget.TextView;
import roboguice.RoboGuice;

/**
 * Things you'll learn in this class:
 *     - How to inject Resources
 *     - How to use RoboAsyncTask to do background tasks with injection
 *     - What it means to be a @Singleton
 */
public class FightForcesOfEvilActivity extends Activity {

    @Bind(R.id.expletive) TextView expletiveText;

    // You can also inject resources such as Strings, Drawables, and Animations
     Animation expletiveAnimation;

    // AstroboyRemoteControl is annotated as @ContextSingleton, so the instance
    // we get in FightForcesOfEvilActivity will be a different instance than
    // the one we got in AstroboyMasterConsole
    //@Inject AstroboyRemoteControl remoteControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fight_evil);
        ButterKnife.bind(this);
        expletiveAnimation = AnimationUtils.loadAnimation(this, R.anim.expletive_animation);
        expletiveText.setAnimation(expletiveAnimation);
        expletiveAnimation.start();

        // Throw some punches
        for( int i=0; i<10; ++i )
            new AsyncPunch(this, expletiveText).execute();
    }



    // This class will call Astroboy.punch() in the background
    public static class AsyncPunch extends AsyncTask<Void, Void, String> {

        private final Context context;
        private final TextView expletiveText;
        // Because Astroboy is a @Singleton, this will be the same
        // instance that we inject elsewhere in our app.
        // Random of course will be a new instance of java.util.Random, since
        // we haven't specified any special binding instructions anywhere
        @Inject Astroboy astroboy;
        @Inject Random random;

        public AsyncPunch(Context context, TextView expletiveText) {
            this.context = context;
            this.expletiveText = expletiveText;
            RoboGuice.getInjector(context).injectMembers(this);
        }

        @Override
        protected String doInBackground(Void... objects) {
            try {
                Thread.sleep(random.nextInt(5*1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return astroboy.punch();
        }

        @Override
        protected void onPostExecute(String expletive) {
            expletiveText.setText(expletive);
        }
    }
}
