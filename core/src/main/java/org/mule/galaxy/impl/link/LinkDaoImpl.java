package org.mule.galaxy.impl.link;

import static org.mule.galaxy.event.DefaultEvents.ENTRY_DELETED;
import static org.mule.galaxy.event.DefaultEvents.ENTRY_VERSION_DELETED;

import java.io.IOException;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Item;
import org.mule.galaxy.Link;
import org.mule.galaxy.event.EntryDeletedEvent;
import org.mule.galaxy.event.EntryVersionDeletedEvent;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.BindToEvents;
import org.mule.galaxy.event.annotation.OnEvent;
import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;
import org.springmodules.jcr.JcrCallback;

@BindToEvents({ENTRY_DELETED, ENTRY_VERSION_DELETED})
public class LinkDaoImpl extends AbstractReflectionDao<Link> implements LinkDao {

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
            node.remove();
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Link> getReciprocalLinks(final Item item, final String property) {

        return (List<Link>) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                return getRecriprocalLinks(item, property, session);
            }
        });
    }


    protected Object getRecriprocalLinks(Item item, String property, Session session) throws InvalidQueryException, RepositoryException {
        EntryVersion ev = null;
        if (item instanceof EntryVersion) {
            ev = (EntryVersion) item;
            item = item.getParent();
        }
        
        StringBuilder q = new StringBuilder();
        q.append("//").append(rootNode).append("/*[(@")
         .append("linkedToPath = '").append(item.getName()).append("' or @linkedToPath = '")
         .append(item.getPath()).append("' or @linkedTo = '").append(item.getId());
        
        if (item instanceof EntryVersion) {
            q.append("' or @linkedToPath = ").append(item.getPath()).append("?version=").append(ev.getVersionLabel());
            q.append("' or @linkedToPath = ").append(item.getName()).append("?version=").append(ev.getVersionLabel());
        }
        
        q.append("') and property = '").append(property);
        q.append("']");
        
        return query(q.toString(), session);
    }
    
    @SuppressWarnings("unchecked")
    public List<Link> getLinks(final Item item, final String property) {
        return (List<Link>) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                return getLinks(item, property, session);
            }
        });
    }

    protected Object getLinks(Item item, String property, Session session) throws InvalidQueryException, RepositoryException {
        StringBuilder q = new StringBuilder();
        q.append("//").append(rootNode)
         .append("/*[@item = '")
         .append(item.getId())
         .append("' and property = '")
         .append(property)
         .append("']");
        
        return query(q.toString(), session);
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

}
