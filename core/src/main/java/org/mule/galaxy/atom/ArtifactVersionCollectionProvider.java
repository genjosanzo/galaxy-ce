package org.mule.galaxy.atom;


import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.activation.MimeType;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Content;
import org.apache.abdera.protocol.server.content.AbstractCollectionProvider;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Registry;

public class ArtifactVersionCollectionProvider extends AbstractCollectionProvider<ArtifactVersion> {
    
    private static final String ID_PREFIX = "urn:galaxy:document:version:";
    private Registry registry;
    private Factory factory = new Abdera().getFactory();
    private Artifact artifact;
    
    public ArtifactVersionCollectionProvider(Artifact document, Registry registry) {
        super();
        this.artifact = document;
    }

    public ArtifactVersion createEntry(String title, String summary, Content content) {
        throw new UnsupportedOperationException();
    }

    public ArtifactVersion createMediaEntry(MimeType mimeType, String slug, InputStream inputStream) {
        try {
            return registry.newVersion(artifact, inputStream, null);
        } catch (RegistryException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteEntry(String resourceName) {
        throw new UnsupportedOperationException();
    }

    public String getAuthor() {
        return "Mule Galaxy";
    }

    public Content getContent(ArtifactVersion doc) {
        Content content = factory.newContent();
        content.setSrc(doc.getVersionLabel());
        content.setMimeType(artifact.getContentType().toString());
        return content;
    }

    public String getContentType(ArtifactVersion entry) {
        return artifact.getContentType().toString();
    }

    public Iterable<ArtifactVersion> getEntries() {
        return artifact.getVersions();
    }

    public ArtifactVersion getEntry(String resourceName) {
        return artifact.getVersion(resourceName);
    }

    public ArtifactVersion getEntryFromId(String id) {
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

    public ArtifactVersion updateEntry(ArtifactVersion entry, Content content) {
        throw new UnsupportedOperationException();
    }
}
