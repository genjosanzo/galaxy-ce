package org.mule.galaxy.impl.jcr;

import javax.jcr.Node;
import javax.jcr.Session;

import org.mule.galaxy.Item;
import org.mule.galaxy.Registry;
import org.mule.galaxy.impl.jcr.onm.FieldDescriptor;
import org.mule.galaxy.impl.jcr.onm.FieldPersister;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ItemPersister implements FieldPersister, ApplicationContextAware {
    private Registry registry;
    private ApplicationContext context;
    
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public Registry getRegistry() {
        if (registry == null) {
            registry = (Registry) context.getBean("registry");
        }
        return registry;
    }

    public Object build(Node n, FieldDescriptor fd, Session session) throws Exception {
        String val = JcrUtil.getStringOrNull(n, fd.getName());
        if (val == null) return null;

        return build(val, fd, session);
    }

    public Object build(String id, FieldDescriptor fd, Session session) throws Exception {
        return getRegistry().getItemById(id);
    }

    public void persist(Object o, Node n, FieldDescriptor fd, Session session) throws Exception {
        if (o == null) {
            n.setProperty(fd.getName(), (String) null);
        } else {
            n.setProperty(fd.getName(), ((Item) o).getId());
        }
    }

}
