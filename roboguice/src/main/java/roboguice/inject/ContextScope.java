/*
 * Copyright 2009 Michael Burton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package roboguice.inject;

/**
 * Scopes a single execution of a block of code. Apply this scope with a
 * try/finally block: <pre>   {@code
 *
 *   scope.enter();
 *   try {
 *     // explicitly seed some seed objects...
 *     scope.seed(Key.get(SomeObject.class), someObject);
 *     // create and access scoped objects
 *   } finally {
 *     scope.exit();
 *   }
 * }</pre>
 *
 * The scope can be initialized with one or more seed values by calling
 * <code>seed(key, value)</code> before the injector will be called upon to
 * provide for this key. A typical use is for a servlet filter to enter/exit the
 * scope, representing a Request Scope, and seed HttpServletRequest and
 * HttpServletResponse.  For each key inserted with seed(), it's good practice
 * (since you have to provide <i>some</i> binding anyhow) to include a
 * corresponding binding that will throw an exception if Guice is asked to
 * provide for that key if it was not yet seeded: <pre>   {@code
 *
 *   bind(key)
 *       .toProvider(ContextScope.<KeyClass>seededKeyProvider())
 *       .in(ScopeAnnotation.class);
 * }</pre>
 *
 * @author Jesse Wilson
 * @author Fedor Karpelevitch
 *
 *
 * From http://code.google.com/p/google-guice/wiki/CustomScopes
 */

import roboguice.application.RoboApplication;
import roboguice.util.Ln;
import roboguice.util.Strings;

import android.content.Context;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.internal.Maps;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Mike Burton
 */
public class ContextScope implements Scope {

    protected WeakHashMap<Context,Map<Key<?>, WeakReference<Object>>> values = new WeakHashMap<Context,Map<Key<?>, WeakReference<Object>>>();
    protected ThreadLocal<WeakActiveStack<Context>> contextStack = new ThreadLocal<WeakActiveStack<Context>>();
    protected ArrayList<ViewMembersInjector<?>> viewsForInjection = new ArrayList<ViewMembersInjector<?>>();
    protected ArrayList<PreferenceMembersInjector<?>> preferencesForInjection = new ArrayList<PreferenceMembersInjector<?>>();

    public ContextScope(RoboApplication app) {
        enter(app);
    }

    public void registerViewForInjection(ViewMembersInjector<?> injector) {
        viewsForInjection.add(injector);
    }

    public void registerPreferenceForInjection(PreferenceMembersInjector<?> injector) {
        preferencesForInjection.add(injector);
    }

    public void injectViews() {
        for (int i = viewsForInjection.size() - 1; i >= 0; --i)
            viewsForInjection.remove(i).reallyInjectMembers();
    }

    public void injectPreferenceViews() {
        for (int i = preferencesForInjection.size() - 1; i >= 0; --i)
            preferencesForInjection.remove(i).reallyInjectMembers();
    }


    public void enter(Context context) {
        ensureContextStack();
        contextStack.get().push(context);

        final Key<Context> key = Key.get(Context.class);
        getScopedObjectMap(key).put(key, new WeakReference<Object>(context));

        if( Ln.isVerboseEnabled() ) {
            final WeakHashMap<Context,Map<Key<?>,WeakReference<Object>>> map = values;
            if( map!=null )
                Ln.v("Contexts in the %s scope map after inserting %s: %s", Thread.currentThread().getName(), context, Strings.join( ", ", map.keySet()));
        }
    }

    public void exit(Context context) {
        ensureContextStack();
        contextStack.get().remove(context);
    }

    public void dispose(Context context) {
        final WeakHashMap<Context,Map<Key<?>,WeakReference<Object>>> map = values;
        if( map!=null ) {
            final Map<Key<?>,WeakReference<Object>> scopedObjects = map.remove(context);
            if( scopedObjects!=null )
                scopedObjects.clear();

            if( Ln.isVerboseEnabled() )
                Ln.v("Contexts in the %s scope map after removing %s: %s", Thread.currentThread().getName(), context, Strings.join( ", ", map.keySet()));
        }
    }

    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return new Provider<T>() {
            public T get() {
                Map<Key<?>, WeakReference<Object>> scopedObjects = getScopedObjectMap(key);

                @SuppressWarnings({"unchecked"}) T current = (T) scopedObjects.get(key).get();
                if (current == null && !scopedObjects.containsKey(key)) {
                    current = unscoped.get();
                    scopedObjects.put(key, new WeakReference<Object>(current));
                }
                return current;
            }
        };
    }

    protected void ensureContextStack() {
        if (contextStack.get() == null) {
            contextStack.set(new WeakActiveStack<Context>());
        }
    }

    protected <T> Map<Key<?>, WeakReference<Object>> getScopedObjectMap(Key<T> key) {
        final Context context = contextStack.get().peek();

        Map<Key<?>,WeakReference<Object>> scopedObjects = values.get(context);
        if (scopedObjects == null) {
            scopedObjects = Maps.newHashMap();
            values.put(context, scopedObjects);
        }

        return scopedObjects;
    }

    /**
     * A circular stack like structure of weak references.
     * Calls to push while not add any new items to stack if the item already exists,
     * it will simply bring the item to the top of the stack.
     *
     * Likewise, pop will not remove the item from the stack, it will simply make the next item
     * the top, move the current top to the bottom.  Thus creating a circular linked list type effect.
     *
     * To remove an item explicitly  call the remove method.
     *
     * The stack also holds WeakReferences of T, these references will automatically be removed
     * anytime the stack accessed.  For performance they are only removed as they are encountered.
     *
     * So it is possible to get a null value back, even though you thought the stack had items in it.
     * @param <T>
     */
    public static class WeakActiveStack<T> {
        static class Node<T> {
            Node<T> previous;
            Node<T> next;
            WeakReference<T> value;

            public Node(T value) {
                this.value = new WeakReference<T>(value);
            }
        }

        private Node<T> head;
        private Node<T> tail;

        /**
         * Pushes the value onto the top of the stack.
         * If the value exists in the stack it is simply brought to the top.
         * @param value
         */
        public void push(T value) {
            if (head == null) {
                head = new Node<T>(value);
                tail = head;
            } else {
                Node<T> existingNode = findNode(value);
                if (existingNode == null) {
                    Node<T> newNode = new Node<T>(value);
                    newNode.next = head;
                    head.previous = newNode;
                    head = newNode;
                } else {
                    if (existingNode == head) return;

                    if (existingNode == tail) {
                        tail = existingNode.previous;
                        tail.next= null;
                    }

                    if (existingNode.previous != null) {
                        existingNode.previous.next = existingNode.next;
                    }

                    if (existingNode.next != null) {
                        existingNode.next.previous = existingNode.previous;
                    }

                    existingNode.next = head;
                    head.previous = existingNode;
                    head = existingNode;
                    head.previous = null;
                }
            }
        }

        /**
         * Pops the first item off the stack, then moves it to the bottom.
         * Popping is an infinite operation that will never end, it just keeps moving the top item to the bottom.
         * Popping will also dispose of items whose weak references have been collected.
         * @return The value of the item at the top of the stack.
         */
        public T pop() {
            WeakActiveStack.Node<T> node = head;
            while (node != null) {
                final T value = node.value.get();
                if (value == null) {
                    node = disposeOfNode(node);
                 } else {
                    if (node.next != null) {
                        head = node.next;
                        node.previous = tail;
                        tail.next = node;
                        node.next = null;
                        head.previous = null;
                        tail = node;
                    }
                    return value;
                }
            }
            return null;
        }

        /**
         * Non destructive read of the item at the top of stack.
         * @return the first non collected referent held, or null if nothing is available.
         */
        public T peek() {
            Node<T> node = head;
            while (node != null) {
                final T value = node.value.get();
                if (value == null) {
                    node = disposeOfNode(node);
                 } else {
                    return value;
                }
            }
            return null;
        }

        /**
         * Removes the item from the stack.
         * @param value
         */
        public void remove(T value) {
            Node<T> node = findNode(value);
            disposeOfNode(node);
        }

        /**
         * Removes a node ensuring all links are properly updated.
         * @param node
         * @return The next node in the stack.
         */
        protected Node<T> disposeOfNode(Node<T> node) {
            if (node == head) {
                head = node.next;
                if (head == null) {
                    tail = null;
                } else {
                    head.previous = null;
                }
            }

            if (node.previous != null) {
                node.previous.next = node.next;
            }

            if (node.next != null) {
                node.next.previous = node.previous;
            }

            if (node == tail) {
                tail = node.previous;
                tail.next = null;
            }

            return node.next;
        }

        /**
         * Finds a node given a value
         * Will dispose of nodes if needed as it iterates the stack.
         * @param value
         * @return The node if found or null
         */
        protected Node<T> findNode(T value) {
            Node<T> node = head;
            while (node != null) {
                final T nodeValue = node.value.get();
                if (nodeValue == null) {
                    node = disposeOfNode(node);
                } else if (nodeValue.equals(value)) {
                    return node;
                } else {
                    node = node.next;
                }
            }
            return null;
        }
    }
}
