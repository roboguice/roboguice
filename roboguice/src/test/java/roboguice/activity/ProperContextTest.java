package roboguice.activity;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import roboguice.CustomRobolectricTestRunner;
import roboguice.inject.ContextScope;

import android.content.Context;

import java.lang.reflect.Field;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class ProperContextTest {

    @Test
    public void testOnStop() throws Exception {
        final RoboActivity a1 = new RoboActivity();
        final RoboActivity a2 = new RoboActivity();
        final ContextScope scope = a2.getInjector().getInstance(ContextScope.class);

        a1.onCreate(null);
        assertEquals( a1, getContext(scope) );
        a1.onStart();
        assertEquals( a1, getContext(scope) );
        a1.onResume();
        assertEquals( a1, getContext(scope) );

        a1.onPause();
        assertEquals( a1, getContext(scope) );
        a2.onCreate(null);
        assertEquals( a2, getContext(scope) );
        a2.onStart();
        assertEquals( a2, getContext(scope) );
        a2.onResume();
        assertEquals( a2, getContext(scope) );
        a1.onStop();
        assertEquals( a1, getContext(scope) );

    }

    @SuppressWarnings({"unchecked"})
    protected Context getContext( ContextScope scope ) throws Exception {
        final Field f = ContextScope.class.getDeclaredField("currentContext");
        f.setAccessible(true);
        return ((ThreadLocal<Context>)f.get(scope)).get();
    }

}
