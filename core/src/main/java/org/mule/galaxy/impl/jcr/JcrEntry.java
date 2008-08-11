package org.mule.galaxy.impl.jcr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Entry;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Item;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.type.Type;
import org.springmodules.jcr.JcrCallback;

public class JcrEntry extends AbstractJcrItem implements Entry {
    public static final String CREATED = "created";
    public static final String DESCRIPTION = "description";
    public static final String NAME = "name";
    public static final String TYPE = "type";
    
    private List<EntryVersion> versions;
    private Workspace workspace;
    private JcrWorkspaceManager manager;
    private ContentHandler contentHandler;
    
    public JcrEntry(Workspace w, Node node, JcrWorkspaceManager registry) 
        throws RepositoryException {
        super(node, registry);
        this.workspace = w;
        this.manager = registry;
    }
    
    public String getPath() {
        StringBuilder sb = getBasePath();
        
        sb.append(getName());
        return sb.toString();
    }
    
    private StringBuilder getBasePath() {
        StringBuilder sb = new StringBuilder();
        
        Item w = workspace;
        while (w != null) {
            sb.insert(0, '/');
            sb.insert(0, ((Workspace)w).getName());
            w = w.getParent();
        }
        sb.insert(0, '/');
        return sb;
    }
    
    public Workspace getParent() {
        return workspace;
    }

    public Calendar getCreated() {
        return getCalendarOrNull(CREATED);
    }

    public String getName() {
        return getStringOrNull(NAME);
    }
    
    public String getDescription() {
        return getStringOrNull(DESCRIPTION);
    }

    
    public void setDescription(String desc) {
        try {
            node.setProperty(DESCRIPTION, desc);
            update();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Type getType() {
        String id = getStringOrNull(TYPE);
        
        try {
            return manager.getTypeManager().getType(id);
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
    }
    
    public void setType(Type t) {
        try {
            node.setProperty(TYPE, t.getId());
            update();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    
    public void setName(final String name) {
        try {
            
            if (!node.getName().equals(name)) {
                manager.execute(new JcrCallback() {
    
                    public Object doInJcr(Session session) throws IOException, RepositoryException {
                        String dest = node.getParent().getPath() + "/" + name;
                        session.move(node.getPath(), dest);
                        return null;
                    }
                    
                });
            }
            node.setProperty(NAME, name);
            update();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<EntryVersion> getVersions() {
        if (versions == null) {
            versions = new ArrayList<EntryVersion>();
            
            try {
                for (NodeIterator itr = node.getNodes(); itr.hasNext();) {
                    Node node = itr.nextNode();
                    
                    if (node.getPrimaryNodeType().getName().equals(getNodeType())) {
                        versions.add(createVersion(node));
                    }
                }
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
            
            Collections.sort(versions, new Comparator<EntryVersion>() {

                public int compare(EntryVersion o1, EntryVersion o2) {
                    return - o1.getCreated().getTime().compareTo(o2.getCreated().getTime());
                }
                
            });
        }
            
        return versions;
    }

    protected String getNodeType() {
        return JcrWorkspaceManager.ENTRY_VERSION_NODE_TYPE;
    }

    protected EntryVersion createVersion(Node node) throws RepositoryException {
        return new JcrEntryVersion(this, node);
    }

    public EntryVersion getVersion(String versionName) {
        for (EntryVersion v : getVersions()) {
            if (v.getVersionLabel().equals(versionName)) {
                return v;
            }
        }
        return null;
    }

    public Node getNode() {
        return node;
    }

    public EntryVersion getDefaultOrLastVersion() {
        for (EntryVersion v : getVersions()) {
            if (v.isDefault()) {
                return v;
            }
        }
        
        return getVersions().get(0);
    }
    
    
    public EntryResult newVersion(String versionLabel)
	    throws RegistryException, PolicyException, DuplicateItemException,
	    AccessException {
	return manager.newVersion(this, versionLabel);
    }
    
    @Override
    public Object getProperty(String name) {
        return getDefaultOrLastVersion().getProperty(name);
    }

    @Override
    public void setProperty(String name, Object value) throws PropertyException, PolicyException {
        getDefaultOrLastVersion().setProperty(name, value);
    }
    
    @Override
    public Collection<PropertyInfo> getProperties() {
        return getDefaultOrLastVersion().getProperties();
    }

    @Override
    public PropertyInfo getPropertyInfo(String name) {
        return getDefaultOrLastVersion().getPropertyInfo(name);
    }

    @Override
    public void setLocked(String name, boolean locked) {
        update();
        getDefaultOrLastVersion().setLocked(name, locked);
    }

    @Override
    public boolean hasProperty(String name) {
        update();
        return getDefaultOrLastVersion().hasProperty(name);
    }

    @Override
    public Object getInternalProperty(String name) {
        return getDefaultOrLastVersion().getInternalProperty(name);
    }

    @Override
    public void setInternalProperty(String name, Object value) throws PropertyException, PolicyException {
        getDefaultOrLastVersion().setInternalProperty(name, value);
    }

    @Override
    public void setVisible(String name, boolean visible) {
        update();
        getDefaultOrLastVersion().setVisible(name, visible);
    }

    public void setVersions(List<EntryVersion> versions2) {
        this.versions = versions2;
    }

    public void delete() throws RegistryException, AccessException {
        manager.delete(this);
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public JcrWorkspaceManager getManager() {
        return manager;
    }

    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

}
