package roboguice.astroboy.activity;

import roboguice.activity.GuiceActivity;
import roboguice.astroboy.R;

import android.os.Bundle;

public class DoctorTenma extends GuiceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); // Injection doesn't happen until you call setContentView()
    }

}