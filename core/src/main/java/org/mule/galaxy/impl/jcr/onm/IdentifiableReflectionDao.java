package org.mule.galaxy.impl.jcr.onm;

import org.mule.galaxy.Identifiable;

public class IdentifiableReflectionDao extends AbstractReflectionDao<Identifiable> {

    /**
     * This constructor is for CGLIB... argh.
     * @throws Exception
     */
    public IdentifiableReflectionDao() throws Exception {
	super(Identifiable.class, "objects", true);
    }
    
    public IdentifiableReflectionDao(Class<Identifiable> t, String rootNode,
	    boolean generateId) throws Exception {
	super(t, rootNode, generateId);
    }

}
