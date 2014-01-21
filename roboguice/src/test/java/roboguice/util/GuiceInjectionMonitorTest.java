package roboguice.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import roboguice.RoboGuice;

import android.app.Activity;

public class GuiceInjectionMonitorTest {

    @Test
    public void testRejectAndroidClass() {
        //given
        GuiceInjectionMonitor gim = new GuiceInjectionMonitor();
        Class<?> c = Activity.class;
        
        //when
        boolean worthInjecting = gim.isWorthInjecting(c);
        
        //then
        assertFalse( worthInjecting );
    }

    @Test
    public void testRejectNull() {
        //given
        GuiceInjectionMonitor gim = new GuiceInjectionMonitor();
        Class<?> c = null;
        
        //when
        boolean worthInjecting = gim.isWorthInjecting(c);
        
        //then
        assertFalse( worthInjecting );
    }

    @Test
    public void testRejectObject() {
        //given
        GuiceInjectionMonitor gim = new GuiceInjectionMonitor();
        Class<?> c = Object.class;
        
        //when
        boolean worthInjecting = gim.isWorthInjecting(c);
        
        //then
        assertFalse( worthInjecting );
    }

    @Test
    public void testAcceptInRoboGuiceClass() {
        //given
        GuiceInjectionMonitor gim = new GuiceInjectionMonitor();
        Class<?> c = RoboGuice.class;
        
        //when
        boolean worthInjecting = gim.isWorthInjecting(c);
        
        //then
        assertTrue( worthInjecting );
    }

    @Test
    public void testRejectClassHigherThanRoboGuice() {
        //given
        GuiceInjectionMonitor gim = new GuiceInjectionMonitor();
        Class<?> c1 = String.class;
        Class<?> c2 = RoboGuice.class;
        
        //when
        boolean worthInjecting = gim.isWorthInjecting(c1);
        boolean worthInjecting2 = gim.isWorthInjecting(c2);
        boolean worthInjecting3 = gim.isWorthInjecting(c1);
        
        //then
        assertTrue( worthInjecting );
        assertTrue( worthInjecting2 );
        assertFalse( worthInjecting3 );
    }

}
