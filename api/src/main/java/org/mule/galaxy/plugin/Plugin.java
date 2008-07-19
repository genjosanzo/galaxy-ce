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

package org.mule.galaxy.plugin;

import org.mule.galaxy.Registry;

public interface Plugin {

    void setRegistry(Registry r);

    /**
     * Previous version will be <code>null</code> if previous plugin version detected,
     * otherwise a version number of the installed plugin.
     * @param previousVersion currently installed plugin version or null
     * @throws UpgradeNotSupportedException
     * @throws DowngradeNotSupportedException
     */
    void update(Integer previousVersion) throws Exception;
    boolean isDowngradeSupported();
    boolean isUpgradeSupported();
    
    String getName();
    int getVersion();

    void initialize() throws Exception;
}
