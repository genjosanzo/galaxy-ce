package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;
import static org.mule.galaxy.event.DefaultEvents.ENTRY_COMMENT_CREATED;
import org.mule.galaxy.event.EntryCommentCreatedEvent;
import org.mule.galaxy.event.annotation.Async;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@BindToEvent(ENTRY_COMMENT_CREATED)
public class EntryCommentCreatedEventListener  extends AbstractActivityLoggingListener {

    protected final Log logger = LogFactory.getLog(getClass());

    @OnEvent
    @Async
    public void onEvent(EntryCommentCreatedEvent event) {
        final String message = MessageFormat.format("Entry comment - {0} - was created for entry {1}", event.getComment().getText(), event.getItemPath());
        getActivityManager().logActivity(message, ActivityManager.EventType.INFO, event.getUser(), event.getItemId());
    }

}
