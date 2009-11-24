package roboguice.astroboy.activity;

import java.io.Serializable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * This activity was created to execute {@link DoctorTenma} activity with extra
 * values.
 * 
 */
public class Tobio extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, DoctorTenma.class);
        intent.putExtra("nullExtra", (Serializable) null);
        intent.putExtra("nameExtra", "Atom");

        startActivity(intent);
    }
}
