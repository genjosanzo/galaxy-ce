package org.mule.galaxy.event;

public class TestEvent extends GalaxyEvent {

    private Object source;

    public TestEvent(final Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }
}
