package org.mule.galaxy;

import java.util.List;
import java.util.Map;

public interface Dao<T extends Object> {
    
    T get(String id) throws NotFoundException;
    
    void save(T t) throws DuplicateItemException, NotFoundException;
    
    void delete(String id);
    
    List<T> listAll();
    
    List<T> find(String property, String value);
    
    List<T> find(Map<String, Object> criteria);
    
    List<T> find(Map<String, Object> criteria, int start, int count);
    
    List<T> find(Map<String, Object> criteria, String sortByField, boolean asc, int start, int count);

    Class<T> getTypeClass();
}
