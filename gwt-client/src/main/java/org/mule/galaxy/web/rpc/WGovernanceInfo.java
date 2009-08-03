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

public class WGovernanceInfo implements IsSerializable {
    /*
     * @gwt typeArgs java.lang.String
     */
    private Collection nextPhases;
    private String currentPhase;
    private String lifecycle;
    private Collection previousPhases;
    
    public String getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(String lifecycle) {
        this.lifecycle = lifecycle;
    }

    public String getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(String currentPhase) {
        this.currentPhase = currentPhase;
    }

    public Collection getNextPhases() {
        return nextPhases;
    }

    public void setNextPhases(Collection nextPhases) {
        this.nextPhases = nextPhases;
    }

    public void setPreviousPhases(Collection previousPhases) {
        this.previousPhases = previousPhases;
    }

    public Collection getPreviousPhases() {
        return previousPhases;
    }
    
}
