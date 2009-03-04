package org.mule.galaxy.impl.link;

import static org.mule.galaxy.event.DefaultEvents.ENTRY_CREATED;
import static org.mule.galaxy.event.DefaultEvents.ENTRY_DELETED;
import static org.mule.galaxy.event.DefaultEvents.ENTRY_VERSION_DELETED;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Item;
import org.mule.galaxy.Link;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.event.EntryCreatedEvent;
import org.mule.galaxy.event.EntryDeletedEvent;
import org.mule.galaxy.event.EntryVersionDeletedEvent;
import org.mule.galaxy.event.annotation.BindToEvents;
import org.mule.galaxy.event.annotation.OnEvent;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;
import org.mule.galaxy.query.OpRestriction;
import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.query.SearchResults;
import org.mule.galaxy.security.AccessException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springmodules.jcr.JcrCallback;

@BindToEvents({ENTRY_CREATED, ENTRY_DELETED, ENTRY_VERSION_DELETED})
public class LinkDaoImpl extends AbstractReflectionDao<Link> implements LinkDao, ApplicationContextAware {

    private Registry registry;
    private ApplicationContext context;
    
    public LinkDaoImpl() throws Exception {
        super(Link.class, "links", true);
    }
    
    @OnEvent
    public void onEvent(final EntryDeletedEvent deleted) {
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                deleteAssociatedLinks(deleted.getItemId(), session);
                return null;
            }
        });
    }

    @OnEvent
    public void onEvent(final EntryVersionDeletedEvent deleted) {
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                deleteAssociatedLinks(deleted.getItemId(), session);
                return null;
            }
        });
    }
    
    @OnEvent
    public void onEvent(final EntryCreatedEvent created) {
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                addLinks(created.getItemId(), session);
                return null;
            }
        });
    }

    /**
     * Checks for Links which were not resolved and see if this new entry matches them.
     * 
     * @param id
     * @param session
     * @throws IOException
     * @throws RepositoryException
     */
    protected void addLinks(String id, Session session) throws IOException, RepositoryException {
        try {
            Item item = getRegistry().getItemById(id);
            
            StringBuilder stmt = new StringBuilder();
            stmt.append("//").append(rootNode)
             .append("/*[not(@linkedTo) and jcr:like(@linkedToPath, '%")
             .append(item.getName())
             .append("')]");
            
            QueryManager qm = getQueryManager(session);
            Query q = qm.createQuery(stmt.toString(), Query.XPATH);
            
            QueryResult qr = q.execute();
            
            for (NodeIterator nodes = qr.getNodes(); nodes.hasNext();) {
                Node node = nodes.nextNode();
                Item linkItem = getRegistry().getItemById(JcrUtil.getStringOrNull(node, "item"));
                String path = JcrUtil.getStringOrNull(node, "item");
                Item resolve = registry.resolve(linkItem, path);
                if (resolve != null) {
                    node.setProperty("linkedTo", id);
                }
            }
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        } catch (RegistryException e) {
            throw new RuntimeException(e);
        } catch (AccessException e) {
            throw new RuntimeException(e);
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
            if (auto != null && auto) {
                // we may want to auto resolve this again in the future
                node.setProperty("linkedTo", (String) null);
            } else {
                node.remove();
            }
        }
    }
    
    public List<Link> getReciprocalLinks(Item item, final String property) {
        StringBuilder q = new StringBuilder();
        String path;
        String name;
        
        if (item instanceof EntryVersion) {
            EntryVersion ev = (EntryVersion) item;
            path = item.getParent().getPath() + "?version=" + ev.getVersionLabel();
            name = item.getParent().getName() + "?version=" + ev.getVersionLabel();
        } else {
            path = item.getPath();
            name = item.getName();
        }
        
        q.append("//").append(rootNode).append("/*[(@")
         .append("linkedToPath = '").append(name).append("' or @linkedToPath = '")
         .append(path).append("' or @linkedTo = '").append(item.getId());
        
        q.append("') and property = '").append(property);
        q.append("']");
        
        return (List<Link>) doQuery(q.toString());
    }

    public List<Link> getLinks(final Item item, final String property) {
        StringBuilder q = new StringBuilder();
        q.append("//").append(rootNode)
         .append("/*[@item = '")
         .append(item.getId())
         .append("' and property = '")
         .append(property)
         .append("']");
        
        return (List<Link>) doQuery(q.toString());
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

    public Collection<Item> getReciprocalItems(final String property, final boolean like, final Object value) {
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
            
            stmt.append("linkedTo = '").append(i.getId());
        }

        stmt.append("') and property = '").append(property);
        stmt.append("']");
        System.out.println("GetRecip " + stmt);
        
        Set<Item> items = new HashSet<Item>();
        List<Link> links = doQuery(stmt.toString());
        for (Link l : links) {
            Item linkedTo = l.getLinkedTo();
            
            items.add(linkedTo);
        }
        
        return items;
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
            q.add(OpRestriction.eq("name", path));
        } else {
            q.add(OpRestriction.like("name", path));
        }
        
        SearchResults results = registry.search(q);
        
        items.addAll(results.getResults());

        System.out.println("Found " + items.size() + " items that were in the clause " + q);

    }
    
    public List<Link> getLinks(final String property, final boolean like, final Object path) {
        return getLinks(property, like, path, "item");
    }
    
    public List<Link> getLinks(final String property, final boolean like, final Object path, String linkField) {
        StringBuilder stmt = new StringBuilder();
        stmt.append("//").append(rootNode).append("/*");
        int c = 0;
        
        if (path instanceof String) {
            c = find(stmt, like, (String) path, c, linkField);
        } else if (path instanceof Collection) {
            for (Object o : (Collection) path) {
                c = find(stmt, like, (String) o, c, linkField);
            }
        } else {
            throw new UnsupportedOperationException();
        }
        
        if (c == 0) {
            return Collections.emptyList();
        }
        
        stmt.append("') and property = '").append(property);
        stmt.append("']");

        return doQuery(stmt.toString());
    }

    private int find(StringBuilder stmt, final boolean like, String value, int c, String linkField) {
        org.mule.galaxy.query.Query q = 
            new org.mule.galaxy.query.Query(EntryVersion.class);
        
        if (!like) {
            q.add(OpRestriction.eq("name", value));
        } else {
            q.add(OpRestriction.like("name", value));
        }
        
        try {
            SearchResults results = getRegistry().search(q);
            for (Item i : results.getResults()) {
                if (c > 0) {
                    stmt.append("' or ").append(linkField).append("='").append(i.getId());
                } else {
                    stmt.append("[(").append(linkField).append("='").append(i.getId());
                }
                c++;
            }
        } catch (QueryException e) {
            throw new RuntimeException(e);
        } catch (RegistryException e) {
            throw new RuntimeException(e);
        }
        return c;
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
