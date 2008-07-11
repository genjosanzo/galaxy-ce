package org.mule.galaxy.event;

public interface EventManager {

    void addListener(Object listener);

    void removeListener(Class eventClass);

    void fireEvent(GalaxyEvent event);
}
