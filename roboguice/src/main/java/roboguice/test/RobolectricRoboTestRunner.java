package roboguice.test;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricConfig;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.bytecode.ClassHandler;
import com.xtremelabs.robolectric.bytecode.RobolectricClassLoader;
import org.junit.runners.model.InitializationError;
import roboguice.application.RoboApplication;
import roboguice.test.shadow.ShadowFragment;
import roboguice.test.shadow.ShadowFragmentActivity;

import android.app.Application;

import java.io.File;

public class RobolectricRoboTestRunner extends RobolectricTestRunner {

    public RobolectricRoboTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    public RobolectricRoboTestRunner(Class<?> testClass, File androidManifestPath, File resourceDirectory) throws InitializationError {
        super(testClass, androidManifestPath, resourceDirectory);
    }

    public RobolectricRoboTestRunner(Class<?> testClass, File androidProjectRoot) throws InitializationError {
        super(testClass, androidProjectRoot);
    }

    public RobolectricRoboTestRunner(Class<?> testClass, ClassHandler classHandler, RobolectricClassLoader classLoader, RobolectricConfig robolectricConfig) throws InitializationError {
        super(testClass, classHandler, classLoader, robolectricConfig);
    }

    public RobolectricRoboTestRunner(Class<?> testClass, ClassHandler classHandler, RobolectricConfig robolectricConfig) throws InitializationError {
        super(testClass, classHandler, robolectricConfig);
    }

    public RobolectricRoboTestRunner(Class<?> testClass, RobolectricConfig robolectricConfig) throws InitializationError {
        super(testClass, robolectricConfig);
    }

    @Override
    protected void bindShadowClasses() {
        super.bindShadowClasses();
        Robolectric.bindShadowClass(ShadowFragmentActivity.class);
        Robolectric.bindShadowClass(ShadowFragment.class);
    }

    @Override
    protected Application createApplication() {
        return new RoboApplication();
    }
}
