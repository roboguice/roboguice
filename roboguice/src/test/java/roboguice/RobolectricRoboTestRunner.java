package roboguice;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricConfig;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.bytecode.ClassHandler;
import com.xtremelabs.robolectric.bytecode.RobolectricClassLoader;
import com.xtremelabs.robolectric.bytecode.ShadowWrangler;
import org.junit.runners.model.InitializationError;
import roboguice.shadow.ShadowFragmentActivity;
import roboguice.shadow.ShadowFragmentManagerImpl;

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
        try {
            this.bindShadowClass(Class.forName("android.support.v4.app.FragmentManagerImpl"),ShadowFragmentManagerImpl.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    public void bindShadowClass(Class<?> realClass, Class<?> shadowClass) {
        try {
            ShadowWrangler.getInstance().bindShadowClass(realClass, shadowClass);
        } catch (TypeNotPresentException typeLoadingException) {
            throw new RuntimeException(typeLoadingException);
        }
    }

}
