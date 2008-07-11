package org.mule.galaxy.event;

import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

@BindToEvent("Test")
public class TestSingleEventListener {

    private TestEvent event;

    @OnEvent
    public void onEvent(TestEvent event) {
        this.event = event;
    }

    public TestEvent getEvent() {
        return event;
    }

    public void reset() {
        this.event = null;
    }
}
