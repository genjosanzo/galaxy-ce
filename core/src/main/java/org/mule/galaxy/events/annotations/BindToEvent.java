package org.mule.galaxy.events.annotations;

import java.lang.annotation.Retention;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface BindToEvent {
    String value();
}
