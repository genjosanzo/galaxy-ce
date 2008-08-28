package org.mule.galaxy.web.client.property;


/**
 * Creates renderers for different property types.
 */
public class PropertyPanelFactory {
    
    public AbstractPropertyRenderer createRenderer(String ext, boolean multivalued) {
        if ("lifecycleExtension".equals(ext)) {
            return new LifecycleRenderer();
        } else if ("linkExtension".equals(ext)) {
            return new LinksRenderer();
        } else if ("userExtension".equals(ext)) {
            return new UserListRenderer();
        } else if (multivalued) {
            return new SimpleListRenderer();
        } else {
            return new SimpleRenderer();
        }
    }
}
