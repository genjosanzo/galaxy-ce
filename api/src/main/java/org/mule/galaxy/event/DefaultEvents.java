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

    // item
    public static final String ITEM_VERSION_CREATED = "ItemVersionCreated";
    public static final String ITEM_VERSION_DELETED = "ItemVersionDeleted";

    // item version
    public static final String ITEM_CREATED = "ItemCreated";
    public static final String ITEM_DELETED = "ItemDeleted";
    public static final String ITEM_MOVED = "ItemMoved";

    // misc
    public static final String LIFECYCLE_TRANSITION = "LifecycleTransition";
}
