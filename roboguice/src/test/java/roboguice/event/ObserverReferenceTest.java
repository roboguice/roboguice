package roboguice.event;

import org.junit.Before;
import org.junit.Test;
import roboguice.event.eventListener.ObserverMethodListener;

import java.lang.reflect.Method;

import static junit.framework.Assert.assertEquals;

/**
 * Test class exercising the ObserverReferences
 *
 * @author John Ericksen
 */
public class ObserverReferenceTest {

    protected EqualityTestClass test;
    protected EqualityTestClass test2;
    protected Method methodOneBase;
    protected Method methodOne;
    protected Method methodTwoBase;
    protected Method methodTwo;


    @Before
    public void setup() throws NoSuchMethodException {
        methodOne = EqualityTestClass.class.getDeclaredMethod("one", EventOne.class);
        methodOneBase = EqualityTestOverrideClass.class.getDeclaredMethod("one", EventOne.class);
        methodTwo = EqualityTestClass.class.getDeclaredMethod("two", EventTwo.class);
        methodTwoBase = EqualityTestOverrideClass.class.getDeclaredMethod("two", EventTwo.class);

        test =  new EqualityTestClass();
        test2 = new EqualityTestClass();
    }

    @Test
    public void testEquality() {

        ObserverMethodListener<EqualityTestClass> observerRefOne = new ObserverMethodListener<EqualityTestClass>(test, methodOne);
        ObserverMethodListener<EqualityTestClass> observerRefTwo = new ObserverMethodListener<EqualityTestClass>(test, methodOneBase);

        assertEquals(observerRefOne, observerRefTwo);
        assertEquals(observerRefOne.hashCode(), observerRefTwo.hashCode());
    }

    @Test
    public void testEqualityOfSameGuts() {

        ObserverMethodListener<EqualityTestClass> observerRefOne = new ObserverMethodListener<EqualityTestClass>(test, methodOne);
        ObserverMethodListener<EqualityTestClass> observerRefTwo = new ObserverMethodListener<EqualityTestClass>(test, methodOne);

        assertEquals(observerRefOne, observerRefTwo);
        assertEquals(observerRefOne.hashCode(), observerRefTwo.hashCode());
    }

    @Test
    public void testInequalityBetweenSameClass() {

        ObserverMethodListener<EqualityTestClass> observerRefOne = new ObserverMethodListener<EqualityTestClass>(test, methodOne);
        ObserverMethodListener<EqualityTestClass> observerRefTwo = new ObserverMethodListener<EqualityTestClass>(test, methodTwo);

        assert !observerRefOne.equals(observerRefTwo) ;
        assert !Integer.valueOf(observerRefOne.hashCode()).equals(observerRefTwo.hashCode());
    }

    @Test
    public void testInequalityBetweenDifferentClass() {

        ObserverMethodListener<EqualityTestClass> observerRefOne = new ObserverMethodListener<EqualityTestClass>(test, methodOne);
        ObserverMethodListener<EqualityTestClass> observerRefTwo = new ObserverMethodListener<EqualityTestClass>(test, methodTwoBase);

        assert !observerRefOne.equals(observerRefTwo) ;
        assert !Integer.valueOf(observerRefOne.hashCode()).equals(observerRefTwo.hashCode());
    }

    @Test
    public void testInequalityBetweenDifferentInstances() {

        ObserverMethodListener<EqualityTestClass> observerRefOne = new ObserverMethodListener<EqualityTestClass>(test, methodOne);
        ObserverMethodListener<EqualityTestClass> observerRefTwo = new ObserverMethodListener<EqualityTestClass>(test2, methodOne);

        assert !observerRefOne.equals(observerRefTwo) ;
        assert !Integer.valueOf(observerRefOne.hashCode()).equals(observerRefTwo.hashCode());
    }

    @Test
    public void testInequalityBetweenDifferentInstancesAndDifferentMethods() {

        ObserverMethodListener<EqualityTestClass> observerRefOne = new ObserverMethodListener<EqualityTestClass>(test, methodOne);
        ObserverMethodListener<EqualityTestClass> observerRefTwo = new ObserverMethodListener<EqualityTestClass>(test2, methodTwoBase);

        assert !observerRefOne.equals(observerRefTwo) ;
        assert !Integer.valueOf(observerRefOne.hashCode()).equals(observerRefTwo.hashCode());
    }

    /*@Test
    public void testEqualityBetweenDecoration() {

        EventListener<EqualityTestClass> observerRefOne = new ObserverMethodListener<EqualityTestClass>(test, methodOne);
        EventListener<EqualityTestClass> observerRefTwo = new AsynchronousEventListenerDecorator<EqualityTestClass>(
                new ObserverMethodListener<EqualityTestClass>(test, methodOne), new RunnableAsyncTaskAdaptorFactory());

        assert !observerRefOne.equals(observerRefTwo) ;
        assert !Integer.valueOf(observerRefOne.hashCode()).equals(observerRefTwo.hashCode());
    }*/

    public class EqualityTestClass{

        public void one(EventOne one){}

        public void two(EventTwo two){}
    }

    public class EqualityTestOverrideClass extends EqualityTestClass{
        public void one(EventOne one){}

        public void two(EventTwo two){}
    }
}
