package org.mule.galaxy.web.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;

/**
 * A simple application implementation which just loads the administration panel.
 * Useful for simple testing only.
 */
public class AdminApplication implements EntryPoint {

    public void onModuleLoad() {
        Galaxy galaxy = new Galaxy();
        
        // no extra modules, so just start an empty Galaxy instance with just
        // the admin panel.
        galaxy.initialize(new ArrayList<GalaxyModule>());
    }

}
