package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;
import org.mule.galaxy.event.ArtifactVersionCreatedEvent;
import static org.mule.galaxy.event.DefaultEvents.ARTIFACT_VERSION_CREATED;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

import java.text.MessageFormat;

@BindToEvent(ARTIFACT_VERSION_CREATED)
public class ArtifactVersionCreatedEventListener extends AbstractActivityLoggingListener {

    @OnEvent
    public void onEvent(ArtifactVersionCreatedEvent event) {
        final String message = MessageFormat.format("Version {0} was created for artifact {1}",
                                                    event.getVersionLabel(), event.getArtifactPath());
        getActivityManager().logActivity(event.getUser(), message, ActivityManager.EventType.INFO);
    }
}