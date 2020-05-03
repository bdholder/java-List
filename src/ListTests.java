package org.roundrockisd.stonypoint.test;

import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.roundrockisd.stonypoint.util.List;

/**
 * Provides a collection of black box tests for an implementation of a simplified {@List} interface. The behavior expected is identical to that of {@code java.util.List}, except that {@List} is not generic and iterators are not expected to throw a {@code ConcurrentModificationException}, nor are they expected to implement {@code forEachRemaining}. {@ListIterator}s are expected to implement {@remove}.
 */
@ExtendWith(EnvironmentClassUnderTestParameterResolver.class)
public class ListTests {
    private final static int N = 100; // default number of objects used for testing

    /**
     * This constructor is used to provide all instances of the class under test.
     */
    private static Constructor<? extends List> constructor = null;

    /**
     * Exists simply to provide a "nicer" reference value for {@code toString} purposes.
     */
    private static class TestObject {
        private static int pseudoreference = 0;

        private final int REFERENCE;
        private String string = null;

        public TestObject() {
            REFERENCE = pseudoreference++;
        }

        @Override
        public String toString() {
            return string == null ? (string = "@" + REFERENCE) : string;
        }
    }

    private static void add(ArrayList<TestObject> arrayList, List list, TestObject object) {
        arrayList.add(object);
        list.add(object);
    }

    private static void add(ArrayList<TestObject> arrayList, List list, int index, TestObject object) {
        arrayList.add(index, object);
        list.add(index, object);
    }

    private static void assertGet(Object expected, List list, int index) {
        assertSame(expected, list.get(index), String.format("get(%d)", index));
    }

    private static void assertSameContents(ArrayList<?> arrayList, List list) {
        int i = 0;
        for (var element : arrayList) {
            assertGet(element, list, i++);
        }
    }

    private static void assertSize(int expected, List list) {
        assertEquals(expected, list.size(), "size()");
    }

    /**
     * Moves the cursor of a {@code ListIterator} to the given index. This method assumes that the index is valid.
     */
    private static void moveToIndex(ListIterator<?> it, int index) {
        int diff = index - it.nextIndex();
        if (diff < 0) {
            while (diff++ < 0) {
                it.previous();
            }
        }
        else {
            while(diff-- > 0) {
                it.next();
            }
        }
    }

    /**
     * Acquires a no-argument {@code Constructor} for the class under test. The mechanism by which this happens depends on the parameter resolver chosen.
     */
    @BeforeAll
    private static void resolveConstructor(Constructor<? extends List> constructor) {
        ListTests.constructor = constructor;
    }

    /**
    * Returns a new instance of the class under test.
    */
    private List newList() {
        try {
            return constructor.newInstance();
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    // TESTS

    @DisplayName("when new")
    @Nested
    class WhenNew {

        /**
         * Contains the core tests. Because all tests are black box tests, the {@code List} implementer should have some confidence that the method {@code get} works correctly. The core tests only check the functionality of {@code add(Object)} and {@code get(int)}.
         */
        @DisplayName("core tests")
        @Nested
        @Tag("core")
        class CoreTests {

            @DisplayName("test add(Object), get(int) once")
            @Test
            void addGet1() {
                var object = new TestObject();
                list.add(object);
                assertGet(object, list, 0);
            }

            @DisplayName("test add(Object), get(int) multiple times")
            @Test
            void addGetMultiple() {
                var arrayList = new ArrayList<TestObject>(N);

                range(0, N).forEachOrdered(i -> {
                    add(arrayList, list, new TestObject());
                    assertSameContents(arrayList, list);
                });
            }
        }

        @DisplayName("Iterator tests")
        @Nested
        @Tag("Iterator")
        class IteratorTests {

            @DisplayName("test iterator() non-null, hasNext() false")
            @Test
            void testIteratorEmptyList() {
                var it = list.iterator();
                assertNotNull(it);
                assertFalse(it.hasNext());
            }
        }

        @DisplayName("ListIterator tests")
        @Nested
        @Tag("ListIterator")
        class ListIteratorTests {

            @DisplayName("test listIterator() for empty list")
            @Test
            void testListIteratorEmptyList() {
                var it = list.listIterator();
                assertNotNull(it);
                assertFalse(it.hasNext());
                assertFalse(it.hasPrevious());
                assertEquals(0, it.nextIndex());
                assertEquals(-1, it.previousIndex());
            }

            @DisplayName("test add")
            @Test
            void testListIteratorAdd() {
                Random rand = new Random(0x2f7a5d92b824db0aL);
                var arrayList = new ArrayList<TestObject>(N);
                var arrayIter = arrayList.listIterator();
                var iter = list.listIterator();
                assertNotNull(iter);

                for (int i = 0; i < N; ++i) {
                    int index = rand.nextInt(i + 1);

                    moveToIndex(arrayIter, index);
                    moveToIndex(iter, index);

                    // [it.nextIndex() == index]
                    assertEquals(index, iter.nextIndex());
                    var object = new TestObject();
                    arrayIter.add(object);
                    iter.add(object);
                    // [it.nextIndex() == index + 1]
                    assertEquals(index + 1, iter.nextIndex());
                    assertEquals(index, iter.previousIndex());
                    assertSameContents(arrayList, list);
                }
            }
        }

        private List list;

        @BeforeEach
        void createList() {
            list = newList();
        }

        @DisplayName("test add(int, Object)")
        @Tag("basic")
        @Test
        void testAddIntObject() {
            var arrayList = new ArrayList<TestObject>(N);
            var random = new Random(0x2e7884b5e1776931L);

            // i starts at 1 because nextInt(n) ==> [0, n)
            rangeClosed(1, N).forEachOrdered(i -> add(arrayList, list, random.nextInt(i), new TestObject()));
            assertSameContents(arrayList, list);
        }

        @DisplayName("test add(int, Object) at start and end of list")
        @Tag("basic")
        @Test
        void testAddIntObjectEdges() {
            var arrayList = new ArrayList<TestObject>(N);
            range(0, N / 2).forEachOrdered(i -> {
                add(arrayList, list, 2 * i, new TestObject());
                add(arrayList, list, 0, new TestObject());
            });

            assertSameContents(arrayList, list);
        }

        @DisplayName("test add(int, Object) throws")
        @Tag("exception")
        @Test
        void testAddIntObjectThrows() {
            var object = new TestObject();
            assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, object));
            assertThrows(IndexOutOfBoundsException.class, () -> list.add(1, object));
            list.add(0, object);
            assertThrows(IndexOutOfBoundsException.class, () -> list.add(2, object));
        }

        @DisplayName("test add(Object) returns true")
        @Tag("basic")
        @Test
        void testAddReturnsTrue() {
            assertTrue(list.add(new TestObject()), "add(Object) failed to return true");
        }

        @DisplayName("test get(int) throws")
        @Tag("exception")
        @Test
        void testGetIndexOutOfBounds() {
            assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
            list.add(new TestObject());
            list.get(0);
            assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        }

        @DisplayName("test remove(int) throws")
        @Tag("exception")
        @Test
        void testRemoveIndexOutOfBounds() {
            assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0)); 
        }

        @DisplayName("test set(int, Object) throws")
        @Tag("exception")
        @Test
        void testSetIndexOutOfBounds() {
            var object = new TestObject();
            assertThrows(IndexOutOfBoundsException.class, () -> list.set(-1, object));
            assertThrows(IndexOutOfBoundsException.class, () -> list.set(0, object));
            list.add(object);
            list.set(0, object);
            assertThrows(IndexOutOfBoundsException.class, () -> list.set(1, object));
        }

        @DisplayName("test size() correct after add(int, Object)")
        @Tag("basic")
        @Test
        void testSizeAddIntObject() {
            var object = new TestObject();
            assertSize(0, list);
            range(0, N).forEachOrdered(i -> {
                list.add(i, object);
                assertSize(i + 1, list);
            });
        }

        @DisplayName("test size() correct after add(Object)")
        @Tag("basic")
        @Test
        void testSizeAddObject() {
            var object = new TestObject();
            assertSize(0, list);
            rangeClosed(1, N).forEachOrdered(i -> {
                list.add(object);
                assertSize(i, list);
            });
        }

        @DisplayName("test size()")
        @Tag("basic")
        @Test
        void testSizeEmpty() {
            assertSize(0, list);
        }
    }

    @DisplayName("when non-empty")
    @Nested
    class WhenNonEmpty {

        @DisplayName("Iterator tests")
        @Nested
        @Tag("Iterator")
        class IteratorTests {

            @DisplayName("test iterator(), Iterator::hasNext(), Iterator::next()")
            @Test
            void testIterator() {
                var arrayListIter = arrayList.iterator();
                var iter = list.iterator();

                while (arrayListIter.hasNext()) {
                    assertTrue(iter.hasNext());
                    assertSame(arrayListIter.next(), iter.next());
                }
                assertFalse(iter.hasNext());
            }
        }

        @DisplayName("ListIterator tests")
        @Nested
        @Tag("ListIterator")
        class ListIteratorTests {

            @DisplayName("test hasNext, nextIndex, next, hasPrevious, previousIndex, previous")
            @Test
            void testListIterator() {
                var arrayListIter = arrayList.listIterator();
                var iter = list.listIterator();
                assertNotNull(iter);

                range(0, N).forEachOrdered(i -> {
                    assertFalse(iter.hasPrevious());
                    assertEquals(-1, iter.previousIndex());

                    range(0, i).forEachOrdered(j -> {
                        assertTrue(iter.hasNext());
                        assertEquals(j, iter.nextIndex());
                        assertSame(arrayListIter.next(), iter.next());
                    });

                    for (int j = i; j > 0; --j) {
                        assertTrue(iter.hasPrevious());
                        assertEquals(j - 1, iter.previousIndex());
                        assertSame(arrayListIter.previous(), iter.previous());
                    }
                });
            }

            @DisplayName("test remove")
            @Test
            void testListIteratorRemove() {
                var arrayListIter = arrayList.listIterator();
                var iter = list.listIterator();
                assertNotNull(iter);

                Random random = new Random(0x85015a62c35ca8fL);
                for (int i = N; i > 0; --i) {
                    int index = random.nextInt(i);
                    moveToIndex(iter, index);
                    moveToIndex(arrayListIter, index);
                    assertSame(arrayListIter.next(), iter.next());

                    arrayListIter.remove();
                    iter.remove();

                    assertSameContents(arrayList, list);
                }
            }

            @DisplayName("test set")
            @Test
            void testListIteratorSet() {
                var arrayListIter = arrayList.listIterator();
                var iter = list.listIterator();
                assertNotNull(iter);

                Random random = new Random(0x189fa707deaa00d5L);
                range(1, N).forEachOrdered(i -> {
                    int index = random.nextInt(i);
                    moveToIndex(arrayListIter, index);
                    moveToIndex(iter, index);
                    assertSame(arrayListIter.next(), iter.next());

                    var object = new TestObject();
                    arrayListIter.set(object);
                    iter.set(object);

                    assertSameContents(arrayList, list);
                });
            }
        }

        ArrayList<TestObject> arrayList;
        List list;

        private void assertSet(ArrayList<TestObject> arrayList, List list, int index, TestObject object) {
            String fmt = "set(%d, %s)";
            assertSame(arrayList.set(index, object), list.set(index, object), String.format(fmt, index, object));
        }

        @BeforeEach
        void beforeEach() {
            list = newList();
            arrayList = new ArrayList<>(N);
            for (int i = 0; i < N; ++i) {
                add(arrayList, list, new TestObject());
            }
        }

        @DisplayName("test remove(int)")
        @Tag("basic")
        @Test
        void testRemove() {
            Random random = new Random(0x2874c9b483185737L);
            int size;
            while ((size = arrayList.size()) > 0) {
                int index = random.nextInt(size);
                assertSame(arrayList.remove(index), list.remove(index), "remove(int) returned the incorrect element");
                assertSameContents(arrayList, list);
            }
        }

        @DisplayName("test set(int, Object)")
        @Tag("basic")
        @Test
        void testSet() {
            Random random = new Random(0xb890175a7859835L);

            for (int i = 0; i < N; ++i) {
                int index = random.nextInt(N);
                var object = new TestObject();
                assertSet(arrayList, list, index, object);
                assertSameContents(arrayList, list);
            }
        }

        @DisplayName("test size() correct after remove(int)")
        @Tag("basic")
        @Test
        void testSizeRemove() {
            for (int i = N; i > 0; --i) {
                assertSize(i, list);
                list.remove(0);
            }
            assertSize(0, list);
        }
    }
}