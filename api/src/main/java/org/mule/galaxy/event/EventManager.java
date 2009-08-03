package org.mule.galaxy.event;

import java.util.List;

public interface EventManager {

    void addListener(Object listener);

    List<Object> getListeners();
    
    void removeListener(Object listener);

    void fireEvent(GalaxyEvent event);
}
