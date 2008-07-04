package org.mule.galaxy.events;

public interface EventManager {

    void addListener(Object listener);

    void removeListener(Object listener);

    void fireEvent(GalaxyEvent event);
}
