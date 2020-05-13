package org.roundrockisd.stonypoint.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;
import org.roundrockisd.stonypoint.util.List;

public class ArrayList implements List {
    private Object[] elementData;
    private int size;

    public ArrayList() {
        elementData = new Object[10];
        size = 0;
    }

    @Override
    public void add(int index, Object element) {
        if (size >= elementData.length) {
            elementData = Arrays.copyOf(elementData, size * 2);
        }

        for (int i = size; i > index; i--) {
            elementData[i] = elementData[i - 1];
        }
        elementData[index] = element;
        size++;
    }

    @Override
    public boolean add(Object element) {
        if (size >= elementData.length) {
            elementData = Arrays.copyOf(elementData, size * 2);
        }
        elementData[size] = element;
        size++;
        return true;
    }

    @Override
    public Object get(int index) {
        return elementData[index];
    }

    @Override
    public Iterator<Object> iterator() {
        // TODO: implement me
        return null;
    }

    @Override
    public ListIterator<Object> listIterator() {
        // TODO: implement me
        return null;
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        // TODO: implement me
        return null;
    }

    @Override
    public Object remove(int index) {
        Object old = elementData[index];
        size--;
        for (int i = index; i < size; i++) {
            elementData[i] = elementData[i + 1];
        }
        elementData[size] = null;
        return old;
    }

    @Override
    public Object set(int index, Object element) {
        Object old = elementData[index];
        elementData[index] = element;
        return old;
    }

    @Override
    public int size() {
        return size;
    }
}