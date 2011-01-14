package roboguice.test.config;

import roboguice.application.RoboApplication;

import android.app.Instrumentation;

import com.google.inject.Module;

import java.util.List;

public class RoboGuiceTestApplication extends RoboApplication {
    protected Instrumentation instrumentation;

    public RoboGuiceTestApplication( Instrumentation instrumentation ) {
        super();
        attachBaseContext(instrumentation.getTargetContext());
        this.instrumentation = instrumentation;
    }


    @Override
    protected void addApplicationModules(List<Module> modules) {
        modules.add(new RoboGuiceTestModule());
    }

}
