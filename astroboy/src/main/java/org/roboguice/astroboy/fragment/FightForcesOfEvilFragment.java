package org.roboguice.astroboy.fragment;

import java.util.Random;

import javax.inject.Inject;

import org.roboguice.astroboy.R;
import org.roboguice.astroboy.activity.AstroboyMasterConsole.AstroSpeechEvent;
import org.roboguice.astroboy.controller.Astroboy;

import roboguice.event.EventManager;
import roboguice.event.Observes;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

/**
 * Things you'll learn in this class: - How to inject Resources - How to use RoboAsyncTask to do
 * background tasks with injection - What it means to be a @Singleton
 */
public class FightForcesOfEvilFragment extends RoboFragment {

    @Inject
    EventManager eventManager; // only necessary to send events, not to receive them.

    @InjectView(R.id.expletive)
    TextView expletiveText;

    // You can also inject resources such as Strings, Drawables, and Animations
    @InjectResource(R.anim.expletive_animation)
    Animation expletiveAnimation;

    // AstroboyRemoteControl is annotated as @ContextSingleton, so the instance
    // we get in FightForcesOfEvilFragment will be a different instance than
    // the one we got in AstroboyMasterConsole
    // @Inject AstroboyRemoteControl remoteControl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fight_evil, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        expletiveText.setAnimation(expletiveAnimation);
        expletiveAnimation.start();

        expletiveText.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                eventManager.fire(new ForceOfEvilFoughtEvent());
            }
        });

        // Throw some punches
        for (int i = 0; i < 10; ++i) {
            new AsyncPunch(getActivity()) {
                @Override
                protected void onSuccess(String expletive) throws Exception {
                    expletiveText.setText(expletive);
                }

                // We could also override onException() and onFinally() if we wanted

            }.execute();
        }

    }

    public void handleAstroSpeechEvent(@Observes AstroSpeechEvent event) {
        expletiveText.setText(event.getMessage());
    }

    // This class will call Astroboy.punch() in the background
    public static class AsyncPunch extends RoboAsyncTask<String> {

        // Because Astroboy is a @Singleton, this will be the same
        // instance that we inject elsewhere in our app.
        // Random of course will be a new instance of java.util.Random, since
        // we haven't specified any special binding instructions anywhere
        @Inject
        Astroboy astroboy;
        @Inject
        Random random;

        public AsyncPunch(Context context) {
            super(context);
        }

        public String call() throws Exception {
            // we are sure to see each explective at least one second
            Thread.sleep(1000 + random.nextInt(5 * 1000));
            return astroboy.punch();
        }
    }

    public class ForceOfEvilFoughtEvent {

    }

}
