package org.mule.galaxy.events.listeners;

import static org.mule.galaxy.events.DefaultEvents.WORKSPACE_CREATED;
import static org.mule.galaxy.events.DefaultEvents.WORKSPACE_DELETED;
import org.mule.galaxy.events.GalaxyEvent;
import org.mule.galaxy.events.GalaxyEventListener;
import org.mule.galaxy.events.WorkspaceCreatedEvent;
import org.mule.galaxy.events.WorkspaceDeletedEvent;
import org.mule.galaxy.events.annotations.BindToEvents;

@BindToEvents({
        WORKSPACE_CREATED,
        WORKSPACE_DELETED})
// TODO refactor this
public class ActivityLoggerListener implements GalaxyEventListener {

    public void internalOnEvent(final WorkspaceCreatedEvent e) {
        System.out.println("WORKSPACE CREATED");
    }

    public void internalOnEvent(final WorkspaceDeletedEvent e) {
        System.out.println("WORKSPACE DELETED");
    }


    public void onEvent(final GalaxyEvent event) {
        if (event instanceof WorkspaceCreatedEvent) {
            internalOnEvent((WorkspaceCreatedEvent) event);
        } else if (event instanceof WorkspaceDeletedEvent) {
            internalOnEvent((WorkspaceDeletedEvent) event);
        } else {
            System.out.println("NOT SUPPORTED");
        }

    }
}
