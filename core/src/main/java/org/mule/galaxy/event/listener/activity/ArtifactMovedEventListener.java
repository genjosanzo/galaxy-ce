package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;
import org.mule.galaxy.event.ArtifactMovedEvent;
import static org.mule.galaxy.event.DefaultEvents.ARTIFACT_MOVED;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

import java.text.MessageFormat;

@BindToEvent(ARTIFACT_MOVED)
public class ArtifactMovedEventListener extends AbstractActivityLoggingListener {

    @OnEvent
    public void onEvent(ArtifactMovedEvent event) {
        final String message = MessageFormat.format("Artifact {0} was moved to {1}",
                                                    event.getArtifactOldPath(), event.getArtifactNewPath());
        getActivityManager().logActivity(event.getUser(), message, ActivityManager.EventType.INFO);
    }
}