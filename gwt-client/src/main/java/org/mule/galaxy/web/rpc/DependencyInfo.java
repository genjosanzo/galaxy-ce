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

public class DependencyInfo implements IsSerializable {
    private boolean userSpecified;
    private boolean dependsOn;
    private String artifactName;
    private String artifactId;
    
    public DependencyInfo() {
        super();
    }
    public DependencyInfo(boolean userSpecified, boolean dependsOn, String artifactName, String artifactId) {
        super();
        this.userSpecified = userSpecified;
        this.dependsOn = dependsOn;
        this.artifactName = artifactName;
        this.artifactId = artifactId;
    }
    
    public boolean isUserSpecified() {
        return userSpecified;
    }
    public void setUserSpecified(boolean userSpecified) {
        this.userSpecified = userSpecified;
    }
    public boolean isDependsOn() {
        return dependsOn;
    }
    public void setDependsOn(boolean dependsOn) {
        this.dependsOn = dependsOn;
    }
    public String getArtifactName() {
        return artifactName;
    }
    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }
    public String getArtifactId() {
        return artifactId;
    }
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }
    
    
}
