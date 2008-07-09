package org.mule.galaxy.event.listener;

import static org.mule.galaxy.event.DefaultEvents.WORKSPACE_DELETED;
import org.mule.galaxy.event.WorkspaceDeletedEvent;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

@BindToEvent(WORKSPACE_DELETED)
public class SysOutLoggingEventListener {

    private String prefix = ">>>>>>> ";

    @OnEvent
    public void onEvent(final WorkspaceDeletedEvent event) {
        System.out.println(prefix + event);
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }
}
