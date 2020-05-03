package org.roundrockisd.stonypoint.util;

import java.util.ListIterator;

public interface List extends Iterable<Object> {
    void add(int index, Object element);
    boolean add(Object element);
    Object get(int index);
    ListIterator<Object> listIterator();
    ListIterator<Object> listIterator(int index);
    Object remove(int index);
    Object set(int index, Object element);
    int size();
}