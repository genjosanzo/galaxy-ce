package org.mule.galaxy;

import java.util.HashSet;
import java.util.Set;

import org.mule.galaxy.mapping.OneToMany;


public class TreeItem implements Identifiable {
    private TreeItem parent;
    private String name;
    private String id;
    private Set<TreeItem> children;
    
    public TreeItem() {
        super();
    }
    public TreeItem(String name, TreeItem parent) {
        this.name = name;
        this.parent = parent;
    }           
    public TreeItem(String name) {
        this.name = name;
    }
    public TreeItem getParent() {
        return parent;
    }
    public void setParent(TreeItem parent) {
        this.parent = parent;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @OneToMany(mappedBy="parent")
    public Set<TreeItem> getChildren() {
        return children;
    }
    
    public void setChildren(Set<TreeItem> children) {
        this.children = children;
    }
    
    public TreeItem addChild(TreeItem child) {
        if (children == null) {
            children = new HashSet<TreeItem>();
        }
        
        children.add(child);
        child.setParent(this);
        return child;
    }
    
    public TreeItem getChild(String name) {
        if (children == null) return null;
        
        for (TreeItem i : children) {
            if (name.equals(i.getName())) {
                return i;
            }
        }
        return null;
    }

    
    public String getFullPath() {
        return getFullPath(true);
    }
    
    public String getFullPath(boolean includeRoot) {
        StringBuffer sb = new StringBuffer();
        TreeItem i = this;
        while (true) {
            if (sb.length() > 0) {
                sb.insert(0, '/');
            }
            sb.insert(0, i.getName());
            i = i.getParent();
            
            if (i == null || !includeRoot && i.getParent() == null) {
                break;
            }
        }
        
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TreeItem other = (TreeItem) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    
}
