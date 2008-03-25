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

package org.mule.galaxy.impl.artifact;

import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.Dao;
import org.mule.galaxy.DowngradeNotSupportedException;
import org.mule.galaxy.Plugin;
import org.mule.galaxy.Registry;
import org.mule.galaxy.UpgradeNotSupportedException;
import org.mule.galaxy.index.IndexManager;
import org.mule.galaxy.view.ViewManager;

/**
 * Makes it easy to add indexes and views for a new artifact type.
 */
public abstract class AbstractArtifactPlugin implements Plugin {
    protected Registry registry;
    protected Dao<ArtifactType> artifactTypeDao;
    protected ViewManager viewManager;
    protected IndexManager indexManager;

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public void setArtifactTypeDao(Dao<ArtifactType> artifactTypeDao) {
        this.artifactTypeDao = artifactTypeDao;
    }

    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    public String getName() {
        return getClass().getName();
    }

    public void update(final Integer previousVersion) throws Exception
    {
        if (null == previousVersion)
        {
            install();
        }
        else if (previousVersion > getVersion())
        {
            if (!isUpgradeSupported())
            {
                throw new UpgradeNotSupportedException();
            }
            // upgrade()
        }
        else if (previousVersion < getVersion())
        {
            if (!isDowngradeSupported())
            {
                throw new DowngradeNotSupportedException();
            }
            // downgrade()
        }

        // same version, nothing else to do
    }

    public abstract void install() throws Exception;

    public boolean isDowngradeSupported()
    {
        return false;
    }

    public boolean isUpgradeSupported()
    {
        return false;
    }

}
