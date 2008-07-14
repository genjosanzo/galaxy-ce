package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;
import org.mule.galaxy.event.ArtifactCreatedEvent;
import static org.mule.galaxy.event.DefaultEvents.ARTIFACT_CREATED;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

import java.text.MessageFormat;

@BindToEvent(ARTIFACT_CREATED)
public class ArtifactCreatedEventListener extends AbstractActivityLoggingListener {

    @OnEvent
    public void onEvent(ArtifactCreatedEvent event) {
        final String message = MessageFormat.format("Artifact {0} was created", event.getArtifactPath());
        getActivityManager().logActivity(event.getUser(), message, ActivityManager.EventType.INFO);
    }
}