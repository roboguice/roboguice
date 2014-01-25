package roboguice.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import roboguice.RoboGuice;
import roboguice.config.RoboGuiceHierarchyTraversalFilter;

import android.app.Activity;

public class GuiceInjectionMonitorTest {

    @Test
    public void testRejectAndroidClass() {
        //given
        RoboGuiceHierarchyTraversalFilter filter = new RoboGuiceHierarchyTraversalFilter();
        Class<?> c = Activity.class;
        
        //when
        boolean worthInjecting = filter.isWorthScanning(c);
        
        //then
        assertFalse( worthInjecting );
    }

    @Test
    public void testRejectNull() {
        //given
        RoboGuiceHierarchyTraversalFilter filter = new RoboGuiceHierarchyTraversalFilter();
        Class<?> c = null;
        
        //when
        boolean worthInjecting = filter.isWorthScanning(c);
        
        //then
        assertFalse( worthInjecting );
    }

    @Test
    public void testRejectObject() {
        //given
        RoboGuiceHierarchyTraversalFilter filter = new RoboGuiceHierarchyTraversalFilter();
        Class<?> c = Object.class;
        
        //when
        boolean worthInjecting = filter.isWorthScanning(c);
        
        //then
        assertFalse( worthInjecting );
    }

    @Test
    public void testAcceptInRoboGuiceClass() {
        //given
        RoboGuiceHierarchyTraversalFilter filter = new RoboGuiceHierarchyTraversalFilter();
        Class<?> c = RoboGuice.class;
        
        //when
        boolean worthInjecting = filter.isWorthScanning(c);
        
        //then
        assertTrue( worthInjecting );
    }

    @Test
    public void testRejectClassHigherThanRoboGuice() {
        //given
        RoboGuiceHierarchyTraversalFilter filter = new RoboGuiceHierarchyTraversalFilter();
        Class<?> c1 = String.class;
        Class<?> c2 = RoboGuice.class;
        
        //when
        boolean worthInjecting = filter.isWorthScanning(c1);
        boolean worthInjecting2 = filter.isWorthScanning(c2);
        boolean worthInjecting3 = filter.isWorthScanning(c1);
        
        //then
        assertTrue( worthInjecting );
        assertTrue( worthInjecting2 );
        assertFalse( worthInjecting3 );
    }

}
