package org.mule.galaxy.atom;


import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.activation.MimeType;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.impl.AbstractCollectionProvider;
import org.apache.abdera.protocol.server.impl.ResponseContextException;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;

public class ArtifactVersionCollectionProvider extends AbstractCollectionProvider<ArtifactVersion> {
    
    private static final String ID_PREFIX = "urn:galaxy:document:version:";
    private Registry registry;
    private Factory factory = new Abdera().getFactory();
    private Artifact artifact;
    
    public ArtifactVersionCollectionProvider(Artifact document, Registry registry) {
        super();
        this.artifact = document;
    }

    @Override
    public ArtifactVersion createEntry(String arg0, IRI arg1, String arg2, Date arg3, List<Person> arg4,
                                       Content arg5, RequestContext request) throws ResponseContextException {
        throw new UnsupportedOperationException();
    }

    public ArtifactVersion createMediaEntry(MimeType mimeType, String slug, 
                                            InputStream inputStream, RequestContext request) {
        try {
            return registry.newVersion(artifact, inputStream, null, null);
        } catch (RegistryException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteEntry(String resourceName, RequestContext request) {
        throw new UnsupportedOperationException();
    }

    public String getAuthor() {
        return "Mule Galaxy";
    }

    public Content getContent(ArtifactVersion doc, RequestContext request) {
        Content content = factory.newContent();
        content.setSrc(doc.getVersionLabel());
        content.setMimeType(artifact.getContentType().toString());
        return content;
    }

    public String getContentType(ArtifactVersion entry) {
        return artifact.getContentType().toString();
    }

    public Iterable<ArtifactVersion> getEntries(RequestContext request) {
        return artifact.getVersions();
    }

    public ArtifactVersion getEntry(String resourceName, RequestContext request) {
        return artifact.getVersion(resourceName);
    }

    public ArtifactVersion getEntryFromId(String id, RequestContext request) {
        return artifact.getVersion(id);
    }

    public String getId() {
        return ID_PREFIX + artifact.getId();
    }

    public String getId(ArtifactVersion entry) {
        return ID_PREFIX + artifact.getId() + ":" + entry.getVersionLabel();
    }

    public InputStream getMediaStream(ArtifactVersion entry) {
        return entry.getStream();
    }

    public String getMediaName(ArtifactVersion entry) {
        return artifact.getName();
    }

    public String getName(ArtifactVersion entry) {
        return entry.getVersionLabel();
    }

    public String getTitle() {
        return artifact.getName() + " Revisions";
    }

    public String getTitle(ArtifactVersion entry) {
        return artifact.getName() + " Version " + entry.getVersionLabel();
    }

    @Override
    public boolean isMediaEntry(ArtifactVersion entry) {
        return true;
    }

    public Date getUpdated(ArtifactVersion entry) {
        return entry.getCreated().getTime();
    }

    @Override
    public List<Person> getAuthors(ArtifactVersion entry, RequestContext request)
        throws ResponseContextException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateEntry(ArtifactVersion entry, String title, Date updated, List<Person> authors,
                            String summary, Content content, RequestContext request)
        throws ResponseContextException {
        // TODO Auto-generated method stub
        
    }

}
