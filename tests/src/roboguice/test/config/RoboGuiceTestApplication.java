package roboguice.test.config;

import android.app.Application;
import android.app.Instrumentation;

import com.google.inject.Module;

import java.util.List;

public class RoboGuiceTestApplication extends Application {
    protected Instrumentation instrumentation;

    public RoboGuiceTestApplication( Instrumentation instrumentation ) {
        super();
        attachBaseContext(instrumentation.getTargetContext());
        this.instrumentation = instrumentation;

        throw new UnsupportedOperationException("addApplicationModules no longer gets called.  Need to think of another way to do this");
    }



    protected void addApplicationModules(List<Module> modules) {
        modules.add(new RoboGuiceTestModule());
    }

}
