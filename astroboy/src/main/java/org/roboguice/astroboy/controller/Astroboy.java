package org.roboguice.astroboy.controller;

import android.content.Context;
import android.os.Vibrator;
import android.widget.Toast;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

// There's only one Astroboy, so make it a @Singleton.
// This means that there will be only one instance of Astroboy in the entire app.
// Any class that requires an instance of Astroboy will get the same instance.
@Singleton
public class Astroboy {

    // Because Astroboy is a Singleton, we can't directly inject the current Context
    // since the current context may change depending on what activity is using Astroboy
    // at the time.  Instead, inject a PROVIDER of the current context, then we can
    // ask the provider for the context when we need it.
    @Inject Provider<Context> contextProvider;
    @Inject Vibrator vibrator;

    public void say(String something) {
        // Make a Toast, using the current context as returned by the Context Provider
        Toast.makeText(contextProvider.get(), "Astroboy says, \"" + something + "\"", Toast.LENGTH_LONG).show();
    }

    public void brushTeeth() {
        vibrator.vibrate(new long[]{0, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50,  }, -1);
    }
}
