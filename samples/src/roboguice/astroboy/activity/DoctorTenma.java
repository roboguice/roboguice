package roboguice.astroboy.activity;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import java.util.Date;

import roboguice.activity.GuiceActivity;
import roboguice.astroboy.AstroboyModule;
import roboguice.astroboy.R;
import roboguice.astroboy.bean.Person;
import roboguice.astroboy.bean.PersonFromNameExtraProvider;
import roboguice.astroboy.service.TalkingThing;
import roboguice.inject.ExtrasListener;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectResource;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.inject.Inject;
import com.google.inject.internal.Nullable;

public class DoctorTenma extends GuiceActivity {
    // You can inject arbitrary View, String, and other types of resources.
    // See ResourceListener for details.
    @InjectResource(R.id.widget1)
    protected TextView          helloView;
    @InjectResource(R.string.hello)
    protected String            hello;

    /**
     * You can inject Extras from the intent that started this activity with
     * {@link InjectExtra}, this annotation is basically equivalent to the
     * following code : {@code
     * nameExtra=getIntent().getStringExtra("nameExtra");}
     * 
     * @see ExtrasListener
     */
    @InjectExtra("nameExtra")
    protected String            nameExtra;

    /**
     * The extra must exists when the activity is injected, unless you specify
     * {@code optional=true} in the {@link InjectExtra} annotation. If optional
     * is set to true and no extra is found, no value will be injected in the
     * field.
     */
    @InjectExtra(value = "optionalExtra", optional = true)
    protected Date              myDateExtra        = new Date(0);

    /**
     * The default behavior of the {@link InjectExtra} annotation is to forbid
     * null values. However, if you wish to allow injection of null values, you
     * should use the {@link Nullable} annotation. In the following example, the
     * extra "nullExtra" MUST exist, but CAN be null.
     */
    @InjectExtra("nullExtra")
    @Nullable
    protected Object            nullInjectedMember = new Object();

    /**
     * This example shows how to inject a bean created from an extra value. See
     * {@link PersonFromNameExtraProvider} to see how a Person is created. A
     * binding is actually done in {@link AstroboyModule}.
     */
    @Inject
    protected Person            personFromExtra;

    // You can inject various useful android objects.
    // See GuiceApplication.configure to see what's available.
    @Inject
    protected SharedPreferences prefs;

    // Injecting a collaborator
    @Inject
    protected TalkingThing      talker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); // Injection doesn't happen until you
        // call setContentView()

        helloView.setText(hello + ", " + this.getClass().getSimpleName());

        assertEquals(prefs.getString("dummyPref", "la la la"), "la la la");
        assertNull(nullInjectedMember);
        assertEquals(myDateExtra, new Date(0));
        assertEquals(nameExtra, "Atom");
        assertEquals(personFromExtra.getName(), "Atom");

        Log.d("DoctorTenma", talker.talk());

    }
}