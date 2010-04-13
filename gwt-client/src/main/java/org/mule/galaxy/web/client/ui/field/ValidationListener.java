package org.mule.galaxy.web.client.ui.field;

/**
 * A generic validation event listener.
 */
public interface ValidationListener {

    void onSuccess(ValidationEvent event);

    void onFailure(ValidationEvent event);

    /**
     * A simple struct with event source and message (typically a failure message).
     */
    public class ValidationEvent {
        // typically a Widget
        public Object source;
        public String message;

        public ValidationEvent(final Object source, final String message) {
            this.source = source;
            this.message = message;
        }
    }

}
