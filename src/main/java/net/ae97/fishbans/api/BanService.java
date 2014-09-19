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
package net.ae97.fishbans.api;

/**
 * The list of ban service provides which http://fishbans.com retrieves bans
 * from.
 *
 * @since 1.0
 * @author Lord_Ralex
 */
public enum BanService {

    MCBANS("McBans", false),
    MCBOUNCER("McBouncer", false),
    MINEBANS("MineBans", false),
    MCBLOCKIT("McBlockIt", true),
    GLIZER("Glizer", false);

    private final String displayName;
    private final boolean legacy;

    private BanService(String displayName, boolean legacy) {
        this.displayName = displayName;
        this.legacy = legacy;
    }

    /**
     * Returns the user-friendly name for this ban service.
     *
     * @return User-friendly name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns whether the ban information for this ban service is legacy data
     * on http://fishbans.com. Legacy ban services are ban services who no
     * longer run or permit Fishbans from retrieving their data. Any data stored
     * by Fishbans from such providers may be old and not contain new bans.
     *
     * @return True if bans are legacy data, false otherwise
     */
    public boolean isLegacy() {
        return legacy;
    }

    @Override
    public String toString() {
        return "BanService{id=" + this.name() + ", displayName=" + getDisplayName() + ", legacy=" + isLegacy() + "}";
    }

    public static BanService getService(String name) {
        for (BanService service : BanService.values()) {
            if (service.name().equalsIgnoreCase(name)) {
                return service;
            }
        }
        return null;
    }

}
