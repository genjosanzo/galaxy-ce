package org.mule.galaxy.impl.artifact;

import static org.mule.galaxy.event.DefaultEvents.ITEM_DELETED;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.mule.galaxy.Item;
import org.mule.galaxy.Link;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.artifact.Artifact;
import org.mule.galaxy.artifact.ArtifactType;
import org.mule.galaxy.artifact.ArtifactTypeDao;
import org.mule.galaxy.artifact.ContentHandler;
import org.mule.galaxy.artifact.ContentService;
import org.mule.galaxy.artifact.XmlContentHandler;
import org.mule.galaxy.event.ItemDeletedEvent;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;
import org.mule.galaxy.extension.AtomExtension;
import org.mule.galaxy.impl.extension.AbstractExtension;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.impl.link.LinkExtension;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.util.BundleUtils;
import org.mule.galaxy.util.Constants;
import org.mule.galaxy.util.GalaxyUtils;
import org.mule.galaxy.util.Message;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

@BindToEvent(ITEM_DELETED)
public class ArtifactExtension extends AbstractExtension implements AtomExtension {
    private static final QName ARTIFACT_QNAME = new QName(Constants.ATOM_NAMESPACE, "artifact");
    private static final Collection<QName> UNDERSTOOD = new ArrayList<QName>();

    static {
        UNDERSTOOD.add(ARTIFACT_QNAME);
    }

    public static final String ID = "artifactExtension";
    public static final String ITEM_ID = "item";
    
    private JcrTemplate template;
    private String artifactsNodeId;
    private ContentService contentService;
    private ArtifactTypeDao artifactTypeDao;
    private Registry registry;
    
    public void initialize() throws Exception {
        JcrUtil.doInTransaction(template.getSessionFactory(), new JcrCallback() {

            public Object doInJcr(Session session) throws IOException,
                    RepositoryException {
                artifactsNodeId = JcrUtil.getOrCreate(session.getRootNode(), "artifacts").getUUID();
                session.save();
                return null;
            }
            
        });
    }
    
    /**
     * In previous versions we didn't properly clean up artifacts. This method will clean up those old ones
     * on startup.
     */
    public void cleanupOldArtifacts() throws IOException, RepositoryException {
        JcrUtil.doInTransaction(template.getSessionFactory(), new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
                for (NodeIterator artifacts = session.getNodeByUUID(artifactsNodeId).getNodes(); artifacts.hasNext();) {
                    Node artifact = artifacts.nextNode();
                    QueryManager qm = session.getWorkspace().getQueryManager();
                    QueryResult result = qm.createQuery("//element(*, galaxy:item)[@artifact = '" + artifact.getUUID() + "']", Query.XPATH).execute();
                  
                    if (result.getNodes().getSize() == 0) {
                        artifact.remove();
                    }
                }
                return null;
            }
        });
    }

    @OnEvent
    public void onItemDeleted(final ItemDeletedEvent e) {
        template.execute(new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
                QueryManager qm = session.getWorkspace().getQueryManager();
                QueryResult result = qm.createQuery("/jcr:root/artifacts/*[item='" + e.getItemId() + "']", Query.XPATH).execute();
                
                for (NodeIterator nodes = result.getNodes(); nodes.hasNext();) {
                    Node artifact = nodes.nextNode();
                    
                    artifact.remove();
                }
                session.save();
                return null;
            }
            
        });
    }

    /**
     * Count the number of artifacts in the system. Used for testing to ensure deletes happen properly.
     * @return
     */
    public int getArtifactCount() {
        return (Integer) template.execute(new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
                return new Long(session.getNodeByUUID(artifactsNodeId).getNodes().getSize()).intValue();
            }
            
        });
    }
    
    public Object get(Item item, PropertyDescriptor pd, boolean getWithNoData) {
        Object storedValue = item.getInternalProperty(pd.getProperty());

        if (storedValue == null) {
            return null;
        }

        return getArtifact(item, (String)storedValue);
    }

    public void store(final Item item, final PropertyDescriptor pd, Object value)
            throws PolicyException, PropertyException, AccessException {
        if (value == null) {
            item.setInternalProperty(pd.getProperty(), null);
        } else if (value instanceof Object[]) {
            Object[] values = (Object[]) value;
            if (values.length != 2) {
                throw new PropertyException(new Message("INVALID_VALUES", BundleUtils.getBundle(ArtifactExtension.class)));
            }
            final InputStream is = (InputStream) values[0];
            final String ct = (String) values[1];
            
            store(item, pd, is, ct);
        } else if (value instanceof Artifact) {
            
            try {
                final InputStream is = ((Artifact) value).getInputStream();
                final String ct = ((Artifact) value).getContentType().toString();
                
                store(item, pd, is, ct);
            } catch (IOException e) {
                throw new PropertyException(e);
            }
        } else {
            throw new UnsupportedOperationException("Values of type " + value.getClass() + " can not be stored as files.");
        }
    }

    public void store(final Item item, final PropertyDescriptor pd, final InputStream is, final String ct)
            throws PropertyException, PolicyException {
        String id = (String) template.execute(new JcrCallback() {

            public Object doInJcr(Session session) throws IOException,
                    RepositoryException {
                return persistArtifact(item, pd, is, ct, session);
            }
        });
        
        item.setInternalProperty(pd.getProperty(), id);
    }

    private Artifact getArtifact(final Item item, final String storedValue) {
        return (Artifact) template.execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException,
                    RepositoryException {
                Node node = session.getNodeByUUID(storedValue);
                return new ArtifactImpl(item, node, contentService);
            }
        });
    }

    protected String persistArtifact(final Item item,
                                     final PropertyDescriptor pd,
                                     final InputStream is,
                                     String contentType, 
                                     Session session)
            throws RepositoryException {
        String nodeId = item.getInternalProperty(pd.getProperty());
        Node node;
        if (nodeId == null) {
            Node artifacts = session.getNodeByUUID(artifactsNodeId);
            node = artifacts.addNode(UUID.randomUUID().toString());
            node.addMixin("mix:referenceable");
        } else {
            node = session.getNodeByUUID(nodeId);
        }

        contentType = trimContentType(contentType);

        // Deal with the case where we don't have a good mime type
        if ("application/octet-stream".equals(contentType)) {
            String ext = getExtension(item.getParent().getName());
            ArtifactType type = artifactTypeDao.getArtifactType(ext);

            if (type == null && "xml".equals(ext)) {
                contentType = "application/xml";
            } else if (type != null) {
                contentType = type.getContentType();
            }
        }
        
        node.setProperty(ITEM_ID, item.getId());
        node.setProperty(ArtifactImpl.CONTENT_TYPE, contentType);
        
        createContentNode(node, is, contentType);
        
        ArtifactImpl artifact = new ArtifactImpl(item, node, contentService);

        ContentHandler ch = artifact.getContentHandler();
        try {
            Object loadedData = artifact.getData();
            if (ch instanceof XmlContentHandler) {
                XmlContentHandler xch = (XmlContentHandler) ch;
                node.setProperty(ArtifactImpl.DOCUMENT_TYPE, xch.getDocumentType(loadedData).toString());
                ch = contentService.getContentHandler(artifact.getDocumentType());
            }
            
            Item container = item.getParent().getParent();
            Set<String> dependencies = ch.detectDependencies(loadedData, container);
            Set<Link> links = new HashSet<Link>();
            for (String p : dependencies) {
                Item resolvedItem = registry.resolve(container, p);
                
                links.add(new Link(item, resolvedItem, p, true));
            }
            
            if (links.size() > 0) {
                item.setProperty(LinkExtension.DEPENDS, links);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (PropertyException e) {
            throw new RuntimeException(e);
        } catch (AccessException e) {
            throw new RuntimeException(e);
        } catch (PolicyException e) {
            throw new RuntimeException(e);
        } catch (RegistryException e) {
            throw new RuntimeException(e);
        }

        return node.getUUID();
    }

    public List<String> getArtifactsForContentType(final String propertyId,
                                                   final boolean like,
                                                   final Object value) {
        return (List<String>) template.execute(new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
                return getArtifacts(session, propertyId, like, "contentType", value);
            }
            
        });
    }

    public List<String> getArtifactsForDocumentType(final String propertyId,
                                                    final boolean like,
                                                    final Object value) {
        return (List<String>) template.execute(new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {
                return getArtifacts(session, propertyId, like, "documentType", value);
            }
            
        });
    }
    
    public List<String> getArtifacts(Session session, 
                                     final String propertyId,
                                     final boolean like,
                                     final String searchField,
                                     final Object value) throws RepositoryException {
        List<String> ids = new ArrayList<String>();

        if (value instanceof Collection) {
            for (Object o : ((Collection) value)) {
                getArtifacts(session, like, searchField, o.toString(), ids);
            }
        } else {
            getArtifacts(session, like, searchField, value.toString(), ids);
        }
        return ids;
    }

    protected void getArtifacts(Session session, final boolean like, final String searchField, final String value,
                                List<String> ids) throws RepositoryException, InvalidQueryException,
            UnsupportedRepositoryOperationException {
        String comparison =  like ? "like" : "=";
        
        QueryManager qm = session.getWorkspace().getQueryManager();
        String queryString = MessageFormat.format("//*[jcr:uuid=\"{0}\"]/*[@{1} {2} \"{3}\"]", artifactsNodeId, searchField, comparison, value);
        
        Query query = qm.createQuery(queryString, Query.XPATH);
        
        for (NodeIterator nodes = query.execute().getNodes(); nodes.hasNext();) {
            ids.add(nodes.nextNode().getUUID());
        }
    }
    
    protected Node createContentNode(Node versionNode,
                                     final InputStream is,
                                     final String contentType)
            throws RepositoryException {
        for (NodeIterator nodes = versionNode.getNodes(); nodes.hasNext();) {
            nodes.nextNode().remove();
        }
        
        // these are required since we inherit from nt:file
        Node resNode = versionNode.addNode("jcr:content", "nt:resource");
        resNode.setProperty("jcr:mimeType", contentType);
        // resNode.setProperty("jcr:encoding", "");
        resNode.setProperty("jcr:lastModified", GalaxyUtils.getCalendarForNow());

        if (is != null) {
            resNode.setProperty("jcr:data", is);
        }
        return resNode;
    }
    
    private String trimContentType(String contentType) {
        int comma = contentType.indexOf(';');
        if (comma != -1) {
            contentType = contentType.substring(0, comma);
        }
        return contentType;
    }

    private String getExtension(String name) {
        int idx = name.lastIndexOf('.');
        if (idx > 0) {
            return name.substring(idx + 1);
        }

        return "";
    }
    
    public Map<String, String> getQueryProperties(PropertyDescriptor pd) {
        HashMap<String, String> props = new HashMap<String, String>();
//        props.put(pd.getProperty() + ".contents", pd.getDescription() + " Contents");
        props.put(pd.getProperty() + ".contentType", pd.getDescription() + " Content Type");
        props.put(pd.getProperty() + ".artifactType", pd.getDescription() + " Artifact Type");
        props.put(pd.getProperty() + ".documentType", pd.getDescription() + " Document Type");

        return props;
    }
    
    public void annotateAtomEntry(Item item, 
                                  PropertyDescriptor pd, 
                                  Entry entry, 
                                  ExtensibleElement metadata,
                                  Factory factory) {
        Artifact artifact = item.getProperty(pd.getProperty());
        
        ExtensibleElement el = factory.newElement(ARTIFACT_QNAME, metadata);
        el.setAttributeValue("property", pd.getProperty());
        el.setAttributeValue("mediaType", artifact.getContentType().toString());
        QName docType = artifact.getDocumentType();
        if (docType != null) {
            el.setAttributeValue("documentType", docType.toString());
        }
    }

    public Collection<QName> getUnderstoodElements() {
        return UNDERSTOOD;
    }

    public Object getValue(Item item, ExtensibleElement e, Factory factory) throws ResponseContextException {
        return null;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public void setArtifactTypeDao(ArtifactTypeDao artifactTypeDao) {
        this.artifactTypeDao = artifactTypeDao;
    }

    public void setTemplate(JcrTemplate template) {
        this.template = template;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

}
