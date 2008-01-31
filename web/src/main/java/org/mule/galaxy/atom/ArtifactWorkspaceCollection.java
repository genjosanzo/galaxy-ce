package org.mule.galaxy.atom;

import org.mule.galaxy.api.Artifact;
import org.mule.galaxy.api.ArtifactVersion;
import org.mule.galaxy.api.Registry;
import org.mule.galaxy.api.RegistryException;
import org.mule.galaxy.api.Workspace;
import org.mule.galaxy.api.query.Query;
import org.mule.galaxy.api.query.QueryException;
import org.mule.galaxy.query.QueryImpl;

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
        
        Query query = new QueryImpl(Artifact.class).workspaceId(w.getId());
        
        Iterator results;
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
