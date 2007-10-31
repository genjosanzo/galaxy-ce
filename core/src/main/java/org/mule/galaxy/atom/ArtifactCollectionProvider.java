package org.mule.galaxy.atom;


import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Text.Type;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.impl.AbstractCollectionProvider;
import org.apache.abdera.protocol.server.impl.EmptyResponseContext;
import org.apache.abdera.protocol.server.impl.ResponseContextException;
import org.apache.abdera.protocol.util.EncodingUtil;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.QueryException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;

public class ArtifactCollectionProvider extends AbstractCollectionProvider<Artifact> {
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
    
    public Content getContent(Artifact doc, RequestContext request) {
        // Not used since these are media entries
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected void addEntryDetails(RequestContext request, Entry e, IRI entryBaseIri, Artifact entryObj)
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
    public Text getSummary(Artifact entry, RequestContext request) {
        Text summary = factory.newSummary();
        
        summary.setText("Version " + entry.getLatestVersion().getVersionLabel());
        summary.setTextType(Type.TEXT);
        
        return summary;
    }

    @Override
    public String getMediaName(Artifact entry) {
        return entry.getId() + "+" + entry.getName();
    }

    @Override
    public String getContentType(Artifact entry) {
        return entry.getContentType().toString();
    }

    public String getAuthor() {
        return "Galaxy";
    }

    public String getId(Artifact doc) {
        return ID_PREFIX + doc.getId();
    }

    public String getName(Artifact doc) {
        return doc.getId() + "+" +  doc.getName() + ".atom";
    }

    public String getTitle() {
        return workspace.getName();
    }

    public String getTitle(Artifact doc) {
        if (doc.getName() != null) {
            return doc.getName();
        } else {
            return doc.getDocumentType().toString();
        }
    }

    public Date getUpdated(Artifact doc) {
        return doc.getLatestVersion().getCreated().getTime();
    }

    @Override
    public Artifact createEntry(String arg0, IRI arg1, String arg2, Date arg3, List<Person> arg4, Content arg5, RequestContext request)
        throws ResponseContextException {
        throw new ResponseContextException(new EmptyResponseContext(500));
    }

    public Artifact createMediaEntry(MimeType mimeType, String slug, 
                                     InputStream inputStream, RequestContext request) throws ResponseContextException {
        try {
            String version = request.getHeader("X-Artifact-Version");
            
            if (version == null || version.equals("")) {
                EmptyResponseContext ctx = new EmptyResponseContext(500);
                ctx.setStatusText("You must supply an X-Artifact-Version header!");
                
                throw new ResponseContextException(ctx);
            }
            return registry.createArtifact(workspace, 
                                           mimeType.toString(), 
                                           slug, 
                                           version, inputStream);
        } catch (RegistryException e) {
            throw new ResponseContextException(500, e);
        } catch (IOException e) {
            throw new ResponseContextException(500, e);
        } catch (MimeTypeParseException e) {
            throw new ResponseContextException(500, e);
        }
    }

    @Override
    public boolean isMediaEntry(Artifact entry) {
        return true;
    }

    @SuppressWarnings("unchecked")
    public Iterable<Artifact> getEntries(RequestContext request) throws ResponseContextException {
        try {
            String q = request.getParameter("q");
            
            if (q != null) {
                q = EncodingUtil.decode(q);
                
                return registry.search(q);
            } else {
                return registry.getArtifacts(workspace);
            }
        } catch (RegistryException e) {
            throw new ResponseContextException(500, e);
        } catch (QueryException e) {
            throw new ResponseContextException(500, e);
        }
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
    public List<Person> getAuthors(Artifact entry, RequestContext request) throws ResponseContextException {
        Person author = request.getAbdera().getFactory().newAuthor();
        author.setName("Galaxy");
        return Arrays.asList(author);
    }

    @Override
    public void updateEntry(Artifact entry, String title, Date updated, List<Person> authors, String summary,
                            Content content, RequestContext request) throws ResponseContextException {
        // TODO Auto-generated method stub
        
    }

    public Artifact getEntryFromId(String id, RequestContext request) {
        id = id.substring(ID_PREFIX.length());
        
        try {
            return registry.getArtifact(id);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Artifact getEntry(String name, RequestContext request) {
        name = parseName(name);
        try {
            return registry.getArtifact(name);
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
