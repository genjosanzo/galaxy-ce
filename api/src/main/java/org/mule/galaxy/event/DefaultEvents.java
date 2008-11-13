package org.mule.galaxy.event;

/**
 * A convenience Constants class for every standard event in Galaxy.
 */
public class DefaultEvents {

    // workspace
    public static final String WORKSPACE_CREATED = "WorkspaceCreated";
    public static final String WORKSPACE_DELETED = "WorkspaceDeleted";

    // property
    public static final String PROPERTY_CHANGED = "PropertyChanged";

    // entry
    public static final String ENTRY_VERSION_CREATED = "EntryVersionCreated";
    public static final String ENTRY_VERSION_DELETED = "EntryVersionDeleted";
    public static final String ENTRY_COMMENT_CREATED = "EntryCommentCreated";

    // entry version
    public static final String ENTRY_CREATED = "EntryCreated";
    public static final String ENTRY_DELETED = "EntryDeleted";
    public static final String ENTRY_MOVED = "EntryMoved";

    // misc
    public static final String LIFECYCLE_TRANSITION = "LifecycleTransition";
}
