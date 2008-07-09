package org.mule.galaxy.config;

import org.mule.galaxy.event.EventManager;
import org.mule.galaxy.event.WorkspaceDeletedEvent;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class GalaxyNamespaceHandlerTest extends AbstractGalaxyTest {

    private EventManager eventManager;

    public void testNSHandler() throws Exception {
        System.out.println("GalaxyNamespaceHandlerTest.testNSHandler");
        final WorkspaceDeletedEvent event = new WorkspaceDeletedEvent();
        eventManager.fireEvent(event);

        // TODO add a test callback listener and validate results (already confirmed ok manually)
    }

    public void setEventManager(final EventManager eventManager) {
        this.eventManager = eventManager;
    }
}
