package roboguice.astroboy.activity;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import roboguice.activity.GuiceActivity;
import roboguice.astroboy.R;
import roboguice.astroboy.service.TalkingThing;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectResource;

import com.google.inject.Inject;
import com.google.inject.internal.Nullable;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class DoctorTenma extends GuiceActivity {
    // You can inject arbitrary View, String, and other types of resources.
    // See ResourceListener for details.
    @InjectResource(R.id.widget1) protected TextView helloView;
    @InjectResource(R.string.hello) protected String hello;

    // You can inject Extras from the intent that started this activity,
    // equivalent to getIntent().getExtras().getXXX()
    // See ExtrasListener for details.
    // They can be @Nullable, or have @DefaultBoolean, Integer, String values.
    @InjectExtra("someValue") @Nullable protected Integer someValue;

    // You can inject various useful android objects.
    // See GuiceApplication.configure to see what's available.
    @Inject protected SharedPreferences prefs;

    // Injecting a collaborator
    @Inject protected TalkingThing talker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); // Injection doesn't happen until you call setContentView()

        helloView.setText(hello + ", " + this.getClass().getSimpleName());

        assertEquals(prefs.getString("dummyPref", "la la la"), "la la la");
        assertNull(someValue);

        Log.d("DoctorTenma", talker.talk());

    }

}