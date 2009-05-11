package org.mule.galaxy.impl.jcr;

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
}
