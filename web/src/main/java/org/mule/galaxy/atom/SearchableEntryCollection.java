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


import static org.mule.galaxy.util.AbderaUtils.createArtifactPolicyExceptionResponse;
import static org.mule.galaxy.util.AbderaUtils.newErrorMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Person;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.RequestContext.Scope;
import org.apache.abdera.protocol.server.context.EmptyResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Item;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;

public class SearchableEntryCollection extends AbstractEntryCollection {

    public SearchableEntryCollection(Registry registry) {
        super(registry);
    }

    @Override
    protected String addEntryDetails(RequestContext request, 
                                     Entry e, 
                                     IRI entryBaseIri, 
                                     Item entryObj)
        throws ResponseContextException {
        String link = super.addEntryDetails(request, e, entryBaseIri, entryObj);
        
        Artifact a = getArtifact(entryObj);
        if (a != null) {
            Collection col = factory.newCollection();
            col.setAttributeValue("id", "versions");
            col.setHref(getRelativeLink(request, entryObj) + ";history");
            col.setTitle("Artifact Versions");
            e.addExtension(col);
        } else if (entryObj instanceof Workspace) {
            Collection col = factory.newCollection();
            col.setAttributeValue("id", "items");
            col.setHref(getRelativeLink(request, entryObj).toString());
            col.setTitle("Workspace Items");
            e.addExtension(col);
        }
        
        return link;
    }

    private IRI getRelativeLink(RequestContext request, Item entryObj) {
        return new IRI(getHref(request)).resolve(getNameOfArtifact(entryObj));
    }


    public String getId(RequestContext request) {
        return "tag:galaxy.mulesource.com,2008:registry:" + registry + ":feed";
    }
    
    public String getTitle(RequestContext request) {
        return "Mule Galaxy Registry/Repository";
    }

    public Iterable<Item> getEntries(RequestContext request) throws ResponseContextException {
        try {
            String q = request.getParameter("q");
            
            if (q == null || "".equals(q)) {
                Item item = getRegistryItem(request);
                if (item == null) {
                    List<Item> items = new ArrayList<Item>();
                    for (Workspace w : registry.getWorkspaces()) {
                        items.add(w);
                    }
                    return items;
                } else {
                    return ((Workspace) item).getItems();
                }
                
            } else {
                q = UrlEncoding.decode(q);
            }
            
            final Iterator results = registry.search(q, 0, 100).getResults().iterator();
            
            return createEntryVersionIterable(results, request);
        } catch (RegistryException e) {
            throw new ResponseContextException(500, e);
        } catch (AccessException e) {
            throw new ResponseContextException(401, e);
        }
    }

    protected Iterable<Item> createEntryVersionIterable(final Iterator<?> results, 
                                                        final RequestContext context) {
        return new Iterable<Item>() {

            public Iterator<Item> iterator() {
                return new Iterator<Item>() {

                    public boolean hasNext() {
                        return results.hasNext();
                    }

                    public Item next() {
                        Object next = results.next();
                        EntryVersion av = null;
                        if (next instanceof EntryVersion) {
                            av = (EntryVersion) next;
                        } else {
                            av = ((org.mule.galaxy.Entry) next).getDefaultOrLastVersion();
                        }
                        
                        return av;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
            
        };
    }

    @Override
    protected EntryResult postMediaEntry(String slug, 
                                            MimeType mimeType, 
                                            String version,
                                            InputStream inputStream, 
                                            User user,
                                            RequestContext request)
        throws RegistryException, PolicyException, IOException, MimeTypeParseException, ResponseContextException, DuplicateItemException, AccessException  {

        Workspace workspace = (Workspace) request.getAttribute(Scope.REQUEST, EntryResolver.ITEM);

        if (workspace == null) {
            EmptyResponseContext ctx = new EmptyResponseContext(500);
            ctx.setStatusText("The specified workspace is invalid. Please POST to a valid workspace URL.");
            
            throw new ResponseContextException(ctx);
        }
        
        return workspace.createArtifact(mimeType.toString(), 
                                        slug, 
                                        version, 
                                        inputStream);
    }

    
    @Override
    public Item postEntry(String title, IRI id, String summary, Date updated, List<Person> authors,
                          Content content, RequestContext request) throws ResponseContextException {
        Workspace workspace = (Workspace) request.getAttribute(Scope.REQUEST, EntryResolver.ITEM);

        
        try {
            Document<Entry> e = request.getDocument();
            Entry atomEntry = e.getRoot();
            Element workspaceInfo = atomEntry.getExtension(new QName(NAMESPACE, "workspace-info"));
            Item item;
            
            if (workspaceInfo != null) {
                String name = workspaceInfo.getAttributeValue("name");
                if (name == null) {
                    name = title;
                }
                
                if (workspace == null) {
                    item = registry.newWorkspace(name);
                } else { 
                    item = workspace.newWorkspace(name);
                }
                
                workspaceInfo.discard();
            } else {
                if (workspace == null) {
                    EmptyResponseContext ctx = new EmptyResponseContext(500);
                    ctx.setStatusText("The specified workspace is invalid. Please POST to a valid workspace URL.");
                    
                    throw new ResponseContextException(ctx);
                }
                
                // The name can be specified in either the entry-info or we can fall back on the title
                Element entryInfo = atomEntry.getExtension(new QName(NAMESPACE, "entry-info"));
                String name = null;
                if (entryInfo != null) { 
                    name = entryInfo.getAttributeValue("name");
                }
                if (name == null) {
                    name = title;
                }
                
                String label = getVersionLabel(atomEntry);
                
                item = workspace.newEntry(name, label).getEntryVersion();
                
                if (entryInfo != null) {
                    entryInfo.discard();
                }
            }
            
            // Update property information
            mapEntryExtensions(item, atomEntry);
            
            // TODO: remove metadata from request?
            
            // fill in the response information
            addEntryDetails(request, atomEntry, getFeedIRI(item, request), item);
            return item;
        } catch (DuplicateItemException e) {
            throw newErrorMessage("Duplicate artifact.", "An artifact with that name already exists in this workspace.", 409);
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

    private IRI getFeedIRI(Item entryObj, RequestContext request) {
      String feedIri = getFeedIriForEntry(entryObj, request);
      return new IRI(feedIri).trailingSlash();
    }
    
    /**
     * Override this method so we don't clone the document and we can actually modify the POST response....
     * 
     */
    protected Entry getEntryFromRequest(RequestContext request) throws ResponseContextException {
        Abdera abdera = request.getAbdera();
        Parser parser = abdera.getParser();

        Document<Element> entry_doc;
        try {
            entry_doc = request.getDocument(parser);
        } catch (ParseException e) {
            throw new ResponseContextException(500, e);
        } catch (IOException e) {
            throw new ResponseContextException(500, e);
        }
        if (entry_doc == null) {
            return null;
        }
        return (Entry) entry_doc.getRoot();
    }
    
    @Override
    public void putMedia(Item item,
                         MimeType contentType, 
                         String slug, 
                         InputStream inputStream, 
                         RequestContext request)
        throws ResponseContextException {
        Artifact a = (Artifact) item;
        
        try {
            a.newVersion(inputStream, getVersion(request));
        } catch (RegistryException e) {
            throw new ResponseContextException(500, e);
        } catch (PolicyException e) {
           throw createArtifactPolicyExceptionResponse(e);
        } catch (IOException e) {
            throw new ResponseContextException(500, e);
        } catch (DuplicateItemException e) {
            throw new ResponseContextException(409, e);
        } catch (AccessException e) {
            throw new ResponseContextException(401, e);
        }
    }

}
