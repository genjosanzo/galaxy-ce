package org.mule.galaxy.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WebManagerImpl implements WebManager {
    private final Log log = LogFactory.getLog(getClass());

    private String productName;
    private String productCss;
    private List<GwtModule> plugins = new ArrayList<GwtModule>();
    private List<GwtFacet> facets = new ArrayList<GwtFacet>();

    public void addGwtModule(GwtModule mod) {
        log.info("Found GWT module: " + mod.getName());
        plugins.add(mod);
    }

    public void addGwtFacet(GwtFacet facet) {
        log.info("Found GWT facet: " + facet.getName());
        facets.add(facet);
	}

	public List<GwtFacet> getGwtFacets() {
		return facets;
	}

	public void setGwtFacets(List<GwtFacet> facets) {
		this.facets = facets;
	}

	public Collection<GwtModule> getGwtModules() {
        return plugins;
    }
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCss() {
        return productCss;
    }

    public void setProductCss(String productCss) {
        this.productCss = productCss;
    }
    
}
