package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;
import org.mule.galaxy.event.ArtifactDeletedEvent;
import static org.mule.galaxy.event.DefaultEvents.ARTIFACT_DELETED;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

import java.text.MessageFormat;

@BindToEvent(ARTIFACT_DELETED)
public class ArtifactDeletedEventListener extends AbstractActivityLoggingListener {

    @OnEvent
    public void onEvent(ArtifactDeletedEvent event) {
        final String message = MessageFormat.format("Artifact {0} was deleted", event.getArtifactPath());
        getActivityManager().logActivity(event.getUser(), message, ActivityManager.EventType.INFO);
    }
}