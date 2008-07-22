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

import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.policy.PolicyManager;
import org.mule.galaxy.security.AccessException;

import java.util.Date;
import java.util.List;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.RequestContext.Scope;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;

/**
 * Manage artifact workspaces through an AtomPub collection.
 */
public class WorkspaceCollection extends AbstractEntityCollectionAdapter<Workspace> {

    public static final String ID_PREFIX = "urn:galaxy:workspaces:";
    
    private Registry registry;
    
    public WorkspaceCollection(Registry registry) {
        super();
        this.registry = registry;
    }

    @Override
    protected String addEntryDetails(RequestContext request, Entry e, IRI feedIri, Workspace entryObj)
        throws ResponseContextException {
        
        return super.addEntryDetails(request, e, feedIri, entryObj);
    }

    @Override
    public void deleteEntry(String resourceName, RequestContext request) throws ResponseContextException {
        Workspace w = getResolvedWorkspace(request);
        
        if (w == null) {
            throw new ResponseContextException(404);
        }
        
        try {
            w.delete();
        } catch (RegistryException e) {
            throw new ResponseContextException(500, e);
        } catch (AccessException e) {
            throw new ResponseContextException(405, e);
        }
    }

    private Workspace getResolvedWorkspace(RequestContext request) {
        return (Workspace) request.getAttribute(Scope.REQUEST, EntryResolver.WORKSPACE);
    }

    @Override
    public Object getContent(Workspace entry, RequestContext request) throws ResponseContextException {
        return null;
    }

    @Override
    public Iterable<Workspace> getEntries(RequestContext request) throws ResponseContextException {
        Workspace parent = getResolvedWorkspace(request);

        if (parent == null) {
            try {
                return registry.getWorkspaces();
            } catch (RegistryException e) {
                throw new ResponseContextException(500, e);
            } catch (AccessException e) {
                throw new ResponseContextException(405, e);
            }
        }
        
        return parent.getWorkspaces();
    }

    @Override
    public Workspace getEntry(String resourceName, RequestContext request) throws ResponseContextException {
        Workspace parent = getResolvedWorkspace(request);
        
        return parent.getWorkspace(resourceName);
    }

    @Override
    public String getId(Workspace entry) throws ResponseContextException {
        return ID_PREFIX + entry.getId();
    }

    @Override
    public String getName(Workspace entry) throws ResponseContextException {
        return entry.getName();
    }

    @Override
    public String getTitle(Workspace entry) throws ResponseContextException {
        return entry.getName();
    }

    @Override
    public Date getUpdated(Workspace entry) throws ResponseContextException {
        return entry.getUpdated().getTime();
    }

    @Override
    public Workspace postEntry(String title, 
                               IRI id, 
                               String summary, 
                               Date updated, 
                               List<Person> authors,
                               Content content, 
                               RequestContext request) throws ResponseContextException {
        Workspace parent = getResolvedWorkspace(request);
        
        try {
            if (parent == null) {
                return registry.createWorkspace(title);
            } else {
                return registry.createWorkspace(parent, title);
            }
        } catch (RegistryException e) {
            throw new ResponseContextException(500, e);
        } catch (DuplicateItemException e) {
            throw new ResponseContextException(409);
        } catch (AccessException e) {
            throw new ResponseContextException(405, e);
        }
    }

    @Override
    public void putEntry(Workspace entry, String title, Date updated, List<Person> authors, String summary,
                         Content content, RequestContext request) throws ResponseContextException {
        throw new ResponseContextException(415);
    }

    @Override
    public String getAuthor(RequestContext request) throws ResponseContextException {
        return "Mule Galaxy";
    }

    @Override
    public String getId(RequestContext request) {
        return ID_PREFIX + getResolvedWorkspace(request) + ":workspaces";
    }

    public String getTitle(RequestContext request) {
        Workspace wkspc = getResolvedWorkspace(request);
        if (wkspc != null) {
            return "Workspaces in " + wkspc.getName();
        } else {
            return "Root Workspaces";
        }
    }
    
    @Override
    public String getHref(RequestContext request) {
        String href = (String) request.getAttribute(Scope.REQUEST, EntryResolver.COLLECTION_HREF);
        if (href == null) {
            // this is the url we use when pulling down the services document
            href = request.getTargetBasePath() + "/registry/";
        }
        return href;
    }
}
