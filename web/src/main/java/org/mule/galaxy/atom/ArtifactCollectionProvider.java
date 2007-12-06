package org.mule.galaxy.atom;


import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.Escaping;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Text.Type;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.impl.AbstractCollectionProvider;
import org.apache.abdera.protocol.server.impl.EmptyResponseContext;
import org.apache.abdera.protocol.server.impl.ResponseContextException;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactPolicyException;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.jcr.UserDetailsWrapper;

public class ArtifactCollectionProvider extends AbstractCollectionProvider<ArtifactVersion> {
    private static final String ID_PREFIX = "urn:galaxy:artifact:";
    private Registry registry;
    private Factory factory = new Abdera().getFactory();
    private Workspace workspace;
    
    public ArtifactCollectionProvider(Registry registry, Workspace workspace) {
        super();
        this.registry = registry;
        this.workspace = workspace;
    }

    public String getId() {
        return "tag:mule.org/galaxy,2007:feed";
    }
    
    public Content getContent(ArtifactVersion doc, RequestContext request) {
        // Not used since these are media entries
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected void addEntryDetails(RequestContext request, 
                                   Entry e, 
                                   IRI entryBaseIri, 
                                   ArtifactVersion entryObj)
        throws ResponseContextException {
        super.addEntryDetails(request, e, entryBaseIri, entryObj);
        
        Factory factory = request.getAbdera().getFactory();
        Collection col = factory.newCollection();
        col.setAttributeValue("id", "versions");
        col.setHref("feed/versions/" + getId(entryObj));
        col.setTitle("Artifact Versions");
        e.addExtension(col);
    }

    @Override
    public Text getSummary(ArtifactVersion entry, RequestContext request) {
        Text summary = factory.newSummary();
        
        summary.setText("Version " + entry.getVersionLabel());
        summary.setTextType(Type.TEXT);
        
        return summary;
    }

    @Override
    public String getMediaName(ArtifactVersion entry) {
        return entry.getParent().getId() + "+" + entry.getParent().getName();
    }

    public InputStream getMediaStream(ArtifactVersion entry) throws ResponseContextException {
        return entry.getStream();
    }
    
    @Override
    public String getContentType(ArtifactVersion entry) {
        return entry.getParent().getContentType().toString();
    }

    public String getAuthor() {
        return "Galaxy";
    }

    public String getId(ArtifactVersion doc) {
        return ID_PREFIX + doc.getParent().getId();
    }

    public String getName(ArtifactVersion doc) {
        return doc.getParent().getId() + "+" +  doc.getParent().getName() + ".atom";
    }

    public String getTitle() {
        return workspace.getName();
    }

    public String getTitle(ArtifactVersion doc) {
        if (doc.getParent().getName() != null) {
            return doc.getParent().getName();
        } else {
            return doc.getParent().getDocumentType().toString();
        }
    }

    public Date getUpdated(ArtifactVersion doc) {
        return doc.getCreated().getTime();
    }

    @Override
    public ArtifactVersion createEntry(String arg0, IRI arg1, String arg2, Date arg3, List<Person> arg4, Content arg5, RequestContext request)
        throws ResponseContextException {
        throw new ResponseContextException(new EmptyResponseContext(500));
    }

    public ArtifactVersion createMediaEntry(MimeType mimeType, String slug, 
                                     InputStream inputStream, RequestContext request) throws ResponseContextException {
        try {
            String version = request.getHeader("X-Artifact-Version");
            
            if (version == null || version.equals("")) {
                EmptyResponseContext ctx = new EmptyResponseContext(500);
                ctx.setStatusText("You must supply an X-Artifact-Version header!");
                
                throw new ResponseContextException(ctx);
            }
            UserDetailsWrapper wrapper = 
                (UserDetailsWrapper) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            
            return registry.createArtifact(workspace, 
                                           mimeType.toString(), 
                                           slug, 
                                           version, inputStream, 
                                           wrapper.getUser()).getArtifactVersion();
        } catch (RegistryException e) {
            throw new ResponseContextException(500, e);
        } catch (IOException e) {
            throw new ResponseContextException(500, e);
        } catch (MimeTypeParseException e) {
            throw new ResponseContextException(500, e);
        } catch (ArtifactPolicyException e) {
            throw new ResponseContextException(500, e);
        }
    }

    @Override
    public boolean isMediaEntry(ArtifactVersion entry) {
        return true;
    }

    @SuppressWarnings("unchecked")
    public Iterable<ArtifactVersion> getEntries(RequestContext request) throws ResponseContextException {
        try {
            String q = request.getParameter("q");
            
            if (q != null) {
                q = Escaping.decode(q);
                
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
            } else {
                final Iterator<Artifact> iterator = registry.getArtifacts(workspace).iterator();
                return new Iterable<ArtifactVersion>() {

                    public Iterator<ArtifactVersion> iterator() {
                        return new Iterator<ArtifactVersion>() {

                            public boolean hasNext() {
                                return iterator.hasNext();
                            }

                            public ArtifactVersion next() {
                                return selectVersion(iterator.next());
                            }

                            public void remove() {
                                throw new UnsupportedOperationException();
                            }
                            
                        };
                    }
                    
                };
            }
        } catch (RegistryException e) {
            throw new ResponseContextException(500, e);
        }
    }

    protected ArtifactVersion selectVersion(Artifact next) {
        return next.getLatestVersion();
    }

    public void deleteEntry(String name, RequestContext request) {
        name = parseName(name);
        Artifact artifact;
        try {
            artifact = registry.getArtifact(name);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        
        try {
            registry.delete(artifact);
        } catch (RegistryException e) {
            throw new RuntimeException(e);
        }
    }
    

    @Override
    public List<Person> getAuthors(ArtifactVersion entry, RequestContext request) throws ResponseContextException {
        Person author = request.getAbdera().getFactory().newAuthor();
        author.setName("Galaxy");
        return Arrays.asList(author);
    }

    @Override
    public void updateEntry(ArtifactVersion entry, String title, Date updated, List<Person> authors, String summary,
                            Content content, RequestContext request) throws ResponseContextException {
        // TODO Auto-generated method stub
        
    }

    public ArtifactVersion getEntryFromId(String id, RequestContext request) {
        id = id.substring(ID_PREFIX.length());
        
        try {
            return selectVersion(registry.getArtifact(id));
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public ArtifactVersion getEntry(String name, RequestContext request) {
        name = parseName(name);
        try {
            return selectVersion(registry.getArtifact(name));
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String parseName(String name) {
        int idx = name.indexOf('+');
        if (idx != -1) {
            name = name.substring(0, idx);
        }
        return name;
    }

    public Registry getRegistry() {
        return registry;
    }

}
