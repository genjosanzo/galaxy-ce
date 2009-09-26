package org.mule.galaxy.web;

import java.util.Collection;

public interface WebManager {

    public abstract void addGwtPlugin(GwtPlugin mod);

    public abstract Collection<GwtPlugin> getGwtPlugins();

    public abstract String getProductName();

    public abstract String getProductCss();

}
