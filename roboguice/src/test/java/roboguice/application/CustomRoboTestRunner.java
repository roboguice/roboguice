package roboguice.application;

import java.io.File;

import org.junit.runners.model.InitializationError;

import roboguice.test.RobolectricRoboTestRunner;

import com.xtremelabs.robolectric.RobolectricConfig;
import com.xtremelabs.robolectric.bytecode.ClassHandler;
import com.xtremelabs.robolectric.bytecode.RobolectricClassLoader;

public class CustomRoboTestRunner extends RobolectricRoboTestRunner {

    public CustomRoboTestRunner(Class<?> testClass, ClassHandler classHandler, RobolectricClassLoader classLoader, RobolectricConfig robolectricConfig)
            throws InitializationError {
        super(testClass, classHandler, classLoader, robolectricConfig);
    }

    public CustomRoboTestRunner(Class<?> testClass, ClassHandler classHandler, RobolectricConfig robolectricConfig) throws InitializationError {
        super(testClass, classHandler, robolectricConfig);
    }

    public CustomRoboTestRunner(Class<?> testClass, File androidManifestPath, File resourceDirectory) throws InitializationError {
        super(testClass, androidManifestPath, resourceDirectory);
    }

    public CustomRoboTestRunner(Class<?> testClass, File androidProjectRoot) throws InitializationError {
        super(testClass, androidProjectRoot);
    }

    public CustomRoboTestRunner(Class<?> testClass, RobolectricConfig robolectricConfig) throws InitializationError {
        super(testClass, robolectricConfig);
    }

    public CustomRoboTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

}
