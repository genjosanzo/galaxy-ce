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
    private List<GwtPlugin> plugins = new ArrayList<GwtPlugin>();

    public void addGwtPlugin(GwtPlugin mod) {
        log.info("Found GWT module: " + mod.getModuleName());
        plugins.add(mod);
    }

    public Collection<GwtPlugin> getGwtPlugins() {
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
