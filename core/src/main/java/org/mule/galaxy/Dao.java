package org.mule.galaxy;

import java.util.List;

public interface Dao<T extends Identifiable> {
    
    T get(String id);
    
    void save(T t);
    
    void delete(String id);
    
    List<T> listAll();
    
    List<T> find(String property, String value);
}
