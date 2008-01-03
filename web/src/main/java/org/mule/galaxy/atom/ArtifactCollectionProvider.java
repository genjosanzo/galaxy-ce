package org.mule.galaxy.atom;


import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.impl.EmptyResponseContext;
import org.apache.abdera.protocol.server.impl.ResponseContextException;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactPolicyException;
import org.mule.galaxy.ArtifactResult;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.security.User;

public class ArtifactCollectionProvider extends AbstractArtifactVersionProvider {
    
    public ArtifactCollectionProvider(Registry registry) {
        super(registry);
        setBaseMediaIri("");
    }

    @Override
    protected void addEntryDetails(RequestContext request, 
                                   Entry e, 
                                   IRI entryBaseIri, 
                                   ArtifactVersion entryObj)
        throws ResponseContextException {
        super.addEntryDetails(request, e, entryBaseIri, entryObj);
        
        Collection col = factory.newCollection();
        col.setAttributeValue("id", "versions");
        col.setHref(getName(entryObj) + "?type=feed");
        col.setTitle("Artifact Versions");
        e.addExtension(col);
    }

    public String getId() {
        return "tag:mule.org/galaxy,2007:registry:feed";
    }
    @Override
    public String getMediaName(ArtifactVersion entry) {
        Artifact a = entry.getParent();
        StringBuilder path = getBasePath(a);
        
        path.append(entry.getParent().getName());
        return path.toString();
    }


    public String getId(ArtifactVersion doc) {
        return ID_PREFIX + doc.getParent().getId();
    }

    public String getName(ArtifactVersion doc) {
        Artifact a = doc.getParent();
        StringBuilder sb = getBasePath(a);
        
        sb.append(UrlEncoding.encode(a.getName()));
        sb.append(".atom");
        return sb.toString();
    }

    public String getTitle(RequestContext request) {
        return "Mule Galaxy Registry/Repository";
    }

    public String getTitle(ArtifactVersion doc) {
        if (doc.getParent().getName() != null) {
            return doc.getParent().getName();
        } else {
            return "(No title) " + doc.getParent().getDocumentType().toString();
        }
    }

    @SuppressWarnings("unchecked")
    public Iterable<ArtifactVersion> getEntries(RequestContext request) throws ResponseContextException {
        try {
            String q = request.getParameter("q");
            
            if (q == null || "".equals(q)) {
                q = "select artifact";
            } else {
                q = UrlEncoding.decode(q);
            }
            
            final Iterator results = registry.search(q).iterator();
            return new Iterable<ArtifactVersion>() {

                public Iterator<ArtifactVersion> iterator() {
                    return new Iterator<ArtifactVersion>() {

                        public boolean hasNext() {
                            return results.hasNext();
                        }

                        public ArtifactVersion next() {
                            Object next = results.next();
                            if (next instanceof ArtifactVersion) {
                                return (ArtifactVersion) next;
                            } else {
                                return ((Artifact) next).getLatestVersion();
                            }
                        }

                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                        
                    };
                }
                
            };
            
        } catch (RegistryException e) {
            throw new ResponseContextException(500, e);
        }
    }

    protected ArtifactVersion selectVersion(Artifact next) {
        return next.getLatestVersion();
    }

    @Override
    protected ArtifactResult createMediaEntry(String slug, 
                                              MimeType mimeType, 
                                              String version,
                                              InputStream inputStream, 
                                              User user,
                                              RequestContext request)
        throws RegistryException, ArtifactPolicyException, IOException, MimeTypeParseException, ResponseContextException  {

        String workspaceId = request.getHeader("X-Workspace");
        if (workspaceId == null || workspaceId.equals("")) {
            EmptyResponseContext ctx = new EmptyResponseContext(500);
            ctx.setStatusText("You must supply an X-Workspace header!");
            
            throw new ResponseContextException(ctx);
        }
        Workspace workspace = registry.getWorkspaceByPath(workspaceId);

        if (workspace == null) {
            EmptyResponseContext ctx = new EmptyResponseContext(500);
            ctx.setStatusText("The specified workspace is invalid.");
            
            throw new ResponseContextException(ctx);
        }
        
        
        return registry.createArtifact(workspace, 
                                       mimeType.toString(), 
                                       slug, 
                                       version, inputStream, 
                                       user);
    }

    public void deleteEntry(String name, RequestContext request) throws ResponseContextException {
        Artifact artifact = findArtifact(name);

        try {
            registry.delete(artifact);
        } catch (RegistryException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void updateEntry(ArtifactVersion entry, 
                            String title, 
                            Date updated, 
                            List<Person> authors, 
                            String summary,
                            Content content, 
                            RequestContext request) throws ResponseContextException {
        Artifact artifact = entry.getParent();
        artifact.setDescription(summary);
        artifact.setName(title);
    }

    public ArtifactVersion getEntryFromId(String id, RequestContext request) {
        id = id.substring(ID_PREFIX.length());
        
        try {
            return selectVersion(registry.getArtifact(id));
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public ArtifactVersion getEntry(String name, RequestContext request) throws ResponseContextException {
        Artifact a = findArtifact(name);
        return selectVersion(a);
    }


}
