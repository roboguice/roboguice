package org.roboguice.astroboy.activity;

import org.roboguice.astroboy.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContextScoped;
import roboguice.inject.InjectView;
import roboguice.util.Ln;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;


/**
 * This activity uses an AstroboyRemoteControl to control Astroboy remotely!
 *
 */
public class AstroboyMasterConsole extends RoboActivity {

    // Various views that we inject into the activity.
    // Equivalent to calling findViewById() in your onCreate(), except more succinct
    @InjectView(R.id.self_destruct) Button selfDestructButton;
    @InjectView(R.id.say_text)      EditText sayText;
    @InjectView(R.id.brush_teeth)   Button brushTeethButton;
    @InjectView(R.id.fight_evil)      Button fightEvilButton;


    // Standard Guice injection of Plain Old Java Objects (POJOs)
    // Guice will find or create the appropriate instance of AstroboyRemoteControl for us
    // Since we haven't specified a special binding for AstroboyRemoteControl, Guice
    // will create a new instance for us using AstroboyRemoteControl's default constructor.
    // Contrast this with Vibrator, which is an Android service that is pre-bound by RoboGuice.
    // Injecting a Vibrator will return a new instance of a Vibrator obtained by calling
    // context.getSystemService(VIBRATOR_SERVICE).  This is configured in RoboModule, which is
    // used by default to configure every RoboGuice injector.
    @Inject AstroboyRemoteControl remoteControl;
    @Inject Vibrator vibrator;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // @Inject, @InjectResource, and @InjectExtra injection happens during super.onCreate()
        setContentView(R.layout.main);      // @InjectView injection happens when you call setContentView()

        sayText.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                // Have the remoteControl tell Astroboy to say something
                remoteControl.say(textView.getText().toString());
                textView.setText(null);
                return true;
            }
        });

        brushTeethButton.setOnClickListener( new OnClickListener() {
            public void onClick(View view) {
                remoteControl.brushTeeth();
            }
        });

        selfDestructButton.setOnClickListener( new OnClickListener() {
            public void onClick(View view) {

                // Self destruct the remoteControl
                vibrator.vibrate(2000);
                remoteControl.selfDestruct();
            }
        });

        // Fighting the forces of evil deserves its own activity
        fightEvilButton.setOnClickListener( new OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(AstroboyMasterConsole.this, FightForcesOfEvilActivity.class));
            }
        });

    }

}


/**
 * A class to control Astroboy remotely.
 *
 * This class uses the current context, so we must make it @ContextScoped.
 * This means that there will be one AstroboyRemoteControl for every activity or
 * service that requires one.
 *
 * It also asks RoboGuice to inject the Astroboy instance so we can control him.
 */
@ContextScoped
class AstroboyRemoteControl {
    @Inject Astroboy astroboy;
    @Inject Context context;

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
        Toast.makeText(context,"Your evil remote control has exploded! Now Astroboy is FREEEEEEEEEE!",Toast.LENGTH_LONG).show();
    }


    // Note: this method may take a long time to execute, so you
    // probably want to run it in a background thread
    public void fightForcesOfEvil() {

    }



}




// There's only one Astroboy, so make it a @Singleton.
// This means that there will be only one instance of Astroboy in the entire app.
// Any class that requires an instance of Astroboy will get the same instance.
@Singleton
class Astroboy {

    // Because Astroboy is a Singleton, we can't directly inject the current Context
    // since the current context may change depending on what activity is using Astroboy
    // at the time.  Instead, inject a PROVIDER of the current context, then we can
    // ask the provider for the context when we need it.
    @Inject Provider<Context> contextProvider;
    @Inject Vibrator vibrator;

    public void say(String something) {
        // Make a Toast, using the current context as returned by the Context Provider
        Toast.makeText(contextProvider.get(),"Astroboy says, \"" + something + "\"",Toast.LENGTH_LONG).show();
    }

    public void brushTeeth() {
        vibrator.vibrate(new long[]{0, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50,  }, -1);
    }
}
