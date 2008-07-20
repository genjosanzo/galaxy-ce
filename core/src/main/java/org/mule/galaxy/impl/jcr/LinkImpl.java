package org.mule.galaxy.impl.jcr;

import javax.jcr.Node;

import org.mule.galaxy.Item;
import org.mule.galaxy.Link;
import org.mule.galaxy.LinkType;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.security.AccessException;

public class LinkImpl implements Link {

    public static final String PATH = "path";
    public static final String AUTO_DETECTED = "autoDetected";
    public static final String RELATIONSHIP = "relationship";
    
    private final String path;
    private final JcrRegistryImpl registry;
    private Item item;
    private final String type;
    private boolean autoDetected;
    private final Node node;
    private final Item parent;
    
    public LinkImpl(Item parent, Node node, JcrRegistryImpl registry) {
        this.parent = parent;
        this.node = node;
        this.registry = registry;
        path = JcrUtil.getStringOrNull(node, PATH);
        type = JcrUtil.getStringOrNull(node, RELATIONSHIP);
        Boolean detected = JcrUtil.getBooleanOrNull(node, AUTO_DETECTED);
        
        if (detected == null)
        {
            detected = Boolean.FALSE; 
        }
        
        this.autoDetected = detected;
    }

    public Item getParent() {
        return parent;
    }

    public boolean exists() {
        return getItem() != null;
    }

    public Item getItem() {
        if (item == null) {
            try {
                item = registry.getItemByPath(path);
            } catch (AccessException e) {
                // don't list dependencies which the user shouldn't see
            } catch (NotFoundException e) {
                // Guess its not in our repository
            } catch (RegistryException e) {
                throw new RuntimeException(e);
            }
        }
        return item;
    }

    public String getPath() {
        return path;
    }

    public LinkType getType() {
        try {
            return registry.getLinkTypeDao().get(type);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isAutoDetected() {
        return autoDetected;
    }

    public Node getNode() {
        return node;
    }

}
