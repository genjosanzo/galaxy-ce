package org.mule.galaxy.impl.link;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.InvalidQueryException;

import org.springmodules.jcr.JcrCallback;

import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.Item;
import org.mule.galaxy.Link;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;

public class LinkDaoImpl extends AbstractReflectionDao<Link> implements LinkDao {

    public LinkDaoImpl() throws Exception {
        super(Link.class, "links", true);
    }
    

    @SuppressWarnings("unchecked")
    public List<Link> getReciprocalLinks(final Item item) {

        return (List<Link>) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                return getRecriprocalLinks(item, session);
            }
        });
    }


    protected Object getRecriprocalLinks(Item item, Session session) throws InvalidQueryException, RepositoryException {
        EntryVersion ev = null;
        if (item instanceof EntryVersion) {
            ev = (EntryVersion) item;
            item = item.getParent();
        }
        
        StringBuilder q = new StringBuilder();
        q.append("//").append(rootNode).append("/*[@")
         .append("linkedToPath = '").append(item.getName()).append("' or @linkedToPath = '")
         .append(item.getPath()).append("' or @linkedTo = '").append(item.getId());
        
        if (item instanceof EntryVersion) {
            q.append("' or @linkedTo = ").append(item.getPath()).append("?version=").append(ev.getVersionLabel());
            q.append("' or @linkedTo = ").append(item.getName()).append("?version=").append(ev.getVersionLabel());
        }
        
        q.append("']");
        
        return query(q.toString(), session);
    }
}
