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
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.i18n.text.CharUtils.Profile;
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
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactPolicyException;
import org.mule.galaxy.ArtifactResult;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.jcr.UserDetailsWrapper;
import org.mule.galaxy.security.User;

public abstract class AbstractArtifactVersionProvider extends AbstractCollectionProvider<ArtifactVersion> {
    public static final String NAMESPACE = "http://galaxy.mule.org/1.0";
    public static final String ID_PREFIX = "urn:galaxy:artifact:";

    protected Factory factory = new Abdera().getFactory();
    protected Registry registry;
    
    public AbstractArtifactVersionProvider(Registry registry) {
        super();
        this.registry = registry;
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

        Element metadata = factory.newElement(new QName(NAMESPACE, "metadata"));
        
        for (Iterator<PropertyInfo> props = entryObj.getProperties(); props.hasNext();) {
            PropertyInfo p = props.next();
            if (p.isVisible()) {
                Element prop = factory.newElement(new QName(NAMESPACE, "property"), metadata);
                prop.setAttributeValue("name", p.getName());
                prop.setAttributeValue("locked", new Boolean(p.isLocked()).toString());
                Object value = p.getValue();
                if (value == null) {
                    value = "";
                } 
                prop.setAttributeValue("value", value.toString());
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

    protected StringBuilder getBasePath(Artifact a) {
        StringBuilder sb = new StringBuilder();
        
        Workspace w = a.getWorkspace();
        while (w != null) {
            sb.insert(0, '/');
            sb.insert(0, UrlEncoding.encode(w.getName(), Profile.PATH.filter()));
            w = w.getParent();
        }
        return sb;
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
            User user = wrapper.getUser();
            
            ArtifactResult result = createMediaEntry(slug, mimeType, version, inputStream, user, request);
            
            return result.getArtifactVersion();
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

    protected abstract ArtifactResult createMediaEntry(String slug, MimeType mimeType, String version,
                                                       InputStream inputStream, User user, RequestContext ctx)
        throws RegistryException, ArtifactPolicyException, IOException, MimeTypeParseException, ResponseContextException;
    
    @Override
    public boolean isMediaEntry(ArtifactVersion entry) {
        return true;
    }

    @Override
    public List<Person> getAuthors(ArtifactVersion entry, RequestContext request) throws ResponseContextException {
        Person author = request.getAbdera().getFactory().newAuthor();
        author.setName("Galaxy");
        return Arrays.asList(author);
    }
    
    @Override
    public String getMediaName(ArtifactVersion entry) {
        Artifact a = entry.getParent();
        StringBuilder path = getBasePath(a);
        
        path.append(entry.getParent().getName());
        return path.toString();
    }

    protected Artifact findArtifact(String name) throws ResponseContextException {
        String[] paths = name.split("/");
        
        Workspace w = null;
        for (int i = 0; i < paths.length-1; i++) {
            try {
                w = registry.getWorkspaceByPath(UrlEncoding.decode(paths[i]));
            } catch (NotFoundException e) {
                throw new ResponseContextException(404);
            } catch (RegistryException e) {
                throw new ResponseContextException(500, e);
            }
        }
        Artifact a = null;
        try {
            a = registry.getArtifact(w, UrlEncoding.decode(paths[paths.length-1]));
            
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        return a;
    }

}
