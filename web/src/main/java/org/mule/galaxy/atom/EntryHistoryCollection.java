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


import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.activation.MimeType;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Entry;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;

public class EntryHistoryCollection extends AbstractEntryCollection {
    private final Log log = LogFactory.getLog(getClass());

    public EntryHistoryCollection(Registry registry) {
        super(registry);
    }

    @Override
    public String getMediaName(EntryVersion version) {
        return super.getMediaName(version);
    }
    
    @Override
    public String getQueryParameters(EntryVersion version, RequestContext request) {
        return "version=" + version.getVersionLabel();
    }

    @Override
    protected EntryResult postMediaEntry(String slug,
                                            MimeType mimeType, 
                                            String version,
                                            InputStream inputStream, 
                                            User user,
                                            RequestContext ctx)
        throws ResponseContextException {
        throw new ResponseContextException(501);
    }
    
    @Override
    public EntryVersion postEntry(String title, IRI id, String summary, Date updated, List<Person> authors,
                                  Content content, RequestContext request) throws ResponseContextException {
        Entry entry = getRegistryEntry(request);
        
        try {
            Document<org.apache.abdera.model.Entry> document = request.getDocument();
            org.apache.abdera.model.Entry atomEntry = document.getRoot();
            
            EntryResult result = entry.newVersion(getVersionLabel(atomEntry));
            EntryVersion version = result.getEntryVersion();
            
            
            return version;
        } catch (DuplicateItemException e) {
            throw newErrorMessage("Duplicate version.", "An entry with that version label already exists.", 409);
        } catch (RegistryException e) {
            log.error("Could not add artifact.", e);
            throw new ResponseContextException(500, e);
        } catch (IOException e) {
            throw new ResponseContextException(500, e);
        } catch (PolicyException e) {
            throw createArtifactPolicyExceptionResponse(e);
        } catch (AccessException e) {
            throw new ResponseContextException(401, e);
        }
    }

    @Override
    public void deleteEntry(String entry, RequestContext request) throws ResponseContextException {
        EntryVersion version = getEntry(entry, request);
        
        try {
            version.delete();
        } catch (RegistryException e) {
            log.error(e);
            throw new ResponseContextException(500, e);
        } catch (AccessException e) {
            throw new ResponseContextException(401, e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterable<EntryVersion> getEntries(RequestContext request) throws ResponseContextException {
        return (Iterable<EntryVersion>) getRegistryEntry(request).getVersions();
    }

    @Override
    public String getId(RequestContext request) {
        return "tag:galaxy.mulesource.com,2008:registry:" + registry.getUUID() + ":history:feed";
    }

    @Override
    public String getName(EntryVersion version) {
        return super.getName(version);
    }

    public String getTitle(RequestContext request) {
        return getRegistryEntry(request).getName() + " Revisions";
    }

    public String getTitle(EntryVersion entry) {
        return entry.getParent().getName() + " Version " + entry.getVersionLabel();
    }


}
