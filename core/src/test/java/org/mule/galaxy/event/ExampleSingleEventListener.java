package org.mule.galaxy.event;

import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

@BindToEvent("Example")
public class ExampleSingleEventListener {

    private ExampleEvent event;

    @OnEvent
    public void onEvent(ExampleEvent event) {
        this.event = event;
    }

    public ExampleEvent getEvent() {
        return event;
    }

    public void reset() {
        this.event = null;
    }
}
