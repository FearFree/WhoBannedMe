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

import java.util.List;
import java.util.ListIterator;
import org.apache.commons.lang3.StringUtils;

/**
 * A {@link ListIterator} implentation which does not permit editing of the
 * {@link List} that is being iterated.
 *
 * @since 1.1
 *
 * @author Lord_Ralex
 */
public class ImmutableListIterator<T extends Object> implements ListIterator<T> {

    private int currentIndex = -1;
    private final Object[] objects;

    public ImmutableListIterator(List<T> collection) {
        objects = collection.toArray(new Object[collection.size()]);
    }

    @Override
    public boolean hasNext() {
        return currentIndex + 1 < objects.length;
    }

    @Override
    public T next() {
        currentIndex++;
        if (currentIndex == objects.length) {
            currentIndex = objects.length - 1;
        }
        return (T) objects[currentIndex];
    }

    @Override
    public boolean hasPrevious() {
        return currentIndex - 1 >= 0;
    }

    @Override
    public T previous() {
        currentIndex--;
        if (currentIndex == -1) {
            currentIndex = 0;
        }
        return (T) objects[currentIndex];
    }

    @Override
    public int nextIndex() {
        if (currentIndex < objects.length - 1) {
            return currentIndex + 1;
        } else {
            return objects.length - 1;
        }
    }

    @Override
    public int previousIndex() {
        if (currentIndex > 0) {
            return currentIndex - 1;
        } else {
            return 0;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Cannot remove elements from an ImmutableListIterator");
    }

    @Override
    public void set(T e) {
        throw new UnsupportedOperationException("Cannot change elements in an ImmutableListIterator");
    }

    @Override
    public void add(T e) {
        throw new UnsupportedOperationException("Cannot add elements to an ImmutableListIterator");
    }

    @Override
    public String toString() {
        return "ImmutableArrayList{id=" + super.toString() + ", currentIndex=" + currentIndex + ",  elements={" + StringUtils.join(this, ", ") + "}}";
    }

}
