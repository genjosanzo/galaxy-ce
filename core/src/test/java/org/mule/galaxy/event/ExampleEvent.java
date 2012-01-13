package org.mule.galaxy.event;

public class ExampleEvent extends GalaxyEvent {

    private Object source;

    public ExampleEvent(final Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }
}
