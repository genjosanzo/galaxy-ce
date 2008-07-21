package org.mule.galaxy.impl.extension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.Dao;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Entry;
import org.mule.galaxy.Identifiable;
import org.mule.galaxy.Item;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.PropertyDescriptor;
import org.mule.galaxy.extension.Extension;
import org.mule.galaxy.policy.PolicyException;

public class IdentifiableExtension<T extends Identifiable> implements Extension, Identifiable {
    private String id;
    private Dao<T> dao;

    @SuppressWarnings("unchecked")
    public Object getExternalValue(Item entry, PropertyDescriptor pd, Object storedValue) {
	if (pd.isMultivalued()) {
	    List<Identifiable> values = new ArrayList<Identifiable>();
	    List<String> ids = (List<String>) storedValue;
	    
	    for (String id : ids) {
		// TODO - account for deleted items
		try {
		    values.add(dao.get(id));
		} catch (NotFoundException e) {
		}
	    }
	    
	    return values;
	} else {
	    try {
		return dao.get((String) storedValue);
	    } catch (NotFoundException e) {
		return null;
	    }
	}
    }

    @SuppressWarnings("unchecked")
    public Object getInternalValue(Item entry, PropertyDescriptor pd, Object value)
	    throws PolicyException {
	if (value instanceof List) {
	    ArrayList<String> ids = new ArrayList<String>();
	    for (Object o : (List)value) {
		Identifiable i = (Identifiable) o;
		
		ids.add(i.getId());
	    }
	    return ids;
	} else if (value instanceof Identifiable) {
	    Identifiable i = (Identifiable) value;
	    if (i.getId() == null) {
		try {
		    dao.save((T) i);
		} catch (DuplicateItemException e) {
		    throw new RuntimeException(e);
		} catch (NotFoundException e) {
		    throw new RuntimeException(e);
		}
	    }
	    return i.getId();
	} else {
	    return null;
	}
    }

    public Dao<T> getDao() {
        return dao;
    }

    public void setDao(Dao<T> dao) {
        this.dao = dao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
}
