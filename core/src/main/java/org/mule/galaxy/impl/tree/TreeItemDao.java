package org.mule.galaxy.impl.tree;

import java.util.List;

import org.mule.galaxy.Dao;
import org.mule.galaxy.TreeItem;

public interface TreeItemDao extends Dao<TreeItem> {
    List<TreeItem> getRootTreeItems();

    TreeItem getTreeItem(String path);
}
