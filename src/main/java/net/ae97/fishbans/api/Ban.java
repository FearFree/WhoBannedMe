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
 * A Ban record for a particular username or {@link java.util.UUID}.
 *
 * @since 1.0
 * @author Lord_Ralex
 */
public class Ban {

    private final BanService service;
    private final String reason;
    private final String server;

    protected Ban(BanService service, String server, String reason) {
        this.service = service;
        this.server = server;
        this.reason = reason;
    }

    /**
     * Gets the reason for this particular ban
     *
     * @return Ban reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * Gets the server from this ban
     *
     * @return Server ban is from
     */
    public String getServer() {
        return server;
    }

    /**
     * Gets the {@link BanService} that contains this ban reason
     *
     * @return The {@link BanService} which issued this ban
     */
    public BanService getService() {
        return service;
    }

    @Override
    public String toString() {
        return "Ban{service=" + service + ", server=" + server + ", reason=" + reason + "}";
    }

}
