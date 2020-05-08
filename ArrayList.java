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
        // TODO: implement me
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
        // TODO: implement me
        return null;
    }

    @Override
    public Object set(int index, Object element) {
        // TODO: implement me
        return null;
    }

    @Override
    public int size() {
        // TODO: implement me
        return -1;
    }
}