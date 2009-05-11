package org.mule.galaxy.impl.link;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Item;
import org.mule.galaxy.Link;
import org.mule.galaxy.Links;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.event.PropertyChangedEvent;
import org.mule.galaxy.extension.AtomExtension;
import org.mule.galaxy.extension.Extension;
import org.mule.galaxy.impl.extension.IdentifiableExtension;
import org.mule.galaxy.impl.jcr.JcrItem;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.type.TypeManager;
import org.mule.galaxy.util.SecurityUtils;

public class LinkExtension extends IdentifiableExtension<Link> implements Extension, AtomExtension {
    
    public static final String RECIPROCAL_CONFIG_KEY = "Reciprocal Name";

    public static final String CONFLICTS = "conflicts";

    public static final String INCLUDES = "includes";

    public static final String SUPERCEDES = "supercedes";

    public static final String DOCUMENTS = "documents";

    public final static String DEPENDS = "depends";
    
    private TypeManager typeManager;
    private List<String> configuration = new ArrayList<String>();
    
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
        return true;
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
    public void store(Item item, PropertyDescriptor pd, Object value) throws PolicyException, AccessException {
        ((JcrItem) item).getSaveEvents().add(new PropertyChangedEvent(SecurityUtils.getCurrentUser(), item, name, value));

        try {
            if (!pd.isMultivalued()) {
                ((LinkDao) dao).deleteLinks(item, pd.getId());
                new LinksImpl(pd, item).addLinks(new Link(item, (Item) value, null, false));
            } else if (value instanceof Collection) {
                for (Object o : (Collection) value) {
                    Link l = (Link) o;
                    new LinksImpl(pd, item).addLinks(l);
                }
            } else if (value == null) {
                ((LinkDao) dao).deleteLinks(item, pd.getId());
                item.setInternalProperty(pd.getProperty(), null);
            } else {
                throw new UnsupportedOperationException();
            }
        } catch (PropertyException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object get(final Item item, final PropertyDescriptor pd, boolean getWithNoData) {
        Boolean hasLinks = (Boolean) item.getInternalProperty(pd.getProperty());
        
        if (!getWithNoData && (hasLinks == null || !hasLinks)) {
            return null;
        }
        
        LinksImpl links = new LinksImpl(pd, item);
        
        if (!getWithNoData && links.getReciprocalLinks().isEmpty() && links.getLinks().isEmpty()) {
            try {
                item.setInternalProperty(pd.getProperty(), null);
            } catch (PropertyException e) {
                throw new RuntimeException(e);
            } catch (PolicyException e) {
                throw new RuntimeException(e);
            } catch (AccessException e) {
                // we're trying to correct some data here, so don't blow up
            }
            return null;
        }
        
        if (pd.isMultivalued()) {
            return links;
        } else {
            Collection<Link> links2 = links.getLinks();
            if (links2.size() > 0) {
                return links2.iterator().next().getItem();
            }
            return null;
        }
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
    
    public void annotateAtomEntry(Item item, PropertyDescriptor pd, Entry entry, ExtensibleElement metadata,
                                  Factory factory) {
        // not yet supported
    }

    public Collection<QName> getUnderstoodElements() {
        return Collections.emptyList();
    }

    public Object getValue(Item item, ExtensibleElement e, Factory factory) throws ResponseContextException {
        // not yet supported
        return null;
    }

    public void setTypeManager(TypeManager typeManager) {
        this.typeManager = typeManager;
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

        public void addLinks(Link l) throws AccessException {
            if (l.getItem() == null) {
                // this is a hack for when we add items.
                l.setItem(item);
            } else if (!l.getItem().equals(item)) {
                throw new IllegalStateException("Item specified must be the item associated with this Links instance.");
            }
            
            try {
                l.setProperty(pd.getId());
                dao.save(l);
                if (links != null) {
                    links.add(l);
                } else {
                    links = new ArrayList<Link>();
                    links.add(l);
                }

                item.setInternalProperty(pd.getProperty(), true);
                if (l.getLinkedTo() != null) {
                    l.getLinkedTo().setInternalProperty(pd.getProperty(), true);
                }
                ((JcrItem) item).getSaveEvents().add(new PropertyChangedEvent(SecurityUtils.getCurrentUser(), item, pd.getId(), getLinks()));
            } catch (DuplicateItemException e) {
                throw new RuntimeException(e);
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            } catch (PropertyException e) {
                throw new RuntimeException(e);
            } catch (PolicyException e) {
                throw new RuntimeException(e);
            }
        }

        public Collection<Link> getLinks() {
            if (links == null) {
                links = ((LinkDao) dao).getLinks(item, pd.getId());
            }
            return links;
        }

        public Collection<Link> getReciprocalLinks() {
            if (reciprocal == null) {
                reciprocal = ((LinkDao) dao).getReciprocalLinks(item, pd.getId());
            }
            return reciprocal;
        }

        public void removeLinks(Link... links) {
            for (Link l : links) {
                dao.delete(l.getId());
            }
            reciprocal = null;
            this.links = null;
            ((JcrItem) item).getSaveEvents().add(new PropertyChangedEvent(SecurityUtils.getCurrentUser(), item, pd.getId(), getLinks()));
        }
    }
}
