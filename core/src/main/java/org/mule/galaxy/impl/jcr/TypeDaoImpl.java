package org.mule.galaxy.impl.jcr;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;
import org.mule.galaxy.type.Type;

public class TypeDaoImpl extends AbstractReflectionDao<Type> {

    public TypeDaoImpl() throws Exception {
        super(Type.class, "types", true);
    }

    @Override
    protected String generateNodeName(Type t) {
        return t.getName();
    }

//    @Override
//    protected String getId(Type t, Node node, Session session) throws RepositoryException {
//        // TODO Auto-generated method stub
//        return node.getUUID();
//    }

}
