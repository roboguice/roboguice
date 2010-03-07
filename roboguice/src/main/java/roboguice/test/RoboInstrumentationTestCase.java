package roboguice.test;

import roboguice.application.GuiceApplication;
import roboguice.inject.ContextScope;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.google.inject.Injector;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;

public class RoboInstrumentationTestCase<T extends GuiceApplication> extends InstrumentationTestCase {
    protected Injector injector;

    @Override
    protected void runTest() throws Throwable {
        final Context context = getInstrumentation().getTargetContext();
        final Constructor constructor = applicationType().getConstructor(Context.class);
        final GuiceApplication app = (GuiceApplication)constructor.newInstance(context);
        injector = app.getInjector();
        final ContextScope scope = injector.getInstance(ContextScope.class);

        try {
            scope.enter(context);
            super.runTest();
        } finally {
            scope.exit(context);
        }
    }

    protected Injector getInjector() {
        return injector;
    }

    protected Class<? extends GuiceApplication> applicationType() {
        final ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<? extends GuiceApplication>) parameterizedType.getActualTypeArguments()[0];
    }

}
