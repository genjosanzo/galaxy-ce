package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;
import org.mule.galaxy.event.ArtifactVersionDeletedEvent;
import static org.mule.galaxy.event.DefaultEvents.ARTIFACT_VERSION_DELETED;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

import java.text.MessageFormat;

@BindToEvent(ARTIFACT_VERSION_DELETED)
public class ArtifactVersionDeletedEventListener extends AbstractActivityLoggingListener {

    @OnEvent
    public void onEvent(ArtifactVersionDeletedEvent event) {
        final String message = MessageFormat.format(
                "Version {0} of artifact {1} was deleted",
                event.getVersionLabel(), event.getArtifactPath());

        getActivityManager().logActivity(event.getUser(), message, ActivityManager.EventType.INFO);
    }
}