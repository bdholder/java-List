package org.roundrockisd.stonypoint.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.roundrockisd.stonypoint.util.List;

public class ArrayList implements List {
    private static final int DEFAULT_CAPACITY = 10;

    // This toy iterator does not throw ConcurrentModificationException.
    private class ArrayListIterator implements ListIterator<Object> {
        private int lastIndex = -1;
        private int index = 0;

        public ArrayListIterator(int index) {
            this.index = index;
        }

        @Override
        public void add(Object e) {
            lastIndex = -1;
            ArrayList.this.add(index++, e);
        }

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public boolean hasPrevious() {
            return index > 0;
        }

        @Override
        public Object next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            lastIndex = index;
            return elementData[index++];
        }

        @Override
        public int nextIndex() {
            return index;
        }

        @Override
        public Object previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            lastIndex = index - 1;
            return elementData[--index];
        }

        @Override
        public int previousIndex() {
            return index - 1;
        }

        @Override
        public void remove() {
            if (lastIndex == -1) {
                throw new IllegalStateException();
            }

            // This wonky syntax calls an instance method of the enclosing class.
            ArrayList.this.remove(lastIndex);
            if (index > lastIndex) {
                --index;
            }
            lastIndex = -1;
        }

        @Override
        public void set(Object e) {
            if (lastIndex == -1) {
                throw new IllegalStateException();
            }

            ArrayList.this.set(lastIndex, e);
            lastIndex = -1;
        }
    }

    private Object[] elementData;
    private int size = 0;

    public ArrayList() {
        /* It is good practice to avoid "magic numbers"; constants with meaningful names are
        * self-documenting.
        */
        elementData = new Object[DEFAULT_CAPACITY];
    }

    @Override
    public void add(int index, Object element) {
        // This index check is slightly different from the normal one.
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }

        ensureCapacity(size + 1);

        /* There is a handy arraycopy tool hidden in java.lang.System. I've left in the manual for-loop
        * copy to illustrate the technique.
        */
        //System.arraycopy(elementData, index, elementData, index + 1, size - index);
        for (int i = size; i > index; --i) {
            elementData[i] = elementData[i - 1];
        }
        
        elementData[index] = element;
        ++size;
    }

    @Override
    public boolean add(Object element) {
        ensureCapacity(size + 1);
        elementData[size++] = element;
        return true;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
    * Make sure we have enough room, and resize if we don't. This method is modeled after OpenJDK's
    * ArrayList implementation. Since we only ever add one item at time in our toy ArrayList, we
    * don't need especially complicated logic here.
    */
    private void ensureCapacity(int capacity) {
        if (elementData.length < capacity) {
        // We use 2 * length + 1 to avoid the edge case of length being 0.
            elementData = Arrays.copyOf(elementData, 2 * elementData.length + 1);
        }
    }

    @Override
    public Object get(int index) {
        checkIndex(index);
        return elementData[index];
    }

    @Override
    public Iterator<Object> iterator() {
        return new ArrayListIterator(0);
    }

    @Override
    public ListIterator<Object> listIterator() {
        return new ArrayListIterator(0);
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        return new ArrayListIterator(index);
    }

    @Override
    public Object remove(int index) {
        checkIndex(index);
        Object old = elementData[index];

        // See comment in add(int, Object);
        // System.arraycopy(elementData, index + 1, elementData, index, size - (index + 1));
        for (int i = index; i < size - 1; ++i) {
        elementData[i] = elementData[i + 1];
        }
        
        /* It is good practice to not retain unnecessary references to objects, because doing so could
        * prevent them from being garbage collected. See "Eliminate obsolete object references" in
        * Bloch's "Effective Java."
        */
        elementData[size] = null;
        --size;
        return old;
    }

    @Override
    public Object set(int index, Object element) {
        checkIndex(index);
        Object old = elementData[index];
        elementData[index] = element;
        return old;
    }

    @Override
    public int size() {
        return size;
    }
}