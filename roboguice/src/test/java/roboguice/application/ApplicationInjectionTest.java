package roboguice.application;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.res.builder.RobolectricPackageManager;

import roboguice.RoboGuice;

import com.google.inject.Inject;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

@RunWith(RobolectricTestRunner.class)
public class ApplicationInjectionTest {

    private static final String TEST_PACKAGE_NAME = "org.robolectric.default";
    
    @Test
    public void shouldBeAbleToInjectIntoApplication() {
        Robolectric.application = new AppA();
        Robolectric.application.onCreate();

        final AppA a = (AppA)Robolectric.application;
        assertNotNull(a.random);
    }


    @Test
    public void shouldBeAbleToInjectContextScopedItemsIntoApplication() {
        Robolectric.application = new AppB();
        Robolectric.application.onCreate();

        final AppB a = (AppB)Robolectric.application;
        assertThat( a.context, equalTo((Context)a) );
    }


    public static class AppA extends Application {

        @Inject Random random;

        @Override
        public void onCreate() {
            super.onCreate();
            RoboGuice.getInjector(this).injectMembers(this);
        }

        @Override
        public String getPackageName() {
            return TEST_PACKAGE_NAME;
        }
        
        @Override
        public PackageManager getPackageManager() {
            return new TestRobolectricPackageManager();
        }
    }

    public static class AppB extends Application {
        
        @Inject Context context;

        @Override
        public void onCreate() {
            super.onCreate();
            RoboGuice.getInjector(this).injectMembers(this);
        }

        @Override
        public String getPackageName() {
            return TEST_PACKAGE_NAME;
        }
        
        @Override
        public PackageManager getPackageManager() {
            return new TestRobolectricPackageManager();
        }
    }
    
    public static class TestRobolectricPackageManager extends RobolectricPackageManager {
        @Override
        public ApplicationInfo getApplicationInfo(String packageName, int flags) throws NameNotFoundException {
            ApplicationInfo applicationInfo = new ApplicationInfo();
            Bundle bundle = new Bundle();
            bundle.putString("roboguice.annotations.packages", "roboguice,testroboguice");
            applicationInfo.metaData = bundle ;
            return applicationInfo;
        }
        
        @Override
        public PackageInfo getPackageInfo(String packageName, int flags) throws NameNotFoundException {
            PackageInfo packageInfo = new PackageInfo();
            packageInfo.packageName = TEST_PACKAGE_NAME;
            return packageInfo;
        }
    }


}
