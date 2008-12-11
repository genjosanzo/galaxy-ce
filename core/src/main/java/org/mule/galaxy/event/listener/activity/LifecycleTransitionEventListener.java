package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;
import static org.mule.galaxy.event.DefaultEvents.LIFECYCLE_TRANSITION;
import org.mule.galaxy.event.LifecycleTransitionEvent;
import org.mule.galaxy.event.annotation.Async;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

import java.text.MessageFormat;

@BindToEvent(LIFECYCLE_TRANSITION)
public class LifecycleTransitionEventListener extends AbstractActivityLoggingListener {

    @OnEvent
    @Async
    public void onEvent(LifecycleTransitionEvent event) {
        final String message = MessageFormat.format("Item {0} was transitioned to phase {2} " +
                                                    "in lifecycle {3}",
                                                    event.getItemPath(),
                                                    event.getNewPhaseName(), 
                                                    event.getLifecycleName());
        getActivityManager().logActivity(message, ActivityManager.EventType.INFO, event.getUser(), null);
    }
}