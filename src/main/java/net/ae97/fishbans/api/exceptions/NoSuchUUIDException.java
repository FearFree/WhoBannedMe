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
package net.ae97.fishbans.api.exceptions;

import java.util.UUID;

/**
 * Thrown when no such UUID exists on the Fishbans API
 *
 * @since 1.0
 * @author Lord_Ralex
 */
public class NoSuchUUIDException extends Exception {

    public NoSuchUUIDException(Exception parent) {
        super(parent);
    }

    public NoSuchUUIDException(UUID uuid) {
        super("UUID (" + uuid.toString() + ") is not known by Fishbans");
    }

}
