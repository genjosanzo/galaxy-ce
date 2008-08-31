package org.mule.galaxy.impl.link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Item;
import org.mule.galaxy.Link;
import org.mule.galaxy.Links;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.event.PropertyChangedEvent;
import org.mule.galaxy.extension.Extension;
import org.mule.galaxy.impl.extension.IdentifiableExtension;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.type.TypeManager;
import org.mule.galaxy.util.SecurityUtils;

public class LinkExtension extends IdentifiableExtension<Link> implements Extension {
    
    public static final String RECIPROCAL_CONFIG_KEY = "Reciprocal Name";

    public static final String CONFLICTS = "conflicts";

    public static final String INCLUDES = "includes";

    public static final String SUPERCEDES = "supercedes";

    public static final String DOCUMENTS = "documents";

    public final static String DEPENDS = "depends";
    
    private TypeManager typeManager;
    private List<String> configuration = new ArrayList<String>();
    private Registry registry;
    
    public void initialize() throws Exception {
        setName("Link");
        
	configuration.add(RECIPROCAL_CONFIG_KEY);
	
	add(DEPENDS, "Depends On", "Depended On By");
        add(DOCUMENTS, "Documents", "Documented By");
        add(SUPERCEDES, "Supercedes", "Superceded By");
        add(INCLUDES, "Includes", "Included By");
        add(CONFLICTS, "Conflicts With", "Conflicted By");
    }
    
    @Override
    public boolean isMultivalueSupported() {
        return false;
    }

    private void add(String property, String name, String inverse) {
        final PropertyDescriptor pd = new PropertyDescriptor(property, name, true, false);
        pd.setExtension(this);
        
        List<String> keys = getPropertyDescriptorConfigurationKeys();
        HashMap<String, String> configuration = new HashMap<String, String>();
        configuration.put(keys.get(0), inverse);
        pd.setConfiguration(configuration);
        
        SecurityUtils.doPriveleged(new Runnable() {

            public void run() {
                try {
                    typeManager.savePropertyDescriptor(pd);
                } catch (DuplicateItemException e) {
                } catch (AccessException e) {
                } catch (NotFoundException e) {
                }
            }
            
        });
    }

    @Override
    public void store(Item item, PropertyDescriptor pd, Object value) throws PolicyException {
        eventManager.fireEvent(new PropertyChangedEvent(SecurityUtils.getCurrentUser(), item.getPath(), name, value));
        if (value instanceof Collection) {
            for (Object o : (Collection) value) {
                Link l = (Link) o;
                new LinksImpl(pd, item).addLinks(l);
            }
        } else if (value == null) {
            ((LinkDao) dao).deleteLinks(item, pd.getProperty());
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Object get(final Item item, final PropertyDescriptor pd, boolean getWithNoData) {
        Links links = new LinksImpl(pd, item);
        
        if (!getWithNoData && links.getReciprocalLinks().isEmpty() && links.getLinks().isEmpty()) {
            return null;
        }
        return links;
    }
    
    protected Collection<Link> addRegistry(Collection<Link> links) {
        for (Link l : links) {
            l.setRegistry(registry);
        }
        return links;
    }

    @Override
    public Map<String, String> getQueryProperties(PropertyDescriptor pd) {
        HashMap<String, String> props = new HashMap<String, String>();
        props.put(pd.getProperty(), pd.getDescription());
        props.put(pd.getProperty() + ".reciprocal",
                  pd.getConfiguration().get(RECIPROCAL_CONFIG_KEY));
        
        return props;
    }

    @Override
    public List<String> getPropertyDescriptorConfigurationKeys() {
        return configuration;
    }
    
    public void setTypeManager(TypeManager typeManager) {
        this.typeManager = typeManager;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }
    
    public final class LinksImpl implements Links {
        private final PropertyDescriptor pd;
        private final Item item;
        private Collection<Link> links;
        private Collection<Link> reciprocal;

        private LinksImpl(PropertyDescriptor pd, Item item) {
            this.pd = pd;
            this.item = item;
        }

        public void addLinks(Link l) {
            if (!l.getItem().equals(item)) {
                throw new IllegalStateException("Item specified must be the item associated with this Links instance.");
            }
            
            try {
                l.setProperty(pd.getProperty());
                dao.save(l);
                if (links != null) {
                    links.add(l);
                } else {
                    links = new ArrayList<Link>();
                    links.add(l);
                }
                eventManager.fireEvent(new PropertyChangedEvent(SecurityUtils.getCurrentUser(), item.getPath(), pd.getProperty(), getLinks()));
            } catch (DuplicateItemException e) {
                throw new RuntimeException(e);
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public Collection<Link> getLinks() {
            if (links == null) {
                links = addRegistry(((LinkDao) dao).getLinks(item, pd.getProperty()));
            }
            return links;
        }

        public Collection<Link> getReciprocalLinks() {
            if (reciprocal == null) {
                reciprocal = addRegistry(((LinkDao) dao).getReciprocalLinks(item, pd.getProperty()));
            }
            return reciprocal;
        }

        public void removeLinks(Link... links) {
            for (Link l : links) {
                dao.delete(l.getId());
            }
            reciprocal = null;
            this.links = null;
            eventManager.fireEvent(new PropertyChangedEvent(SecurityUtils.getCurrentUser(), item.getPath(), pd.getProperty(), getLinks()));
        }
    }
}
