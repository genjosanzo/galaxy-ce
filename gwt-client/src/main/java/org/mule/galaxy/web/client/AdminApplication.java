package org.mule.galaxy.web.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;

/**
 * A simple application implementation which just loads the administration panel.
 * Useful for simple testing only.
 */
public class AdminApplication implements EntryPoint {
    private Galaxy galaxy;
    private String firstPage = "users";
    
    public void onModuleLoad() {
        galaxy = new Galaxy();
        galaxy.setFirstPage(firstPage);
        
        // no extra modules, so just start an empty Galaxy instance with just
        // the admin panel.
        galaxy.getAdministrationPanel();
        galaxy.initialize(new ArrayList<GalaxyModule>());
    }

    public Galaxy getGalaxy() {
        return galaxy;
    }

    public void setFirstPage(String firstPage) {
        this.firstPage = firstPage;
    }

}
