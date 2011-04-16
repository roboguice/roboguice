package roboguice.inject;

import org.junit.Test;

import static junit.framework.Assert.*;

public class WeakActiveStackTest {
    @Test
    public void test_when_pushing_an_item_it_should_be_returned_from_peek() {
        ContextScope.WeakActiveStack<String> stack = new ContextScope.WeakActiveStack<String>();
        String value = "foo";
        stack.push(value);
        assertEquals(value, stack.peek());
    }

    @Test
    public void test_when_peeking_an_item_it_should_not_be_removed_from_the_stack() {
        ContextScope.WeakActiveStack<String> stack = new ContextScope.WeakActiveStack<String>();
        String value = "foo";
        stack.push(value);
        stack.peek();
        assertEquals(value, stack.peek());
    }

    @Test
    public void test_when_pushing_a_second_item_it_should_be_returned_from_peek() {
        ContextScope.WeakActiveStack<String> stack = new ContextScope.WeakActiveStack<String>();
        String value = "foo";
        String value2 = "bar";
        stack.push(value);
        stack.push(value2);
        assertEquals(value2, stack.peek());
        assertPops(stack, new String[]{value2, value, value2, value});
    }

    @Test
    public void test_when_pushing_an_existing_item_it_should_be_returned_from_peek() {
        ContextScope.WeakActiveStack<String> stack = new ContextScope.WeakActiveStack<String>();
        String value = "foo";
        String value2 = "bar";
        stack.push(value);
        stack.push(value2);
        stack.push(value);
        assertEquals(value, stack.peek());
        assertPops(stack, new String[]{value, value2, value, value2});
    }

    @Test
    public void test_when_pushing_a_second_item_the_stack_should_pop() {
        ContextScope.WeakActiveStack<String> stack = new ContextScope.WeakActiveStack<String>();
        String value = "foo";
        String value2 = "bar";
        stack.push(value);
        stack.push(value2);

        assertPops(stack, new String[]{value2, value});
    }

    @Test
    public void test_when_popping_items_they_should_be_moved_to_the_tail() {
        ContextScope.WeakActiveStack<String> stack = new ContextScope.WeakActiveStack<String>();
        String value = "foo";
        String value2 = "bar";
        String value3 = "fubar";
        stack.push(value);
        stack.push(value2);
        stack.push(value3);

        assertPops(stack, new String[]{value3, value2, value, value3});
    }

    @Test
    public void test_when_removing_an_item_from_the_head_it_should_gone_and_stack_order_should_be_preserved() {
        ContextScope.WeakActiveStack<String> stack = new ContextScope.WeakActiveStack<String>();
        String value = "foo";
        String value2 = "bar";
        String value3 = "fubar";
        stack.push(value);
        stack.push(value2);
        stack.push(value3);

        stack.remove(value3);
        assertPops(stack, new String[]{value2, value, value2, value});
    }

    @Test
    public void test_when_removing_an_item_from_the_tail_it_should_gone_and_stack_order_should_be_preserved() {
        ContextScope.WeakActiveStack<String> stack = new ContextScope.WeakActiveStack<String>();
        String value = "foo";
        String value2 = "bar";
        String value3 = "fubar";
        stack.push(value);
        stack.push(value2);
        stack.push(value3);

        stack.remove(value);

        assertPops(stack, new String[]{value3, value2, value3, value2});
    }

    @Test
    public void test_when_removing_an_item_from_the_middle_it_should_gone_and_stack_order_should_be_preserved() {
        ContextScope.WeakActiveStack<String> stack = new ContextScope.WeakActiveStack<String>();
        String value = "foo";
        String value2 = "bar";
        String value3 = "fubar";
        stack.push(value);
        stack.push(value2);
        stack.push(value3);

        stack.remove(value2);
        assertPops(stack, new String[]{value3, value, value3, value, value3, value, value3, value});
    }

    @Test
    public void test_should_be_able_to_remove_all_items_in_order() {
        ContextScope.WeakActiveStack<String> stack = new ContextScope.WeakActiveStack<String>();
        String value = "foo";
        String value2 = "bar";
        String value3 = "fubar";
        String value4 = "hello world";

        stack.push(value);
        stack.push(value2);
        stack.push(value3);
        stack.push(value4);

        stack.remove(value4);
        assertEquals(value3, stack.peek());
        assertPops(stack, new String[]{value3, value2, value, value3, value2, value});

        stack.remove(value3);
        assertEquals(value2, stack.peek());
        assertPops(stack, new String[]{value2, value, value2, value});

        stack.remove(value2);
        assertEquals(value, stack.peek());
        assertPops(stack, new String[]{value, value});

        stack.remove(value);
        assertEquals(null, stack.peek());
        assertPops(stack, new String[]{null, null});
    }

    @Test
    public void test_should_be_able_to_remove_all_items_in_reverse_order() {
        ContextScope.WeakActiveStack<String> stack = new ContextScope.WeakActiveStack<String>();
        String value = "foo";
        String value2 = "bar";
        String value3 = "fubar";
        String value4 = "hello world";

        stack.push(value);
        stack.push(value2);
        stack.push(value3);
        stack.push(value4);

        stack.remove(value);
        assertEquals(value4, stack.peek());
        assertPops(stack, new String[]{value4, value3, value2, value4, value3, value2});

        stack.remove(value2);
        assertEquals(value4, stack.peek());
        assertPops(stack, new String[]{value4, value3, value4, value3});

        stack.remove(value3);
        assertEquals(value4, stack.peek());
        assertPops(stack, new String[]{value4, value4});

        stack.remove(value4);
        assertEquals(null, stack.peek());
        assertPops(stack, new String[]{null, null});
    }

    @Test
    public void test_should_be_able_to_remove_all_items_in_any_order() {
        ContextScope.WeakActiveStack<String> stack = new ContextScope.WeakActiveStack<String>();
        String value = "foo";
        String value2 = "bar";
        String value3 = "fubar";
        String value4 = "hello world";

        stack.push(value);
        stack.push(value2);
        stack.push(value3);
        stack.push(value4);

        stack.remove(value2);
        assertEquals(value4, stack.peek());
        assertPops(stack, new String[]{value4, value3, value, value4, value3, value});

        stack.remove(value3);
        assertEquals(value4, stack.peek());
        assertPops(stack, new String[]{value4, value, value4, value});

        stack.remove(value4);
        assertEquals(value, stack.peek());
        assertPops(stack, new String[]{value, value});

        stack.remove(value);
        assertEquals(null, stack.peek());
        assertPops(stack, new String[]{null, null});
    }

    @Test
    public void test_when_references_are_cleared_they_should_be_removed_from_stack_in_order() {
        ContextScope.WeakActiveStack<Object> stack = new ContextScope.WeakActiveStack<Object>();
        Object obj1 = new Object();
        Object obj2 = new Object();
        Object obj3 = new Object();

        stack.push(obj1);
        stack.push(obj2);
        stack.push(obj3);

        System.gc();
        assertPops(stack, new Object[]{obj3, obj2, obj1});

        obj3 = null;
        System.gc();
        assertPops(stack, new Object[]{obj2, obj1});

        obj2 = null;
        System.gc();
        assertPops(stack, new Object[]{obj1});

        obj1 = null;
        System.gc();
        assertPops(stack, new Object[]{null});
    }

    @Test
    public void test_when_references_are_cleared_they_should_be_removed_from_stack_in_reverse_order() {
        ContextScope.WeakActiveStack<Object> stack = new ContextScope.WeakActiveStack<Object>();
        Object obj1 = new Object();
        Object obj2 = new Object();
        Object obj3 = new Object();

        stack.push(obj1);
        stack.push(obj2);
        stack.push(obj3);

        System.gc();
        assertPops(stack, new Object[]{obj3, obj2, obj1});

        obj1 = null;
        System.gc();
        assertPops(stack, new Object[]{obj3, obj2, obj3, obj2});

        obj2 = null;
        System.gc();
        assertPops(stack, new Object[]{obj3, obj3});

        obj3 = null;
        System.gc();
        assertPops(stack, new Object[]{null, null});
    }

    @Test
    public void test_when_references_are_cleared_they_should_be_removed_from_stack_in_any_order() {
        ContextScope.WeakActiveStack<Object> stack = new ContextScope.WeakActiveStack<Object>();
        Object obj1 = new Object();
        Object obj2 = new Object();
        Object obj3 = new Object();
        Object obj4 = new Object();

        stack.push(obj1);
        stack.push(obj2);
        stack.push(obj3);
        stack.push(obj4);

        System.gc();
        assertPops(stack, new Object[]{obj4, obj3, obj2, obj1});

        obj2 = null;
        System.gc();
        assertPops(stack, new Object[]{obj4, obj3, obj1, obj4, obj3, obj1});

        obj4 = null;
        System.gc();
        assertPops(stack, new Object[]{obj3, obj1, obj3, obj1});

        obj1 = null;
        System.gc();
        assertPops(stack, new Object[]{obj3, obj3});

        obj3 = null;
        System.gc();
        assertPops(stack, new Object[]{null, null});
    }

    @Test public void should_not_cause_an_infinite_loop() {
        ContextScope.WeakActiveStack<Object> stack = new ContextScope.WeakActiveStack<Object>();
        Object app = new Object();
        Object activity1 = new Object();
        Object service = new Object();
        Object activity2 = new Object();

        stack.push(app);
        stack.push(activity1);
        stack.push(activity1);
        stack.push(activity1);
        stack.push(service);
        stack.push(service);
        stack.push(service);
        stack.remove(service);
        stack.push(activity2);
        stack.push(activity1);
        stack.remove(activity1);

        stack.push(service);
        assertEquals(service, stack.peek());

        stack.remove(service);
        assertEquals(activity2, stack.peek());
    }

    protected <T> void assertPops(ContextScope.WeakActiveStack<T> stack, T[] assertionValues) {
        for (int i = 0; i < assertionValues.length; i++) {
            assertEquals("Pop " + i + 1 + " should be " + assertionValues[i], assertionValues[i], stack.pop());
        }
    }
}
