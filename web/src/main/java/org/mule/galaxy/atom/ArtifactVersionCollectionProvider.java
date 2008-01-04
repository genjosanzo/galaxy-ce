package org.mule.galaxy.atom;


import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.RequestContext.Scope;
import org.apache.abdera.protocol.server.impl.ResponseContextException;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactPolicyException;
import org.mule.galaxy.ArtifactResult;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.security.User;

public class ArtifactVersionCollectionProvider extends AbstractArtifactVersionProvider {
    
    private static final String ARTIFACT_KEY = "artifact";
    
    public ArtifactVersionCollectionProvider(Registry registry) {
        super(registry);
        setBaseMediaIri("");
    }

    @Override
    public void begin(RequestContext request) throws ResponseContextException {
        String target = request.getTargetPath();
        int idx = target.indexOf("/registry/");
        int qIdx = target.lastIndexOf('?');
        if (qIdx == -1) {
            qIdx = target.length();
        }
        
        target = target.substring(idx + 10, qIdx);
        
        Artifact a = findArtifact(target);
        request.setAttribute(Scope.REQUEST, ARTIFACT_KEY, a);
        
        super.begin(request);
    }

    @Override
    public String getMediaName(ArtifactVersion version) {
        // TODO: Hmm this seems quite ugly. Need to revisit Abdera's code here.
        return new StringBuilder()
          .append("../")
          .append(UrlEncoding.encode(version.getParent().getName()))
          .append("?version=")
          .append(version.getVersionLabel()).toString();
    }

    @Override
    protected ArtifactResult createMediaEntry(String slug,
                                              MimeType mimeType, 
                                              String version,
                                              InputStream inputStream, 
                                              User user,
                                              RequestContext ctx)
        throws RegistryException, ArtifactPolicyException, IOException, MimeTypeParseException {
        
        return registry.newVersion(getArtifact(ctx), inputStream, version, user);
    }

    private Artifact getArtifact(RequestContext ctx) {
        return (Artifact) ctx.getAttribute(Scope.REQUEST, ARTIFACT_KEY);
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
    public ArtifactVersion getEntry(String name, RequestContext request) throws ResponseContextException {
        String versionLabel = request.getHeader("version");
        
        if (versionLabel == null || "".equals(versionLabel)) {
            return getArtifact(request).getActiveVersion();
        }
        
        return getArtifact(request).getVersion(versionLabel);
    }

    @Override
    public String getId() {
        return "tag:galaxy.mulesource.com,2008:registry:history:feed";
    }

    @Override
    public String getId(ArtifactVersion entry) throws ResponseContextException {
        return ID_PREFIX + entry.getParent().getId() + ":" + entry.getVersionLabel();
    }

    @Override
    public String getName(ArtifactVersion version) throws ResponseContextException {
        StringBuilder sb = getBasePath(version.getParent());
        
        sb.append("../")
          .append(UrlEncoding.encode(version.getParent().getName()))
          .append(".atom")
          .append("?version=")
          .append(version.getVersionLabel());
        
        return sb.toString();
    }

    @Override
    public void updateEntry(ArtifactVersion arg0, String arg1, Date arg2, List<Person> arg3, String arg4,
                            Content arg5, RequestContext arg6) throws ResponseContextException {
        throw new ResponseContextException(501);
    }

    public String getTitle(RequestContext request) {
        return getArtifact(request).getName() + " Revisions";
    }

    public String getTitle(ArtifactVersion entry) {
        return entry.getParent().getName() + " Version " + entry.getVersionLabel();
    }


}
