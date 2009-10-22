package roboguice.astroboy.activity;

import roboguice.activity.GuiceActivity;
import roboguice.astroboy.R;
import roboguice.inject.InjectResource;

import android.os.Bundle;
import android.widget.TextView;

public class DoctorTenma extends GuiceActivity {
    @InjectResource(R.id.widget1) protected TextView helloView;
    @InjectResource(R.string.hello) protected String hello;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); // Injection doesn't happen until you call setContentView()

        helloView.setText( hello + ", " + this.getClass().getSimpleName() );
    }

}