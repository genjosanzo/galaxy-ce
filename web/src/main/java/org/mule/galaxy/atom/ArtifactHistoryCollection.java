package org.mule.galaxy.atom;


import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.activation.MimeType;

import org.apache.abdera.model.Content;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.mule.galaxy.ArtifactResult;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Registry;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.security.User;

public class ArtifactHistoryCollection extends AbstractArtifactCollection {
    
    public ArtifactHistoryCollection(Registry registry, LifecycleManager lifecycleManager) {
        super(registry, lifecycleManager);
    }

    @Override
    public String getMediaName(ArtifactVersion version) {
        return super.getMediaName(version);
    }
    
    @Override
    public String getQueryParameters(ArtifactVersion version, RequestContext request) {
        return "version=" + version.getVersionLabel();
    }

    @Override
    protected ArtifactResult postMediaEntry(String slug,
                                              MimeType mimeType, 
                                              String version,
                                              InputStream inputStream, 
                                              User user,
                                              RequestContext ctx)
        throws ResponseContextException {
        throw new ResponseContextException(501);
    }
    
    @Override
    public void deleteEntry(String entry, RequestContext arg1) throws ResponseContextException {
        throw new ResponseContextException(501);
    }

    @Override
    public Iterable<ArtifactVersion> getEntries(RequestContext request) throws ResponseContextException {
        return getArtifact(request).getVersions();
    }

    @Override
    public String getId(RequestContext request) {
        return "tag:galaxy.mulesource.com,2008:registry:" + registry.getUUID() + ":history:feed";
    }

    @Override
    public String getId(ArtifactVersion entry) throws ResponseContextException {
        return ID_PREFIX + entry.getParent().getId() + ":" + entry.getVersionLabel();
    }

    @Override
    public String getName(ArtifactVersion version) {
        return super.getName(version);
    }

    public String getTitle(RequestContext request) {
        return getArtifact(request).getName() + " Revisions";
    }

    public String getTitle(ArtifactVersion entry) {
        return entry.getParent().getName() + " Version " + entry.getVersionLabel();
    }


}
