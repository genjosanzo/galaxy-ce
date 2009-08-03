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

package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Collection;
import java.util.Iterator;

public class WLifecycle implements IsSerializable {
    
    private String name;
    private String id;
    private Collection<WPhase> phases;
    private WPhase initialPhase;
    private boolean defaultLifecycle;
    
    public WLifecycle() {
    }

    public WLifecycle(String id, String name, boolean defaultLifecyle) {
        this.id = id;
        this.name = name;
        defaultLifecycle = defaultLifecyle;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDefaultLifecycle() {
        return defaultLifecycle;
    }

    public void setDefaultLifecycle(boolean defaultLifecycle) {
        this.defaultLifecycle = defaultLifecycle;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Collection<WPhase> getPhases() {
        return phases;
    }
    public void setPhases(Collection<WPhase> phases) {
        this.phases = phases;
    }

    public WPhase getInitialPhase() {
        return initialPhase;
    }

    public void setInitialPhase(WPhase initialPhase) {
        this.initialPhase = initialPhase;
    }

    public WPhase getPhase(String name) {
        if (phases == null) return null;
        
        for (Iterator<WPhase> itr = phases.iterator(); itr.hasNext();) {
            WPhase p = itr.next();
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    public WPhase getPhaseById(String id) {
        if (phases == null) return null;
        
        for (Iterator<WPhase> itr = phases.iterator(); itr.hasNext();) {
            WPhase p = itr.next();
            if (p.getId() != null && p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }
   
}