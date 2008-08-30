package org.mule.galaxy.impl.upgrade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.value.StringValue;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Link;
import org.mule.galaxy.Registry;
import org.mule.galaxy.impl.jcr.AbstractJcrItem;
import org.mule.galaxy.impl.jcr.JcrArtifact;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.impl.jcr.JcrWorkspace;
import org.mule.galaxy.impl.jcr.JcrWorkspaceManager;
import org.mule.galaxy.impl.link.LinkExtension;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.type.Type;
import org.mule.galaxy.type.TypeManager;

public class V11Upgrader extends Upgrader {

    private final Log log = LogFactory.getLog(getClass());
    
    private TypeManager typeManager;
    
    private Registry registry;
    
    private JcrWorkspaceManager manager;
    
    @Override
    public void doUpgrade(int version, Session session, Node root) throws Exception {
        if (version >= 3) return;
        
        log.info("Upgrading to version 1.1....");
        
        QueryManager qm = session.getWorkspace().getQueryManager();
        
        upgradeIndexes(root, qm);
        
        upgradeComments(root, qm);

        upgradeLifecycle(root, qm);

        upgradeGroups(root, qm, session);

        upgradePlugins(root, qm, session);

        upgradeLinks(root, qm, session);
        
        log.info("Upgrade to version 1.1 complete!");
    }
    
    /**
     * Sets index field to true on property descriptors if they refer to an Index.
     */
    private void upgradeIndexes(Node root, QueryManager qm) throws RepositoryException {
        
        Node pds = root.getNode("propertyDescriptors");
        for (NodeIterator itr = pds.getNodes(); itr.hasNext();) {
            Node pd = itr.nextNode();
            String name = pd.getName();
            
            String qStr = "//indexes//*[@property='" + JcrUtil.escape(name) + "']";
            Query q = qm.createQuery(qStr, Query.XPATH);
            
            if (q.execute().getNodes().getSize() > 0) {
                pd.setProperty("index", true);
            }
        }
    }
    
    /**
     * Comment.artifact was renamed to Comment.item
     */
    private void upgradeComments(Node root, QueryManager qm) throws RepositoryException {
        Query q = qm.createQuery("//comments/*", Query.XPATH);
        
        for (NodeIterator itr = q.execute().getNodes(); itr.hasNext();) {
            Node av = itr.nextNode();
            String artifact = JcrUtil.getStringOrNull(av, "artifact");
            
            JcrUtil.setProperty("artifact", null, av);
            
            JcrUtil.setProperty("item", artifact, av);
        }
    }

    /**
     * Changes the lifecycle fields to a metadata property on the artifact
     */
    private void upgradeLifecycle(Node root, QueryManager qm) throws Exception {
        Query q = qm.createQuery("//element(*, galaxy:artifact)/*", Query.XPATH);
        
        for (NodeIterator itr = q.execute().getNodes(); itr.hasNext();) {
            Node av = itr.nextNode();
            String lifecycle = JcrUtil.getStringOrNull(av, "lifecycle");
            String phase = JcrUtil.getStringOrNull(av, "phase");
            
            JcrUtil.setProperty("lifecycle", null, av);
            JcrUtil.setProperty("phase", null, av);
            
            JcrUtil.setProperty("primary.lifecycle", Arrays.asList(lifecycle, phase), av);
            
            try {
                Property p = av.getProperty(AbstractJcrItem.PROPERTIES);
                
                List<Value> values = new ArrayList<Value>();
                for (Value v : p.getValues()) {
                    values.add(v);
                }
                values.add(new StringValue("primary.lifecycle"));
                
                p.setValue(values.toArray(new Value[values.size()]));
            } catch (PathNotFoundException e) {
                
            }
        }
         

        final PropertyDescriptor lifecyclePD = new PropertyDescriptor();
        lifecyclePD.setProperty(Registry.PRIMARY_LIFECYCLE);
        lifecyclePD.setDescription("Primary lifecycle");
        lifecyclePD.setExtension(registry.getExtension("lifecycleExtension"));
        
        final Type defaultType = new Type();
        defaultType.setName("Base Type");
        defaultType.setProperties(Arrays.asList(lifecyclePD));

        typeManager.savePropertyDescriptor(lifecyclePD);
        typeManager.saveType(defaultType);
    }

    /**
     * Changes the lifecycle fields to a metadata property on the artifact
     */
    private void upgradeGroups(Node root, QueryManager qm, Session session) throws RepositoryException {
        Query q = qm.createQuery("//groups/*/*", Query.XPATH);
        
        for (NodeIterator itr = q.execute().getNodes(); itr.hasNext();) {
            Node g = itr.nextNode();
            
            session.move(g.getPath(), g.getParent().getPath() + "local$" + g.getName());
        }
    }
    /**
     * Accounts for plugin name changes.
     */
    private void upgradePlugins(Node root, QueryManager qm, Session session) throws RepositoryException {
        Query q = qm.createQuery("//plugins/*", Query.XPATH);
        
        String oldPkg = "org.mule.galaxy.impl.artifact.";
        
        for (NodeIterator itr = q.execute().getNodes(); itr.hasNext();) {
            Node p = itr.nextNode();
            String name = JcrUtil.getStringOrNull(p, "plugin");
            
            if (name.startsWith(oldPkg)) {
                String newName = "org.mule.galaxy.impl.plugin." + name.substring(oldPkg.length());
                
                JcrUtil.setProperty("plugin", newName, p);
            }
        }
    }
    
    /**
     * links are now represented much differently.
     */
    private void upgradeLinks(Node root, QueryManager qm, Session session) throws Exception {
        Query q = qm.createQuery("//element(*, galaxy:artifactVersion)/dependencies", Query.XPATH);

        Node links = root.getNode("links");
        for (NodeIterator itr = q.execute().getNodes(); itr.hasNext();) {
            Node d = itr.nextNode();
            
            Node avNode = d.getParent();
            Node aNode = avNode.getParent();
           
            JcrWorkspace w = new JcrWorkspace(manager, aNode.getParent());
            JcrArtifact a = new JcrArtifact(w, aNode, manager);
            
            ContentHandler ch = a.getContentHandler();
            for (EntryVersion av : a.getVersions()) {
                Set<String> dependencies = ch.detectDependencies(((ArtifactVersion)av).getData(), w);
               
                for (String dep : dependencies) {
                    Node l = links.addNode(UUID.randomUUID().toString(), "galaxy:link");
                    JcrUtil.setProperty("linkedToPath", dep, l);
                    JcrUtil.setProperty("item", av.getId(), l);
                    JcrUtil.setProperty("autoDetected", true, l);
                    JcrUtil.setProperty("property", LinkExtension.DEPENDS, l);
                }
            }
            d.remove();
        }
    }

    public void setTypeManager(TypeManager typeManager) {
        this.typeManager = typeManager;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public void setLocalWorkspaceManager(JcrWorkspaceManager localWorkspaceManager) {
        this.manager = localWorkspaceManager;
    }
    
}
