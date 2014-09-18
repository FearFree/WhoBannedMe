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
import java.util.Stack;
import org.apache.commons.lang3.StringUtils;

/**
 * A {@link Iterator} implentation which does not permit editing of the
 * {@link Collection} that is being iterated.
 *
 * @since 1.1
 *
 * @author Lord_Ralex
 */
public class ImmutableIterator<T extends Object> implements Iterator<T> {

    private final Stack<T> objects;

    public ImmutableIterator(Collection<T> collection) {
        objects = new Stack<T>();
        for (T o : (T[]) collection.toArray()) {
            objects.push(o);
        }
    }

    @Override
    public boolean hasNext() {
        return !objects.empty();
    }

    @Override
    public T next() {
        return objects.pop();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Cannot remove elements from an ImmutableListIterator");
    }

    @Override
    public String toString() {
        return "ImmutableArrayList{id=" + super.toString() + ", stack={" + StringUtils.join(this, ", ") + "}}";
    }
}
