package org.mule.galaxy.impl.link;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Item;
import org.mule.galaxy.Link;
import org.mule.galaxy.Links;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.impl.extension.IdentifiableExtension;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.type.TypeManager;
import org.mule.galaxy.util.SecurityUtils;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

public class LinkExtension extends IdentifiableExtension<Link> {
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
        
	configuration.add("Reciprocal Name");
	
	add(DEPENDS, "Depends On", "Depended On By");
        add(DOCUMENTS, "Documents", "Documented By");
        add(SUPERCEDES, "Supercedes", "Superceded By");
        add(INCLUDES, "Includes", "Included By");
        add(CONFLICTS, "Conflicts With", "Is Conflicted By");
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
    public Object getExternalValue(final Item item, final PropertyDescriptor pd, final Object storedValue) {
        return new Links() {
            public void addLinks(Link l) throws RegistryException {
                if (!l.getItem().equals(item)) {
                    throw new IllegalStateException("Item specified must be the item associated with this Links instance.");
                }
                
                List<String> ids = getLinkIds(item, pd);
                try {
                    dao.save(l);
                    
                    ids.add(l.getId());
                    updateLinks(item, pd, ids);
                } catch (DuplicateItemException e) {
                    throw new RuntimeException(e);
                } catch (NotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

            @SuppressWarnings("unchecked")
            public Collection<Link> getLinks() {
                return addRegistry((Collection<Link>) LinkExtension.super.getExternalValue(item, pd, item.getInternalProperty(pd.getProperty())));
            }

            public Collection<Link> getReciprocalLinks() throws RegistryException {
                return addRegistry(((LinkDao) dao).getReciprocalLinks(item));
            }

            public void removeLinks(Link... links) throws RegistryException {
                List<String> ids = getLinkIds(item, pd);
                
                for (Link l : links) {
                    dao.delete(l.getId());
                    ids.remove(l.getId());
                }
                
                updateLinks(item, pd, ids);
            }

            @SuppressWarnings("unchecked")
            private List<String> getLinkIds(final Item item, final PropertyDescriptor pd) {
                List<String> ids = (List<String>) item.getInternalProperty(pd.getProperty());
                if (ids == null) {
                    ids = new ArrayList<String>();
                }
                return ids;
            }

            private void updateLinks(final Item item, final PropertyDescriptor pd, List<String> ids) {
                try {
                    item.setInternalProperty(pd.getProperty(), ids);
                } catch (PropertyException e) {
                    throw new RuntimeException(e);
                } catch (PolicyException e) {
                    throw new RuntimeException(e);
                }
            }

            
        };
    }
    
    protected Collection<Link> addRegistry(Collection<Link> links) {
        for (Link l : links) {
            l.setRegistry(registry);
        }
        return links;
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

}
