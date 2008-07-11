package org.mule.galaxy.config;

import org.mule.galaxy.event.EventManager;
import org.mule.galaxy.event.TestEvent;
import org.mule.galaxy.event.TestSingleEventListener;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class GalaxyNamespaceHandlerTest extends AbstractGalaxyTest {

    private EventManager eventManager;


    @Override
    protected void onTearDown() throws Exception {
        eventManager.removeListener(TestEvent.class);
        super.onTearDown();
    }

    public void testNSHandler() throws Exception {
        //setDirty();
        System.out.println("GalaxyNamespaceHandlerTest.testNSHandler");
        TestSingleEventListener listener = new TestSingleEventListener();
        eventManager.addListener(listener);
        final TestEvent event = new TestEvent(this);
        eventManager.fireEvent(event);

        assertNotNull(listener.getEvent());
        assertSame(event, listener.getEvent());
    }

    public void setEventManager(final EventManager eventManager) {
        this.eventManager = eventManager;
    }
}
