package org.roboguice.astroboy.activity;

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
import com.google.inject.Guice;
import com.google.inject.HierarchyTraversalFilter;
import com.google.inject.HierarchyTraversalFilterFactory;
import com.google.inject.Inject;
import org.roboguice.astroboy.R;
import org.roboguice.astroboy.controller.AstroboyRemoteControl;
import roboguice.AnnotationDatabase;
import roboguice.activity.*;
import roboguice.event.EventManager;
import roboguice.event.eventListener.factory.EventListenerThreadingDecorator;
import roboguice.fragment.provided.NativeFragmentUtil;
import roboguice.fragment.support.SupportFragmentUtil;
import roboguice.inject.*;
import roboguice.util.Ln;
import roboguice.util.LnImpl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This activity uses an AstroboyRemoteControl to control Astroboy remotely!
 *
 * What you'll learn in this class:
 *   - How to use @InjectView as a typesafe version of findViewById()
 *   - How to inject plain old java objects as well (POJOs)
 *   - When injection happens
 *   - Some basics about injection, including when injection results in a call to
 *     an object's default constructor, versus when it does something "special"
 *     like call getSystemService()
 */
public class AstroboyMasterConsole extends RoboActivity {
    static {
        final Set<Class<?>> injectionClasses = new HashSet<Class<?>>();
        injectionClasses.addAll(AnnotationDatabase.getClasses());

        // BUG hack
        injectionClasses.addAll(Arrays.<Class<?>>asList(EventManager.class, EventListenerThreadingDecorator.class, NativeFragmentUtil.class, SupportFragmentUtil.class,
                AccountManagerProvider.class, AssetManagerProvider.class, ContentResolverProvider.class, ContentViewListener.class, ContextScopedProvider.class,
                ResourcesProvider.class, RoboApplicationProvider.class, SharedPreferencesProvider.class, StringResourceFactory.class, Ln.class, LnImpl.class,
                RoboAccountAuthenticatorActivity.class, /*RoboActionBarActivity.class,*/ RoboActivityGroup.class, RoboExpandableListActivity.class,
                /*RoboFragmentActivity.class,*/ RoboLauncherActivity.class, RoboListActivity.class, /*RoboMapActivity.class,*/  RoboPreferenceActivity.class,
                /*RoboSherlockAccountAuthenticatorActivity.class, RoboSherlockActivity.class, RoboSherlockFragmentActivity.class,
                RoboSherlockListActivity.class, RoboSherlockPreferenceActivity.class,*/ RoboTabActivity.class)
        );
        if( injectionClasses.isEmpty() )
            throw new IllegalStateException("Was unable to find the output of annotation processor");

        Guice.setHierarchyTraversalFilterFactory(new HierarchyTraversalFilterFactory() {
            @Override
            public HierarchyTraversalFilter createHierarchyTraversalFilter() {
                return new HierarchyTraversalFilter() {
                    @Override
                    public boolean isWorthScanning(Class<?> c) {
                        return c != null && injectionClasses.contains(c);
                    }
                };
            }
        });
    }

    // Various views that we inject into the activity.
    // Equivalent to calling findViewById() in your onCreate(), except more succinct
    @InjectView(R.id.self_destruct) Button selfDestructButton;
    @InjectView(R.id.say_text)      EditText sayText;
    @InjectView(R.id.brush_teeth)   Button brushTeethButton;
    @InjectView(tag="fightevil")    Button fightEvilButton;     // we can also use tags if we want


    // Standard Guice injection of Plain Old Java Objects (POJOs)
    // Guice will find or create the appropriate instance of AstroboyRemoteControl for us
    // Since we haven't specified a special binding for AstroboyRemoteControl, Guice
    // will create a new instance for us using AstroboyRemoteControl's default constructor.
    // Contrast this with Vibrator, which is an Android service that is pre-bound by RoboGuice.
    // Injecting a Vibrator will return a new instance of a Vibrator obtained by calling
    // context.getSystemService(VIBRATOR_SERVICE).  This is configured in DefaultRoboModule, which is
    // used by default to configure every RoboGuice injector.
    @Inject AstroboyRemoteControl remoteControl;
    @Inject Vibrator vibrator;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // @Inject, @InjectResource, and @InjectExtra injection happens during super.onCreate()
        setContentView(R.layout.main);

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




