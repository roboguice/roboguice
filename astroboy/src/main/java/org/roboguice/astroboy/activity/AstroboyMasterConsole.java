package org.roboguice.astroboy.activity;

import javax.annotation.Nullable;

import org.roboguice.astroboy.R;
import org.roboguice.astroboy.controller.AstroboyRemoteControl;
import org.roboguice.astroboy.fragment.FightForcesOfEvilFragment;
import org.roboguice.astroboy.fragment.FightForcesOfEvilFragment.ForceOfEvilFoughtEvent;

import roboguice.activity.RoboFragmentActivity;
import roboguice.event.EventManager;
import roboguice.event.EventProducer;
import roboguice.event.Observes;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.inject.Inject;

/**
 * This activity uses an AstroboyRemoteControl to control Astroboy remotely! What you'll learn in
 * this class: - How to use @InjectView as a typesafe version of findViewById() - How to inject
 * plain old java objects as well (POJOs) - When injection happens - Some basics about injection,
 * including when injection results in a call to an object's default constructor, versus when it
 * does something "special" like call getSystemService()
 */
@ContentView(R.layout.main)
public class AstroboyMasterConsole extends RoboFragmentActivity {

    protected static final String FIGHT_FORCES_OF_EVIL_FRAGMENT_TAG = "FIGHT_FORCES_OF_EVIL_FRAGMENT_TAG";

    // Various views that we inject into the activity.
    // Equivalent to calling findViewById() in your onCreate(), except more succinct
    @InjectView(R.id.self_destruct)
    Button selfDestructButton;
    @InjectView(R.id.say_text)
    EditText sayText;
    @InjectView(R.id.brush_teeth)
    Button brushTeethButton;
    @InjectView(tag = "fightevil")
    Button fightEvilButton; // we can also use tags if we want

    @Nullable
    @InjectView(R.id.fragment_container)
    LinearLayout fragmentContainer;

    @Inject
    EventManager eventManager; // only necessary to send events, not to receive them.

    // Standard Guice injection of Plain Old Java Objects (POJOs)
    // Guice will find or create the appropriate instance of AstroboyRemoteControl for us
    // Since we haven't specified a special binding for AstroboyRemoteControl, Guice
    // will create a new instance for us using AstroboyRemoteControl's default constructor.
    // Contrast this with Vibrator, which is an Android service that is pre-bound by RoboGuice.
    // Injecting a Vibrator will return a new instance of a Vibrator obtained by calling
    // context.getSystemService(VIBRATOR_SERVICE). This is configured in DefaultRoboModule, which is
    // used by default to configure every RoboGuice injector.
    @Inject
    AstroboyRemoteControl remoteControl;
    @Inject
    Vibrator vibrator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // @Inject, @InjectResource, and @InjectExtra injection
        // happens during super.onCreate()

        sayText.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                // Have the remoteControl tell Astroboy to say something
                String message = textView.getText().toString();
                remoteControl.say(message);
                textView.setText(null);

                AstroSpeechEvent event = new AstroSpeechEvent(message);
                eventManager.fire(event);
                eventManager.registerProducer(AstroSpeechEvent.class, new AstroSpeechEventProducer(event));
                return true;
            }
        });

        brushTeethButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                detachFightForcesOfEvilFragment();

                remoteControl.brushTeeth();
            }
        });

        selfDestructButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                detachFightForcesOfEvilFragment();

                // Self destruct the remoteControl
                vibrator.vibrate(2000);
                remoteControl.selfDestruct();
            }
        });

        // Fighting the forces of evil deserves its own activity
        fightEvilButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                boolean hasFragmentContainer = fragmentContainer != null;
                if (hasFragmentContainer) {
                    Fragment fightForcesOfEvilFragment = getSupportFragmentManager().findFragmentByTag(FIGHT_FORCES_OF_EVIL_FRAGMENT_TAG);
                    if (fightForcesOfEvilFragment == null) {
                        Fragment fragment = new FightForcesOfEvilFragment();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.add(R.id.fragment_container, fragment, FIGHT_FORCES_OF_EVIL_FRAGMENT_TAG);
                        ft.commit();
                    }
                } else {
                    startActivity(new Intent(AstroboyMasterConsole.this, FightForcesOfEvilActivity.class));
                }
            }
        });

    }

    public void handleForceOfEvilFoughtEvent(@Observes ForceOfEvilFoughtEvent event) {
        detachFightForcesOfEvilFragment();
    }

    private void detachFightForcesOfEvilFragment() {
        Fragment fightForcesOfEvilFragment = getSupportFragmentManager().findFragmentByTag(FIGHT_FORCES_OF_EVIL_FRAGMENT_TAG);
        if (fightForcesOfEvilFragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(fightForcesOfEvilFragment);
            ft.commit();
        }
    }

    public class AstroSpeechEvent {
        private String message;

        private AstroSpeechEvent(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    private class AstroSpeechEventProducer implements EventProducer<AstroSpeechEvent> {
        private AstroSpeechEvent event;

        private AstroSpeechEventProducer(AstroSpeechEvent event) {
            this.event = event;
        }

        public AstroSpeechEvent onEventRequested() {
            return event;
        }
    }
}
