package org.mule.galaxy.web;

import java.util.Collection;

public interface WebManager {

    public abstract void addGwtModule(GwtModule mod);

    public abstract Collection<GwtModule> getGwtModules();

    public abstract Collection<GwtFacet> getGwtFacets();

    public abstract String getProductName();

    public abstract String getProductCss();

	public abstract void addGwtFacet(GwtFacet facet);

}
