package org.mule.galaxy.impl.jcr;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.mule.galaxy.LinkType;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;

public class LinkTypeManagerImpl extends AbstractReflectionDao<LinkType> {

    public LinkTypeManagerImpl() throws Exception {
        super(LinkType.class, "dependencyTypes", true);
    }

    @Override
    protected void doCreateInitialNodes(Session session, Node objects)
	    throws RepositoryException {
	try {
	    generateId = false;
	    save(new LinkType(LinkType.DEPENDS, "Depends On", "Depended On By"));
	    save(new LinkType("documents", "Documents", "Documented By"));
	    save(new LinkType("supercedes", "Supercedes", "Superceded By"));
	    save(new LinkType("includes", "Includes", "Included By"));
	    save(new LinkType("conflicts", "Conflicts With"));
	} catch (DuplicateItemException e) {
	} catch (NotFoundException e) {
	    throw new RuntimeException(e);
	} finally {
	    generateId = true;
	}
    }

    @Override
    protected String getObjectNodeName(LinkType t) {
	return t.getId();
    }    
    
}
