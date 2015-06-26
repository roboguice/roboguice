package roboguice.activity;

import android.app.Activity;
import android.os.Bundle;
import com.google.inject.Injector;
import roboguice.RoboGuice;

public class TestRoboActivity extends Activity {
    protected Injector injector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (injector == null) {
            injector = RoboGuice.getInjector(this);
        }
        injector.injectMembers(this);
    }

    @Override
    protected void onDestroy() {
        RoboGuice.destroyInjector(this);
        super.onDestroy();
    }

}
