/*
 * $Id: ContextPathResolver.java 794 2008-04-23 22:23:10Z andrew $
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

package org.mule.galaxy.atom;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.query.Query;
import org.mule.galaxy.query.QueryException;

import java.util.Iterator;

import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.RequestContext.Scope;
import org.apache.abdera.protocol.server.context.ResponseContextException;

/**
 * This collection will display all the artifacts within a particular workspace.
 * It maps to workspace URLs such as "/api/registry/myWorkspace/".
 */
public class ArtifactWorkspaceCollection extends SearchableArtifactCollection {

    public ArtifactWorkspaceCollection(Registry registry) {
        super(registry);
    }

    @Override
    public Iterable<ArtifactVersion> getEntries(RequestContext request) throws ResponseContextException {
        Workspace w = (Workspace) request.getAttribute(Scope.REQUEST, ArtifactResolver.WORKSPACE);
        
        Query query = new Query(Artifact.class).workspaceId(w.getId());
        
        Iterator<?> results;
        try {
            results = registry.search(query).getResults().iterator();
        } catch (QueryException e) {
            throw new ResponseContextException(500, e);
        } catch (RegistryException e) {
            throw new ResponseContextException(500, e);
        }
        
        return createArtifactVersionIterable(results, request);
    }

}
