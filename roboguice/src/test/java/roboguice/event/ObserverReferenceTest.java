package roboguice.event;

import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;

/**
 * @author John Ericksen
 */
public class ObserverReferenceTest {

    @Test(groups = "roboguice")
    public void testEquality() throws NoSuchMethodException {

        EqualityTestClass test = new EqualityTestClass();
        Method methodOne = EqualityTestClass.class.getDeclaredMethod("one", null);
        Method methodTwo = EqualityTestOverrideClass.class.getDeclaredMethod("one", null);

        EventManager.ObserverReference<EqualityTestClass> observerRefOne = new EventManager.ObserverReference<EqualityTestClass>(test, methodOne);
        EventManager.ObserverReference<EqualityTestClass> observerRefTwo = new EventManager.ObserverReference<EqualityTestClass>(test, methodTwo);

        assertEquals(observerRefOne, observerRefTwo);
        assertEquals(observerRefOne.hashCode(), observerRefTwo.hashCode());
    }

     @Test(groups = "roboguice")
    public void testEqualityOfSameGuts() throws NoSuchMethodException {

        EqualityTestClass test = new EqualityTestClass();
        Method methodOne = EqualityTestClass.class.getDeclaredMethod("one", null);

        EventManager.ObserverReference<EqualityTestClass> observerRefOne = new EventManager.ObserverReference<EqualityTestClass>(test, methodOne);
        EventManager.ObserverReference<EqualityTestClass> observerRefTwo = new EventManager.ObserverReference<EqualityTestClass>(test, methodOne);

        assertEquals(observerRefOne, observerRefTwo);
        assertEquals(observerRefOne.hashCode(), observerRefTwo.hashCode());
    }

    @Test(groups = "roboguice")
    public void testInequalityBetweenSameClass() throws NoSuchMethodException {

        EqualityTestClass test = new EqualityTestClass();
        Method methodOne = EqualityTestClass.class.getDeclaredMethod("one", null);
        Method methodTwo = EqualityTestClass.class.getDeclaredMethod("two", null);

        EventManager.ObserverReference<EqualityTestClass> observerRefOne = new EventManager.ObserverReference<EqualityTestClass>(test, methodOne);
        EventManager.ObserverReference<EqualityTestClass> observerRefTwo = new EventManager.ObserverReference<EqualityTestClass>(test, methodTwo);

        assert !observerRefOne.equals(observerRefTwo) ;
        assert !Integer.valueOf(observerRefOne.hashCode()).equals(observerRefTwo.hashCode());
    }

    @Test(groups = "roboguice")
    public void testInequalityBetweenDifferentClass() throws NoSuchMethodException {

        EqualityTestClass test = new EqualityTestClass();
        Method methodOne = EqualityTestClass.class.getDeclaredMethod("one", null);
        Method methodTwo = EqualityTestOverrideClass.class.getDeclaredMethod("two", null);

        EventManager.ObserverReference<EqualityTestClass> observerRefOne = new EventManager.ObserverReference<EqualityTestClass>(test, methodOne);
        EventManager.ObserverReference<EqualityTestClass> observerRefTwo = new EventManager.ObserverReference<EqualityTestClass>(test, methodTwo);

        assert !observerRefOne.equals(observerRefTwo) ;
        assert !Integer.valueOf(observerRefOne.hashCode()).equals(observerRefTwo.hashCode());
    }

    @Test(groups = "roboguice")
    public void testInequalityBetweenDifferentInstances() throws NoSuchMethodException {

        EqualityTestClass test = new EqualityTestClass();
        EqualityTestClass test2 = new EqualityTestClass();
        Method methodOne = EqualityTestClass.class.getDeclaredMethod("one", null);

        EventManager.ObserverReference<EqualityTestClass> observerRefOne = new EventManager.ObserverReference<EqualityTestClass>(test, methodOne);
        EventManager.ObserverReference<EqualityTestClass> observerRefTwo = new EventManager.ObserverReference<EqualityTestClass>(test2, methodOne);

        assert !observerRefOne.equals(observerRefTwo) ;
        assert !Integer.valueOf(observerRefOne.hashCode()).equals(observerRefTwo.hashCode());
    }

    @Test(groups = "roboguice")
    public void testInequalityBetweenDifferentInstancesAndDifferentMethods() throws NoSuchMethodException {

        EqualityTestClass test = new EqualityTestClass();
        EqualityTestClass test2 = new EqualityTestClass();
        Method methodOne = EqualityTestClass.class.getDeclaredMethod("one", null);
        Method methodTwo = EqualityTestOverrideClass.class.getDeclaredMethod("one", null);

        EventManager.ObserverReference<EqualityTestClass> observerRefOne = new EventManager.ObserverReference<EqualityTestClass>(test, methodOne);
        EventManager.ObserverReference<EqualityTestClass> observerRefTwo = new EventManager.ObserverReference<EqualityTestClass>(test2, methodTwo);

        assert !observerRefOne.equals(observerRefTwo) ;
         assert !Integer.valueOf(observerRefOne.hashCode()).equals(observerRefTwo.hashCode());
    }

    public class EqualityTestClass{

        public void one(){}

        public void two(){}
    }

    public class EqualityTestOverrideClass extends EqualityTestClass{
        public void one(){}

        public void two(){}
    }
}
