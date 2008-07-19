/*
 * $Id: ContextPathResolver.java 794 2008-04-23 22:23:10Z andrew $
 * --------------------------------------------------------------------------------------
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.mule.galaxy.atom;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Text;
import org.apache.abdera.model.Text.Type;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.RequestContext.Scope;
import org.apache.abdera.protocol.server.context.EmptyResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.context.SimpleResponseContext;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Item;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.jcr.UserDetailsWrapper;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.lifecycle.TransitionException;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;

public abstract class AbstractArtifactCollection 
    extends AbstractEntityCollectionAdapter<ArtifactVersion> {
    public static final String NAMESPACE = "http://galaxy.mule.org/1.0";
    public static final String ID_PREFIX = "urn:galaxy:artifact:";
    private final Log log = LogFactory.getLog(getClass());

    protected Factory factory = Abdera.getInstance().getFactory();
    protected Registry registry;
    
    public AbstractArtifactCollection(Registry registry) {
        super();
        this.registry = registry;
    }

    public Content getContent(ArtifactVersion doc, RequestContext request) {
        // Not used since these are media entries
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected String addEntryDetails(RequestContext request, 
                                     Entry e, 
                                     IRI feedIri, 
                                     ArtifactVersion entryObj)
        throws ResponseContextException {
        String link = super.addEntryDetails(request, e, feedIri, entryObj);

        Artifact artifact = (Artifact)entryObj.getParent();

        Element info = factory.newElement(new QName(NAMESPACE, "artifact-info"));
        info.setAttributeValue("mediaType", artifact.getContentType().toString());
        e.addExtension(info);
        
        if (artifact.getDocumentType() != null) {
            info.setAttributeValue("documentType", artifact.getDocumentType().toString());
        }
        
        Element metadata = factory.newElement(new QName(NAMESPACE, "metadata"));
        
        boolean showHidden = BooleanUtils.toBoolean(request.getParameter("showHiddenProperties"));
       
        for (Iterator<PropertyInfo> props = entryObj.getProperties(); props.hasNext();) {
            PropertyInfo p = props.next();
            if (p.isVisible() || showHidden) {
                Element prop = factory.newElement(new QName(NAMESPACE, "property"), metadata);
                prop.setAttributeValue("name", p.getName());
                prop.setAttributeValue("locked", new Boolean(p.isLocked()).toString());
                
                if (p.isVisible()) {
                    prop.setAttributeValue("visible", new Boolean(p.isVisible()).toString());
                }
                
                Object value = p.getValue();
                if (value == null) {
                    value = "";
                }
                
                if (value instanceof Collection) {
                    for (Object o : ((Collection) value)) {
                        Element valueEl = factory.newElement(new QName(NAMESPACE, "value"), prop);
                        
                        valueEl.setText(o.toString());
                    }
                } else {
                    prop.setAttributeValue("value", value.toString());
                }
            }
        }
        
        e.addExtension(metadata);
        
        Element lifecycle = factory.newElement(new QName(NAMESPACE, "lifecycle"));
        Phase phase = entryObj.getPhase();
        lifecycle.setAttributeValue("name", phase.getLifecycle().getName());
        lifecycle.setAttributeValue("phase", phase.getName());
        
        e.addExtension(lifecycle);
        
        buildAvailablePhases(phase, phase.getNextPhases(), "next-phases", lifecycle);
        buildAvailablePhases(phase, phase.getPreviousPhases(), "previous-phases", lifecycle);
        
        Element version = factory.newElement(new QName(NAMESPACE, "version"));
        version.setAttributeValue("label", entryObj.getVersionLabel());
        version.setAttributeValue("enabled", (String) new Boolean(entryObj.isEnabled()).toString());
        version.setAttributeValue("default",(String) new Boolean(entryObj.isDefault()).toString());
        
        e.addExtension(version);
        
        return link;
    }

    private Element buildAvailablePhases(Phase phase, Set<Phase> phases, String name, Element lifecycle) {
        Element availPhases = factory.newElement(new QName(NAMESPACE, name), lifecycle);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Phase p : phases) {
            if (!first) {
                sb.append(", ");
            } else {
                first = false;
            }
            
            sb.append(p.getName());
        }
        availPhases.setText(sb.toString());
        return availPhases;
    }

    @Override
    public Text getSummary(ArtifactVersion entry, RequestContext request) {
        Text summary = factory.newSummary();
        
        String d = ((Artifact)entry.getParent()).getDescription();
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
        return ((Artifact)entry.getParent()).getContentType().toString();
    }

    public String getAuthor(RequestContext request) {
        return "Mule Galaxy";
    }

    protected StringBuilder getBasePath(Artifact a) {
        StringBuilder sb = new StringBuilder();
        
        Workspace w = (Workspace) a.getParent();
        while (w != null) {
            sb.insert(0, '/');
            sb.insert(0, UrlEncoding.encode(w.getName(), Profile.PATH.filter()));
            w = (Workspace) w.getParent();
        }
        return sb;
    }
    public Date getUpdated(ArtifactVersion doc) {
        return doc.getUpdated().getTime();
    }

    @Override
    public ArtifactVersion postEntry(String arg0, IRI arg1, String arg2, Date arg3, List<Person> arg4, Content arg5, RequestContext request)
        throws ResponseContextException {
        throw new ResponseContextException(new EmptyResponseContext(500));
    }

    @Override
    public ArtifactVersion postMedia(MimeType mimeType, 
                                     String slug, 
                                     InputStream inputStream,
                                     RequestContext request) throws ResponseContextException {
        try {
            String version = getVersion(request);
            
            User user = getUser();
            
            EntryResult result = postMediaEntry(slug, mimeType, version, inputStream, user, request);
            
            return (ArtifactVersion) result.getEntryVersion();
        } catch (DuplicateItemException e) {
            throw newErrorMessage("Duplicate artifact.", "An artifact with that name already exists in this workspace.", 409);
        } catch (RegistryException e) {
            log.error("Could not add artifact.", e);
            throw new ResponseContextException(500, e);
        } catch (IOException e) {
            throw new ResponseContextException(500, e);
        } catch (MimeTypeParseException e) {
            throw new ResponseContextException(500, e);
        } catch (PolicyException e) {
            throw createArtifactPolicyExceptionResponse(e);
        } catch (AccessException e) {
            throw new ResponseContextException(401, e);
        }
    }

    protected ResponseContextException createArtifactPolicyExceptionResponse(PolicyException e) {
        final StringBuilder s = new StringBuilder();
        s.append("<html><head><title>Artifact Policy Failure</title></head><body>");
        
        List<ApprovalMessage> approvals = e.getApprovals();
        
        for (ApprovalMessage m : approvals) {
            if (m.isWarning()) {
                s.append("<div class=\"warning\">");
            } else {
                s.append("<div class=\"failure\">");
            }
            
            s.append(m.getMessage());
            s.append("</div>");
        }
        
        s.append("</body></html>");
        SimpleResponseContext rc = new SimpleResponseContext() {
            @Override
            protected void writeEntity(Writer writer) throws IOException {
                writer.write(s.toString());
                writer.flush();
            }

            public boolean hasEntity() {
                return true;
            }
        };
        rc.setContentType("application/xhtml");
        // bad request code
        rc.setStatus(400);
        
        return new ResponseContextException(rc);
    }

    protected User getUser() {
        UserDetailsWrapper wrapper = 
            (UserDetailsWrapper) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = wrapper.getUser();
        return user;
    }

    protected String getVersion(RequestContext request) throws ResponseContextException {
        String version = request.getHeader("X-Artifact-Version");
        
        if (version == null || version.equals("")) {
            EmptyResponseContext ctx = new EmptyResponseContext(500);
            ctx.setStatusText("You must supply an X-Artifact-Version header!");
            
            throw new ResponseContextException(ctx);
        }
        return version;
    }

    protected abstract EntryResult postMediaEntry(String slug, MimeType mimeType, String version,
                                                     InputStream inputStream, User user, RequestContext ctx)
        throws RegistryException, PolicyException, IOException, MimeTypeParseException, ResponseContextException, DuplicateItemException, AccessException;
    
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
    public String getMediaName(ArtifactVersion version) {
        return UrlEncoding.encode(version.getParent().getName());
    }

    @Override
    public String getHref(RequestContext request) {
        String href = (String) request.getAttribute(Scope.REQUEST, ArtifactResolver.COLLECTION_HREF);
        if (href == null) {
            // this is the url we use when pulling down the services document
            href = request.getTargetBasePath() + "/registry";
        }
        return href;
    }

    public String getId(ArtifactVersion doc) {
        return ID_PREFIX + doc.getParent().getId();
    }

    @Override
    protected String getFeedIriForEntry(ArtifactVersion entryObj, RequestContext request) {
        Artifact a = (Artifact)entryObj.getParent();
        
        return request.getTargetBasePath() 
               + "/registry" 
               + UrlEncoding.encode(a.getParent().getPath(), Profile.PATH.filter());
    }

    @Override
    public String getName(ArtifactVersion doc) {
        return getNameOfArtifact(doc) + ";atom";
    }
    public String getNameOfArtifact(ArtifactVersion doc) {
        return UrlEncoding.encode(doc.getParent().getName(), Profile.PATH.filter());
    }
    
    public ArtifactVersion getEntry(String name, RequestContext request) throws ResponseContextException {
        Artifact a = getArtifact(request);
        return selectVersion(a, request.getParameter("version"));
    }

    protected ArtifactVersion selectVersion(Artifact next, String version) throws ResponseContextException {
        if (version != null) {
            EntryVersion v = next.getVersion(version);
            
            if (v == null || "".equals(version)) {
                EmptyResponseContext res = new EmptyResponseContext(404);
                res.setStatusText("Version " + version + " was not found.");
                throw new ResponseContextException(res);
            }
            
            return (ArtifactVersion) v;
        }
        return (ArtifactVersion) next.getDefaultOrLastVersion();
    }

    protected Artifact getArtifact(RequestContext request) {
        return (Artifact) request.getAttribute(Scope.REQUEST, ArtifactResolver.ARTIFACT);
    }

    @Override
    public void putEntry(ArtifactVersion av, 
                         String title, 
                         Date updated, 
                         List<Person> authors, 
                         String summary,
                         Content content, 
                         RequestContext request) throws ResponseContextException {
        Artifact artifact = (Artifact) av.getParent();
        artifact.setDescription(summary);
//        artifact.setName(title);
        
        try {
            Document<Entry> entryDoc = request.getDocument();
            Entry entry = entryDoc.getRoot();
            
            for (Element e : entry.getElements()) {
                QName q = e.getQName();
                if (NAMESPACE.equals(q.getNamespaceURI())) {
                    if ("lifecycle".equals(q.getLocalPart())) {
                        updateLifecycle(av, e);
                    } else if ("metadata".equals(q.getLocalPart())) {
                        updateMetadata(av, e);
                    } else if ("version".equals(q.getLocalPart())) {
                        updateVersion(av, e);
                    }
                }
            }
        } catch (ParseException e) {
            throw new ResponseContextException(500, e);
        } catch (IOException e) {
            throw new ResponseContextException(500, e);
        } catch (RegistryException e) {
            throw new ResponseContextException(500, e);
        } catch (PolicyException e) {
            throw createArtifactPolicyExceptionResponse(e);
        }
    }

    private void updateVersion(ArtifactVersion av, Element e) 
        throws RegistryException, PolicyException, ResponseContextException {
        String label = e.getAttributeValue("label");
        
        if (label != null && !av.getVersionLabel().equals(label)) {
            // TODO: provide way to rename versions
        }
        
        String def = e.getAttributeValue("default");
        if (def != null) {
            boolean defBool = BooleanUtils.toBoolean(def);
            
            if (defBool != av.isDefault()) 
            {
                if (defBool) {
                    av.setAsDefaultVersion();
                } else {
                    throwMalformed("You can only set an artifact default version to true!");
                }
            }
        }
        
        String enabled = e.getAttributeValue("enabled");
        if (enabled != null) {
            boolean enabledBool = BooleanUtils.toBoolean(enabled);
            
            if (enabledBool != av.isEnabled()) {
                av.setEnabled(enabledBool);
            }
        }
    }

    private void updateLifecycle(ArtifactVersion av, Element e) throws ResponseContextException {
        String name = e.getAttributeValue("name");
        assertNotEmpty(name, "Lifecycle name attribute cannot be null.");
        
        String phaseName = e.getAttributeValue("phase");
        assertNotEmpty(phaseName, "Lifecycle phase attribute cannot be null.");
        
        Phase current = av.getPhase();
        if (name.equals(current.getLifecycle().getName()) 
            && phaseName.equals(current.getName())) {
            return;
        }
            
        Workspace w = (Workspace) av.getParent().getParent();
        LifecycleManager lifecycleManager = w.getLifecycleManager();
        Lifecycle lifecycle = lifecycleManager.getLifecycle(name);
        
        if (lifecycle == null)
            throwMalformed("Lifecycle \"" + name + "\" does not exist.");
        
        Phase phase = lifecycle.getPhase(phaseName);

        if (phase == null)
            throwMalformed("Lifecycle phase \"" + phaseName + "\" does not exist.");
        
        try {
            lifecycleManager.transition(av, phase, getUser());
        } catch (TransitionException e1) {
            throwMalformed(e1.getMessage());
        } catch (PolicyException e1) {
            throw createArtifactPolicyExceptionResponse(e1);
        }
    }

    protected void assertNotEmpty(String name, String message) throws ResponseContextException {
        if (name == null || "".equals(name)) {
            throwMalformed(message);
        }
    }

    protected void throwMalformed(final String message) throws ResponseContextException {
        throw newErrorMessage("Malformed Atom Entry", message, 400);
    }

    protected ResponseContextException newErrorMessage(final String title,
                                   final String message, int status) throws ResponseContextException {
        SimpleResponseContext rc = new SimpleResponseContext() {

            @Override
            protected void writeEntity(Writer writer) throws IOException {
                writer.write("<html><head><title>)");
                writer.write(title);
                writer.write("</title></head><body><div class=\"error\">");
                writer.write(message);
                writer.write("</div></body></html>");
            }

            public boolean hasEntity() {
                return true;
            }
        };
        
        rc.setStatus(status);
        
        return new ResponseContextException(rc);
    }

    private void updateMetadata(ArtifactVersion av, Element e) throws ResponseContextException {
        for (Element propEl : e.getElements()) {
            String name = propEl.getAttributeValue("name");
            if (name == null)
                throwMalformed("You must specify name attributes on metadata properties.");
            
            String value = propEl.getAttributeValue("value");
            String visible = propEl.getAttributeValue("visible");
            if (value != null) {
                try {
                    av.setProperty(name, value);
                } catch (PropertyException e1) {
                    // Ignore as its probably because its locked
                }
            } else {
                List<Element> elements = propEl.getElements();
                ArrayList<String> values = new ArrayList<String>();
                for (Element valueEl : elements) {
                    if (valueEl.getQName().getLocalPart().equals("value")) {
                        values.add(valueEl.getText().trim());
                    }
                }
                try {
                    av.setProperty(name, values);
                } catch (PropertyException e1) {
                    // Ignore as its probably because its locked
                }
            }
            
            if (visible != null) {
                av.setVisible(name, BooleanUtils.toBoolean(visible));
            }
        }
    }


}
