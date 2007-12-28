package org.mule.galaxy.atom;


import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.namespace.QName;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.Escaping;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Text.Type;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.impl.AbstractCollectionProvider;
import org.apache.abdera.protocol.server.impl.EmptyResponseContext;
import org.apache.abdera.protocol.server.impl.ResponseContextException;
import org.apache.abdera.protocol.util.EncodingUtil;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactPolicyException;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.jcr.UserDetailsWrapper;

public class ArtifactCollectionProvider extends AbstractCollectionProvider<ArtifactVersion> {
    public static final String NAMESPACE = "http://galaxy.mule.org/1.0";
    private static final String ID_PREFIX = "urn:galaxy:artifact:";
    private Registry registry;
    private Factory factory = new Abdera().getFactory();
    
    public ArtifactCollectionProvider(Registry registry) {
        super();
        this.registry = registry;
        setBaseMediaIri("");
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
        
        Element metadata = factory.newElement(new QName(NAMESPACE, "metadata"));
        
        for (Iterator<PropertyInfo> props = entryObj.getProperties(); props.hasNext();) {
            PropertyInfo p = props.next();
            if (p.isVisible()) {
                Element prop = factory.newElement(new QName(NAMESPACE, "property"), metadata);
                prop.setAttributeValue("name", p.getName());
                prop.setAttributeValue("locked", new Boolean(p.isLocked()).toString());
                prop.setAttributeValue("value", p.getValue().toString());
            }
        }
        
        e.addExtension(metadata);
    }

    @Override
    public Text getSummary(ArtifactVersion entry, RequestContext request) {
        Text summary = factory.newSummary();
        
        String d = entry.getParent().getDescription();
        if (d == null) d = "";
        
        summary.setText(d);
        summary.setTextType(Type.XHTML);
        
        return summary;
    }

    @Override
    public String getMediaName(ArtifactVersion entry) {
        Artifact a = entry.getParent();
        StringBuilder path = getBasePath(a);
        
        path.append(entry.getParent().getName());
        return path.toString();
    }

    public InputStream getMediaStream(ArtifactVersion entry) throws ResponseContextException {
        return entry.getStream();
    }
    
    @Override
    public String getContentType(ArtifactVersion entry) {
        return entry.getParent().getContentType().toString();
    }

    public String getAuthor() {
        return "Mule Galaxy";
    }

    public String getId(ArtifactVersion doc) {
        return ID_PREFIX + doc.getParent().getId();
    }

    public String getName(ArtifactVersion doc) {
        Artifact a = doc.getParent();
        StringBuilder sb = getBasePath(a);
        
        sb.append(Escaping.encode(a.getName()));
        sb.append(".atom");
        return sb.toString();
    }

    private StringBuilder getBasePath(Artifact a) {
        StringBuilder sb = new StringBuilder();
        
        Workspace w = a.getWorkspace();
        while (w != null) {
            sb.insert(0, '/');
            sb.insert(0, Escaping.encode(w.getName()));
            w = w.getParent();
        }
        return sb;
    }

    public String getTitle() {
        return "Mule Galaxy Registry/Repository";
    }

    public String getTitle(ArtifactVersion doc) {
        if (doc.getParent().getName() != null) {
            return doc.getParent().getName();
        } else {
            return "(No title) " + doc.getParent().getDocumentType().toString();
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
            String workspaceId = request.getHeader("X-Workspace");
            
            if (version == null || version.equals("")) {
                EmptyResponseContext ctx = new EmptyResponseContext(500);
                ctx.setStatusText("You must supply an X-Artifact-Version header!");
                
                throw new ResponseContextException(ctx);
            }
            
            if (workspaceId == null || workspaceId.equals("")) {
                EmptyResponseContext ctx = new EmptyResponseContext(500);
                ctx.setStatusText("You must supply an X-Workspace header!");
                
                throw new ResponseContextException(ctx);
            }
            
            
            UserDetailsWrapper wrapper = 
                (UserDetailsWrapper) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            
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
            
            if (q == null || "".equals(q)) {
                q = "select artifact";
            } else {
                q = Escaping.decode(q);
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

    public void deleteEntry(String name, RequestContext request) throws ResponseContextException {
        Artifact artifact = findArtifact(name);

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

    private Artifact findArtifact(String name) throws ResponseContextException {
        String[] paths = name.split("/");
        
        Workspace w = null;
        for (int i = 0; i < paths.length-1; i++) {
            try {
                w = registry.getWorkspace(Escaping.decode(paths[0]));
            } catch (NotFoundException e) {
                throw new ResponseContextException(404);
            } catch (RegistryException e) {
                throw new ResponseContextException(500, e);
            }
        }
        Artifact a = null;
        try {
            a = registry.getArtifact(w, Escaping.decode(paths[paths.length-1]));
            
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        return a;
    }


    public Registry getRegistry() {
        return registry;
    }

}
