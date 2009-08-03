package org.mule.galaxy.web.client.property;


/**
 * Creates renderers for different property types.
 */
public class PropertyInterfaceManager {
    public AbstractPropertyRenderer createRenderer(String ext, boolean multivalued) {
        if ("lifecycleExtension".equals(ext)) {
            return new LifecycleRenderer();
        } else if ("linkExtension".equals(ext)) {
            return new LinksRenderer();
        } else if ("userExtension".equals(ext)) {
            return new UserListRenderer();
        } else if ("mapExtension".equals(ext)) {
            return new MapRenderer();
        } else if ("artifactExtension".equals(ext)) {
            return new ArtifactRenderer();
        } else if (multivalued) {
            return new SimpleListRenderer();
        } else {
            return new SimpleRenderer();
        }
    }
    
    public boolean isExtensionEditable(String ext) {
        if ("treeExtension".equals(ext) || "artifactExtension".equals(ext)) {
            return false;
        }
        return true;
    }

    public boolean isExtensionRenderable(String ext) {
        if ("treeExtension".equals(ext)) {
            return false;
        }
        return true;
    }
}
