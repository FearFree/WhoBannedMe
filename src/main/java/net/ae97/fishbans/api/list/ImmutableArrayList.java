/*
 * Copyright (C) 2014 Lord_Ralex
 *
 * This file is a part of FishbansAPI
 *
 * FishbansAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FishbansAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FishbansAPI.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.ae97.fishbans.api.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.lang3.StringUtils;

/**
 * A {@link List} implentation which does not permit adding or removing items
 * from it. This uses an array as the backend.
 *
 * @since 1.1
 *
 * @author Lord_Ralex
 */
public class ImmutableArrayList<T extends Object> implements List<T> {

    private final T[] objects;

    public ImmutableArrayList(Collection<T> list) {
        objects = (T[]) list.toArray();
    }

    @Override
    public int size() {
        return objects.length;
    }

    @Override
    public boolean isEmpty() {
        return objects.length == 0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) == -1;
    }

    @Override
    public Iterator<T> iterator() {
        return new ImmutableIterator<T>(this);
    }

    @Override
    public Object[] toArray() {
        return objects;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return (T[]) objects;
    }

    @Override
    public boolean add(T e) {
        throw new UnsupportedOperationException("Cannot add elements to an ImmutableArrayList");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Cannot remove elements from an ImmutableArrayList");
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        for (Object o : collection) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException("Cannot add elements to an ImmutableArrayList");
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException("Cannot add elements to an ImmutableArrayList");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Cannot remove elements from an ImmutableArrayList");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Cannot remove elements from an ImmutableArrayList");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Cannot remove elements from an ImmutableArrayList");
    }

    @Override
    public T get(int index) {
        return objects[index];
    }

    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException("Cannot edit elements in an ImmutableArrayList");
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException("Cannot add elements to an ImmutableArrayList");
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException("Cannot remove elements from an ImmutableArrayList");
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < objects.length; i++) {
            if (objects[i].equals(o)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        for (int i = objects.length - 1; i >= 0; i++) {
            if (objects[i].equals(o)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public ListIterator<T> listIterator() {
        return new ImmutableListIterator<T>(this);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new ImmutableListIterator<T>(subList(index, size()));
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        LinkedList<T> backend = new LinkedList<T>();
        for (int i = fromIndex; i < toIndex; i++) {
            backend.add(objects[i]);
        }
        return new ImmutableArrayList<T>(backend);
    }

    @Override
    public String toString() {
        return "ImmutableArrayList{id=" + super.toString() + ", elements={" + StringUtils.join(this, ", ") + "}}";
    }
}
