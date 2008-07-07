package org.mule.galaxy.event;

public interface EventManager {

    void addListener(Object listener);

    void removeListener(Object listener);

    void fireEvent(GalaxyEvent event);
}
