package org.mule.galaxy.atom;


import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Set;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Text.Type;
import org.apache.abdera.protocol.server.content.AbstractCollectionProvider;
import org.apache.abdera.protocol.server.content.ResponseContextException;
import org.apache.abdera.protocol.server.impl.EmptyResponseContext;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Registry;
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
    
    public Content getContent(Artifact doc) {
        Content content = factory.newContent();

        content.setText("Hey its an artifact!");
        return content;
    }

    @Override
    public Text getSummary(Artifact entry) {
        Text summary = factory.newSummary();
        
        summary.setText("Version " + entry.getLatestVersion().getVersion());
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

    public Artifact createEntry(String title, 
                                String summary, 
                                Content content) throws ResponseContextException {
        throw new ResponseContextException(new EmptyResponseContext(500));
    }


    public Artifact createMediaEntry(MimeType mimeType, String slug, InputStream inputStream) throws ResponseContextException {
        try {
            return registry.createArtifact(workspace, mimeType.toString(), slug, inputStream);
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

    public Iterable<Artifact> getEntries() {
        try {
            return registry.getArtifacts(workspace);
        } catch (RegistryException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteEntry(String name) {
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
    

    public Artifact updateEntry(Artifact entry, Content content) {
        // TODO Auto-generated method stub
        return null;
    }

    public Artifact getEntryFromId(String id) {
        id = id.substring(ID_PREFIX.length());
        
        try {
            return registry.getArtifact(id);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Artifact getEntry(String name) {
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
