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

import static org.mule.galaxy.util.AbderaUtils.newErrorMessage;
import static org.mule.galaxy.util.AbderaUtils.throwMalformed;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.extension.AtomExtension;
import org.mule.galaxy.extension.Extension;
import org.mule.galaxy.impl.jcr.UserDetailsWrapper;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.util.AbderaUtils;
import org.mule.galaxy.util.SecurityUtils;

public abstract class AbstractEntryCollection 
    extends AbstractEntityCollectionAdapter<EntryVersion> {
    public static final String NAMESPACE = "http://galaxy.mule.org/1.0";
    public static final String ID_PREFIX = "urn:galaxy:artifact:";    
    public static final QName VERSION_QNAME = new QName(NAMESPACE, "version");

    protected final Log log = LogFactory.getLog(getClass());

    protected Factory factory = Abdera.getInstance().getFactory();
    protected Registry registry;
    
    public AbstractEntryCollection(Registry registry) {
        super();
        this.registry = registry;
    }

    public Content getContent(EntryVersion doc, RequestContext request) {
        Content content = request.getAbdera().getFactory().newContent();
//        content.setContentType(org.apache.abdera.model.Content.Type.XHTML);
        content.setText(((org.mule.galaxy.Entry)doc.getParent()).getDescription());
        return content;
    }
    
    @Override
    protected String addEntryDetails(RequestContext request, 
                                     Entry atomEntry, 
                                     IRI feedIri, 
                                     EntryVersion entryObj)
        throws ResponseContextException {
        String link = super.addEntryDetails(request, atomEntry, feedIri, entryObj);

        org.mule.galaxy.Entry regEntry = (org.mule.galaxy.Entry)entryObj.getParent();

        if (regEntry instanceof Artifact) {
            Artifact artifact = (Artifact) regEntry;
            Element info = factory.newElement(new QName(NAMESPACE, "artifact-info"));
            info.setAttributeValue("mediaType", artifact.getContentType().toString());
            atomEntry.addExtension(info);
            
            if (artifact.getDocumentType() != null) {
                info.setAttributeValue("documentType", artifact.getDocumentType().toString());
            }
        }
        
        Element metadata = factory.newElement(new QName(NAMESPACE, "metadata"));
        
        boolean showHidden = BooleanUtils.toBoolean(request.getParameter("showHiddenProperties"));
       
        for (PropertyInfo p : entryObj.getProperties()) {
            PropertyDescriptor pd = p.getPropertyDescriptor();
            
            if (p.isVisible() || showHidden) {
                if (pd != null && pd.getExtension() instanceof AtomExtension) {
                    ((AtomExtension) pd.getExtension()).annotateAtomEntry(entryObj, pd, atomEntry, factory);
                } else {
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
        }
        
        atomEntry.addExtension(metadata);
        
        Element version = factory.newElement(new QName(NAMESPACE, "version"));
        version.setAttributeValue("label", entryObj.getVersionLabel());
        version.setAttributeValue("enabled", (String) new Boolean(entryObj.isEnabled()).toString());
        version.setAttributeValue("default",(String) new Boolean(entryObj.isDefault()).toString());
        
        atomEntry.addExtension(version);
        
        return link;
    }

    @Override
    public Text getSummary(EntryVersion entry, RequestContext request) {
        if (entry instanceof ArtifactVersion) {
            Text summary = factory.newSummary();
            
            String d = ((Artifact)entry.getParent()).getDescription();
            if (d == null) d = "";
            
            summary.setText(d);
            summary.setTextType(Type.XHTML);
            
            return summary;
        }
        return null;
    }

    public InputStream getMediaStream(EntryVersion entry) throws ResponseContextException {
        if (entry instanceof ArtifactVersion) {
            return ((ArtifactVersion)entry).getStream();
        }
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String getContentType(EntryVersion entry) {
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
    public Date getUpdated(EntryVersion doc) {
        return doc.getUpdated().getTime();
    }

    @Override
    public EntryVersion postMedia(MimeType mimeType, 
                                     String slug, 
                                     InputStream inputStream,
                                     RequestContext request) throws ResponseContextException {
        try {
            String version = getVersion(request);
            
            User user = getUser();
            
            EntryResult result = postMediaEntry(slug, mimeType, version, inputStream, user, request);
            
            return (EntryVersion) result.getEntryVersion();
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
            throw AbderaUtils.createArtifactPolicyExceptionResponse(e);
        } catch (AccessException e) {
            throw new ResponseContextException(401, e);
        }
    }
    
    protected User getUser() {
        return SecurityUtils.getCurrentUser();
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
    public boolean isMediaEntry(EntryVersion entry) {
        return entry instanceof ArtifactVersion;
    }

    @Override
    public List<Person> getAuthors(EntryVersion entry, RequestContext request) throws ResponseContextException {
        Person author = request.getAbdera().getFactory().newAuthor();
        author.setName("Galaxy");
        return Arrays.asList(author);
    }
    
    @Override
    public String getMediaName(EntryVersion version) {
        return UrlEncoding.encode(version.getParent().getName());
    }

    @Override
    public String getHref(RequestContext request) {
        String href = (String) request.getAttribute(Scope.REQUEST, EntryResolver.COLLECTION_HREF);
        if (href == null) {
            // this is the url we use when pulling down the services document
            href = request.getTargetBasePath() + "/registry";
        }
        return href;
    }

    public String getId(EntryVersion doc) {
        return ID_PREFIX + doc.getParent().getId();
    }

    @Override
    protected String getFeedIriForEntry(EntryVersion entryObj, RequestContext request) {
        org.mule.galaxy.Entry a = (org.mule.galaxy.Entry)entryObj.getParent();
        
        return request.getTargetBasePath() 
               + "/registry" 
               + UrlEncoding.encode(a.getParent().getPath(), Profile.PATH.filter());
    }

    @Override
    public String getName(EntryVersion doc) {
        return getNameOfArtifact(doc) + ";atom";
    }
    public String getNameOfArtifact(EntryVersion doc) {
        return UrlEncoding.encode(doc.getParent().getName(), Profile.PATH.filter());
    }
    
    public EntryVersion getEntry(String name, RequestContext request) throws ResponseContextException {
        org.mule.galaxy.Entry a = getRegistryEntry(request);
        return selectVersion(a, request.getParameter("version"));
    }

    protected EntryVersion selectVersion(org.mule.galaxy.Entry next, String version) throws ResponseContextException {
        if (version != null) {
            EntryVersion v = next.getVersion(version);
            
            if (v == null || "".equals(version)) {
                EmptyResponseContext res = new EmptyResponseContext(404);
                res.setStatusText("Version " + version + " was not found.");
                throw new ResponseContextException(res);
            }
            
            return (EntryVersion) v;
        }
        return (EntryVersion) next.getDefaultOrLastVersion();
    }

    protected org.mule.galaxy.Entry getRegistryEntry(RequestContext request) {
        return (org.mule.galaxy.Entry) request.getAttribute(Scope.REQUEST, EntryResolver.ENTRY);
    }
    
    protected String getVersionLabel(Entry atomEntry) throws ResponseContextException {
        Element version = atomEntry.getExtension(VERSION_QNAME);
        
        if (version == null || version.getAttributeValue("label") == null) {
            throw newErrorMessage("Invalid version label", "A version element must be specified with a label attribute.", 500);
        }
        
        return version.getAttributeValue("label");
    }


    @Override
    public void putEntry(EntryVersion av, 
                         String title, 
                         Date updated, 
                         List<Person> authors, 
                         String summary,
                         Content content, 
                         RequestContext request) throws ResponseContextException {
        org.mule.galaxy.Entry artifact = (org.mule.galaxy.Entry) av.getParent();
        artifact.setDescription(summary);
//        artifact.setName(title);
        
        try {
            Document<Entry> entryDoc = request.getDocument();
            Entry atomEntry = entryDoc.getRoot();
            
            mapEntryExtensions(av, atomEntry);
        } catch (ParseException e) {
            throw new ResponseContextException(500, e);
        } catch (IOException e) {
            throw new ResponseContextException(500, e);
        } catch (RegistryException e) {
            throw new ResponseContextException(500, e);
        } catch (PolicyException e) {
            throw AbderaUtils.createArtifactPolicyExceptionResponse(e);
        }
    }

    protected void mapEntryExtensions(EntryVersion av, Entry entry) throws ResponseContextException,
        PolicyException, RegistryException {
        for (Element e : entry.getElements()) {
            QName q = e.getQName();
            
            AtomExtension atomExt = getExtension(q);
            if (atomExt != null) {
                atomExt.updateItem(av, factory, e);
            } else if (NAMESPACE.equals(q.getNamespaceURI())) {
                if ("metadata".equals(q.getLocalPart())) {
                    updateMetadata(av, e);
                } else if ("version".equals(q.getLocalPart())) {
                    updateVersion(av, e);
                }
            }
        }
    }

    private AtomExtension getExtension(QName q) {
        for (Extension e : registry.getExtensions()) {
            if (e instanceof AtomExtension && ((AtomExtension) e).getUnderstoodElements().contains(q)) {
                return (AtomExtension) e;
            }
        }
        return null;
    }

    private void updateVersion(EntryVersion av, Element e) 
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
    private void updateMetadata(EntryVersion av, Element e) throws ResponseContextException, PolicyException {
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
