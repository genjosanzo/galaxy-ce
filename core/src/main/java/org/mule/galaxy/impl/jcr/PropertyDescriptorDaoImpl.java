package org.mule.galaxy.impl.jcr;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.QueryResult;

import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;
import org.mule.galaxy.type.PropertyDescriptor;

public class PropertyDescriptorDaoImpl extends AbstractReflectionDao<PropertyDescriptor>{

    public PropertyDescriptorDaoImpl() throws Exception {
        super(PropertyDescriptor.class, "propertyDescriptors", true);
    }
    
    protected String generateNodeName(PropertyDescriptor pd) {
        // we can only ever have one property with the same name. If we allowed 
        // property overloading in types (i.e. Different Types can have different 
        // properties of the same name) it would wreak all sorts of havoc in various
        // places. This will enforce uniqueness.
        return pd.getProperty();
    }

    @Override
    /**
     * This will rename all the existing properties on items if a property is renamed.
     * It'd be awesome if we could just issue an update, but JCR doesn't allow that.  
     * 
     */
    protected void move(Session session, Node node, String newName) throws RepositoryException {
        String oldName = node.getName();
        
        super.move(session, node, newName);
        
        QueryResult result = query("//element(*, galaxy:item)[@" + oldName + "]");
        for (NodeIterator nodes = result.getNodes(); nodes.hasNext();) {
            Node item = nodes.nextNode();
         
            Property p = item.getProperty(oldName);
            if (p.getDefinition().isMultiple()) {
                Value[] v = p.getValues();

                item.setProperty(newName, v);
            } else {
                Value v = p.getValue();

                item.setProperty(newName, v);
            }
            p.remove();
        }
        
        session.save();
    }
}
