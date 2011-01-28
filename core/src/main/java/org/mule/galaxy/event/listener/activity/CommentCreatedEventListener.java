package org.mule.galaxy.event.listener.activity;

import static org.mule.galaxy.event.DefaultEvents.COMMENT_CREATED;

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.activity.ActivityManager;
import org.mule.galaxy.event.CommentCreatedEvent;
import org.mule.galaxy.event.annotation.Async;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

@BindToEvent(COMMENT_CREATED)
public class CommentCreatedEventListener  extends AbstractActivityLoggingListener {

    protected final Log logger = LogFactory.getLog(getClass());

    @OnEvent
    @Async
    public void onEvent(CommentCreatedEvent event) {
        final String message = MessageFormat.format("Entry comment - {0} - was created for entry {1}", event.getComment().getText(), event.getItemPath());
        getActivityManager().logActivity(message, ActivityManager.EventType.INFO, event.getUser(), event.getItemId());
    }

}
