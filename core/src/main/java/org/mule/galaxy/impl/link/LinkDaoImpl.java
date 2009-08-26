package org.mule.galaxy.impl.link;

import static org.mule.galaxy.event.DefaultEvents.ITEM_CREATED;
import static org.mule.galaxy.event.DefaultEvents.ITEM_DELETED;
import static org.mule.galaxy.event.DefaultEvents.ITEM_MOVED;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.Item;
import org.mule.galaxy.Link;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.event.ItemCreatedEvent;
import org.mule.galaxy.event.ItemDeletedEvent;
import org.mule.galaxy.event.ItemMovedEvent;
import org.mule.galaxy.event.annotation.BindToEvents;
import org.mule.galaxy.event.annotation.OnEvent;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;
import org.mule.galaxy.query.OpRestriction;
import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.query.SearchResults;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.util.SecurityUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springmodules.jcr.JcrCallback;

@BindToEvents({ITEM_CREATED, ITEM_MOVED, ITEM_DELETED})
public class LinkDaoImpl extends AbstractReflectionDao<Link> implements LinkDao, ApplicationContextAware {

    private final Log log = LogFactory.getLog(getClass());

    private Registry registry;
    private ApplicationContext context;
    
    public LinkDaoImpl() throws Exception {
        super(Link.class, "links", true);
    }

    /**
     * Checks for 1) Links which were not resolved and see if this new entry matches them 
     * 2) Links which are associated with this ID and tries to resolve them
     * 
     * @param session
     * @throws IOException
     * @throws RepositoryException
     * @throws RegistryException 
     */
    protected void addLinks(Item item, Session session) throws IOException, RepositoryException, RegistryException {
        try {
            // First, see if this item which was moved matches any outstanding unmatched links 
            StringBuilder stmt = new StringBuilder();
            stmt.append("//").append(rootNode)
             .append("/*[not(@linkedTo) and jcr:like(@linkedToPath, '%")
             .append(JcrUtil.stringToXPathLiteralWithoutQuotes(item.getName()))
             .append("%')]");
            
            QueryManager qm = getQueryManager(session);
            Query q = qm.createQuery(stmt.toString(), Query.XPATH);
            
            QueryResult qr = q.execute();
            
            for (NodeIterator nodes = qr.getNodes(); nodes.hasNext();) {
                Node node = nodes.nextNode();
                Item linkItem = getRegistry().getItemById(JcrUtil.getStringOrNull(node, "item"));
                String path = JcrUtil.getStringOrNull(node, "linkedToPath");
                Item resolve = registry.resolve(linkItem, path);
                if (resolve != null) {
                    node.setProperty("linkedTo", resolve.getId());
                }
            }

            // Now try to resolve links which are associated with this item
            stmt = new StringBuilder();
            stmt.append("//").append(rootNode)
             .append("/*[not(@linkedTo) and (@item = '")
             .append(item.getId());

            stmt.append("')]");

            q = qm.createQuery(stmt.toString(), Query.XPATH);
            for (NodeIterator nodes = q.execute().getNodes(); nodes.hasNext();) {
                Node node = nodes.nextNode();
                Item linkItem = getRegistry().getItemById(JcrUtil.getStringOrNull(node, "item"));
                String path = JcrUtil.getStringOrNull(node, "linkedToPath");
                Item resolve = registry.resolve(linkItem, path);
                
                if (resolve != null) {
                    node.setProperty("linkedTo", resolve.getId());
                }
            }
        } catch (NotFoundException e) {
            // this was deleted before we could tie together links... forget about it
        } catch (RegistryException e) {
            throw new RuntimeException(e);
        } catch (AccessException e) {
            throw new RuntimeException(e);
        }
        
        for (Item child : item.getItems()) {
            addLinks(child, session);
        }
    }
    
    protected void deleteAssociatedLinks(String id, Session session) throws IOException, RepositoryException {
        StringBuilder stmt = new StringBuilder();
        stmt.append("//").append(rootNode)
         .append("/*[@item = '")
         .append(id)
         .append("' or linkedTo = '")
         .append(id)
         .append("']");
        
        QueryManager qm = getQueryManager(session);
        Query q = qm.createQuery(stmt.toString(), Query.XPATH);
        
        QueryResult qr = q.execute();
        for (NodeIterator nodes = qr.getNodes(); nodes.hasNext();) {
            Node node = nodes.nextNode();
            
            Boolean auto = JcrUtil.getBooleanOrNull(node, "autoDetected");
            String linkedTo = JcrUtil.getStringOrNull(node, "linkedTo");
            
            if (auto != null && auto && id.equals(linkedTo)) {
                // we may want to auto resolve this again in the future
                node.setProperty("linkedTo", (String) null);
            } else {
                node.remove();
            }
        }
    }
    
    protected void clearDetectedLinks(final Item item, Session session) throws IOException, RepositoryException, RegistryException {
        final StringBuilder stmt = new StringBuilder();
        stmt.append("//").append(rootNode)
            .append("/*[@item = '")
            .append(item.getId())
            .append("' or linkedTo = '")
            .append(item.getId());

        stmt.append("']");
        
        QueryManager qm = getQueryManager(session);
        Query q = qm.createQuery(stmt.toString(), Query.XPATH);
        
        QueryResult qr = q.execute();
        
        for (NodeIterator nodes = qr.getNodes(); nodes.hasNext();) {
            Node node = nodes.nextNode();
            
            Boolean auto = JcrUtil.getBooleanOrNull(node, "autoDetected");
            if (auto != null && auto) {
                // we may want to auto resolve this again in the future
                node.setProperty("linkedTo", (String) null);
            }
        }
        
        for (Item child : item.getItems()) {
            clearDetectedLinks(child, session);
        }
    }
    
    public List<Link> getReciprocalLinks(Item item, final String property) {
        StringBuilder q = new StringBuilder();
        String path = item.getPath();
        String name = item.getName();
        
        q.append("//").append(rootNode).append("/*[(@")
         .append("linkedToPath = '").append(name).append("' or @linkedToPath = '")
         .append(path).append("' or @linkedTo = '").append(item.getId());
        
        q.append("') and property = '").append(property);
        q.append("']");
        
        return doQuery(q.toString());
    }

    public List<Link> getLinks(final Item item, final String property) {
        StringBuilder q = new StringBuilder();
        q.append("//").append(rootNode)
         .append("/*[@item = '")
         .append(item.getId())
         .append("' and property = '")
         .append(property)
         .append("']");
        
        List<Link> links = doQuery(q.toString());
        for (Iterator<Link> itr = links.iterator(); itr.hasNext();) {
            Link link = itr.next();
            if (link.getLinkedTo() == null && link.getLinkedToPath() == null) {
                itr.remove();
            }
        }
        return links;
    }
    
    public void deleteLinks(final Item item, final String property) {
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                deleteLinks(item, property, session);
                return null;
            }
        });
    }

    protected void deleteLinks(Item item, String property, Session session) throws IOException, RepositoryException {
        StringBuilder stmt = new StringBuilder();
        stmt.append("//").append(rootNode)
         .append("/*[@item = '")
         .append(item.getId())
         .append("' and property = '")
         .append(property)
         .append("']");
        
        QueryManager qm = getQueryManager(session);
        Query q = qm.createQuery(stmt.toString(), Query.XPATH);
        
        QueryResult qr = q.execute();
        
        for (NodeIterator nodes = qr.getNodes(); nodes.hasNext();) {
            Node node = nodes.nextNode();
            node.remove();
        }
    }

    public Collection<Item> getReciprocalItems(final String property, 
                                               final boolean like, 
                                               final Object value) {
        Collection<Link> links = getLinks(property, "item", like, value);

        Set<Item> items = new HashSet<Item>();
        for (Link l : links) {
            Item linkedTo = l.getLinkedTo();
            
            items.add(linkedTo);
        }
        
        return items;
    }
    
    public List<Link> getLinks(final String property,
                               String linkProperty,
                               final boolean like, 
                               final Object value) {
        // Find the Items which we'll use for the next part of the query
        Set<Item> linkToItems = new HashSet<Item>();

        try {

            if (value instanceof Collection) {
                Collection values = (Collection) value;
                for (Object v : values) {
                    findItemsForPath(linkToItems, v.toString(), like);
                }
            } else {
                findItemsForPath(linkToItems, value.toString(), like);
            }

        } catch (QueryException e) {
            throw new RuntimeException(e);
        } catch (RegistryException e) {
            throw new RuntimeException(e);
        }
        
        if (linkToItems.size() == 0) return Collections.emptyList();
        
        // Now find all the links where the linkedTo property is equal to one of the above items
        StringBuilder stmt = new StringBuilder();
        stmt.append("//").append(rootNode).append("/*[(@");
        
        boolean first = true;
        for (Item i : linkToItems) {
            if (first) first = false;
            else stmt.append("' or ");
            
            stmt.append(linkProperty).append(" = '").append(i.getId());
        }

        stmt.append("') and property = '").append(property);
        stmt.append("']");
        
        return doQuery(stmt.toString());
    }

    /**
     * Find all the items which match the given path
     * @param items
     * @param path
     * @param like
     * @throws RegistryException
     * @throws QueryException
     */
    private void findItemsForPath(Set<Item> items, String path, final boolean like)
        throws RegistryException, QueryException {
        org.mule.galaxy.query.Query q = 
            new org.mule.galaxy.query.Query();
        
        int idx = path.lastIndexOf('/');
        if (idx != -1) {
            q.fromPath(path.substring(0, idx));
            path = path.substring(idx+1);
        }

        if (!like) {
            q.add(OpRestriction.eq("name", JcrUtil.stringToXPathLiteralWithoutQuotes(path)));
        } else {
            q.add(OpRestriction.like("name", JcrUtil.stringToXPathLiteralWithoutQuotes(path)));
        }
        
        SearchResults results = getRegistry().search(q);
        
        items.addAll(results.getResults());
        
    }
    
    public List<Link> getLinks(final String property, final boolean like, final Object path) {
        return getLinks(property, "linkedTo", like, path);
    }
    
    @OnEvent
    public void onEvent(final ItemDeletedEvent deleted) {
        SecurityUtils.doPriveleged(new Runnable() {
            public void run() {
                execute(new JcrCallback() {
                    public Object doInJcr(final Session session) throws IOException, RepositoryException {
                        deleteAssociatedLinks(deleted.getItemId(), session);
                        return null;
                    }
                });
            }
        });
    }
    
    @OnEvent
    public void onEvent(final ItemCreatedEvent created) {
        SecurityUtils.doPriveleged(new Runnable() {
            public void run() {
                execute(new JcrCallback() {
                    public Object doInJcr(final Session session) throws IOException, RepositoryException {
                        try {
                            Item item = getRegistry().getItemById(created.getItemId());
                            
                            addLinks(item, session);
                       } catch (RegistryException e) {
                           throw new RuntimeException(e);
                       } catch (NotFoundException e) {
                       } catch (AccessException e) {
                           log.error(e);
                       }
                       return null;
                    }
                });
            }
        });
    }
    
    @OnEvent
    public void onEvent(final ItemMovedEvent created) {
        SecurityUtils.doPriveleged(new Runnable() {
            public void run() {
                execute(new JcrCallback() {
                    public Object doInJcr(final Session session) throws IOException, RepositoryException {
                        try {
                             Item item = getRegistry().getItemById(created.getItemId());
                             
                             clearDetectedLinks(item, session);
                             addLinks(item, session);
                        } catch (RegistryException e) {
                            throw new RuntimeException(e);
                        } catch (NotFoundException e) {
                        } catch (AccessException e) {
                            log.error(e);
                        }
                        return null;
                    }
                });
            }
        });
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public Registry getRegistry() {
        if (registry == null) {
            registry = (Registry) context.getBean("registry");
        }
        return registry;
    }

    
}
