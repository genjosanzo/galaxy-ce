/*
 * $Id: LicenseHeader-GPLv2.txt 288 2008-01-29 00:59:35Z andrew $
 * --------------------------------------------------------------------------------------
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.mule.galaxy.repository.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.extjs.gxt.ui.client.data.BeanModelTag;

import java.util.Collection;

public class WPhase implements IsSerializable, BeanModelTag {
    private String id;
    private String name;
    private Collection<WPhase> nextPhases;
    
    public WPhase(String id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public WPhase() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<WPhase> getNextPhases() {
        return nextPhases;
    }

    public void setNextPhases(Collection<WPhase> nextPhases) {
        this.nextPhases = nextPhases;
    }

    public String toString() {
        return getName();
    }

    
}
