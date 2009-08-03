package org.mule.galaxy.impl.extension;

import javax.jcr.Node;
import javax.jcr.Session;

import org.mule.galaxy.Identifiable;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.impl.jcr.onm.FieldDescriptor;
import org.mule.galaxy.impl.jcr.onm.FieldPersister;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ExtensionPersister implements FieldPersister, ApplicationContextAware {
    private ApplicationContext context;
    
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public Object build(Node n, FieldDescriptor fd, Session session) throws Exception {
        String val = JcrUtil.getStringOrNull(n, fd.getName());
        if (val == null) return null;

        return build(val, fd, session);
    }

    public Object build(String id, FieldDescriptor fd, Session session) throws Exception {
        return context.getBean(id);
    }

    public void persist(Object o, Node n, FieldDescriptor fd, Session session) throws Exception {
        if (o == null) {
            n.setProperty(fd.getName(), (String) null);
        } else {
            n.setProperty(fd.getName(), ((Identifiable) o).getId());
        }
    }
}

