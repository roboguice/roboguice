package roboguice.test.config;

import roboguice.application.RoboApplication;

import android.content.Context;

import com.google.inject.Module;

import java.util.List;

public class RoboGuiceTestApplication extends RoboApplication {

    public RoboGuiceTestApplication() {
        super();
    }

    public RoboGuiceTestApplication( Context context ) {
        super();
        attachBaseContext(context);
    }

    @Override
    protected void addApplicationModules(List<Module> modules) {
        modules.add(new RoboGuiceTestModule());
    }

}
