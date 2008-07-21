package org.mule.galaxy;

import java.util.List;

public interface Dao<T extends Identifiable> {
    
    T get(String id) throws NotFoundException;
    
    void save(T t) throws DuplicateItemException, NotFoundException;
    
    void delete(String id);
    
    List<T> listAll();
    
    List<T> find(String property, String value);

    Class<T> getTypeClass();
}
