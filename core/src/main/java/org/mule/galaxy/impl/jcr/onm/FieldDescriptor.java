package org.mule.galaxy.impl.jcr.onm;

import java.lang.reflect.Method;

import org.mule.galaxy.mapping.OneToMany;

public class FieldDescriptor {
    private String name;
    private boolean id;
    private Class<?> type;
    private OneToMany oneToMany;
    private Method readMethod;
    private Method writeMethod;
    private ClassPersister classPersister;
    private Class componentType;
    
    public ClassPersister getClassPersister() {
        return classPersister;
    }
    public void setClassPersister(ClassPersister classPersister) {
        this.classPersister = classPersister;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isId() {
        return id;
    }
    public void setId(boolean id) {
        this.id = id;
    }
    public Class<?> getType() {
        return type;
    }
    public void setType(Class<?> type) {
        this.type = type;
    }
    public OneToMany getOneToMany() {
        return oneToMany;
    }
    public void setOneToMany(OneToMany oneToMany) {
        this.oneToMany = oneToMany;
    }
    public Method getReadMethod() {
        return readMethod;
    }
    public void setReadMethod(Method readMethod) {
        this.readMethod = readMethod;
    }
    public Method getWriteMethod() {
        return writeMethod;
    }
    public void setWriteMethod(Method writeMethod) {
        this.writeMethod = writeMethod;
    }
    public Class getComponentType() {
        return componentType;
    }
    public void setComponentType(Class componentType) {
        this.componentType = componentType;
    }
    
}
