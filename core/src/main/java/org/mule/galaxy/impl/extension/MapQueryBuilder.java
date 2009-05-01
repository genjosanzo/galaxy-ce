package org.mule.galaxy.impl.extension;


public class MapQueryBuilder extends ExtensionQueryBuilder {

    public MapQueryBuilder() {
        super();
        getSuffixes().add("key");
        getSuffixes().add("value");
    }

    @Override
    protected String getProperty(String property) {
        // JcrUtil.setProperty stores Maps with the suffixes ".keys" and ".values"
        if (property.endsWith(".key") || property.endsWith(".value")) {
            return property + "s";
        }
        return super.getProperty(property);
    }

}
