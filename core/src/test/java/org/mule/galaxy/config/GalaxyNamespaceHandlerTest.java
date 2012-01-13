package org.mule.galaxy.config;

import org.mule.galaxy.event.EventManager;
import org.mule.galaxy.event.ExampleEvent;
import org.mule.galaxy.event.ExampleSingleEventListener;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class GalaxyNamespaceHandlerTest extends AbstractGalaxyTest {

    private EventManager eventManager;

    private ExampleSingleEventListener listener;

    @Override
    protected void onTearDown() throws Exception {
        eventManager.removeListener(ExampleEvent.class);
        super.onTearDown();
    }

    public void testNSHandler() throws Exception {

        assertNotNull(listener);

        System.out.println("GalaxyNamespaceHandlerTest.testNSHandler");
        final ExampleEvent event = new ExampleEvent(this);
        eventManager.fireEvent(event);

        assertNotNull(listener.getEvent());
        assertSame(event, listener.getEvent());
    }

    public void setEventManager(final EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void setListener(final ExampleSingleEventListener listener) {
        this.listener = listener;
    }
}
