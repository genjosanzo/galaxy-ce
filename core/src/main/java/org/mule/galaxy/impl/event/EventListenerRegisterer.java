package org.mule.galaxy.impl.event;

import java.util.List;

import org.mule.galaxy.event.EventManager;

public class EventListenerRegisterer {
    private EventManager eventManager;
    private List<Object> listeners;
    
    public void initialize() {
        for (Object listener : listeners) {
            eventManager.addListener(listener);
        }
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public List<Object> getListeners() {
        return listeners;
    }

    public void setListeners(List<Object> listeners) {
        this.listeners = listeners;
    }
    
}
