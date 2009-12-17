package org.mule.galaxy.repository.client;

import java.util.Arrays;

import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.GalaxyModule;

import com.google.gwt.core.client.EntryPoint;

public class RepositoryApplication implements EntryPoint {

    public void onModuleLoad() {
        Galaxy galaxy = new Galaxy();
        
        // Initialize Galaxy with the RepositoryModule
        galaxy.initialize(Arrays.asList((GalaxyModule)new RepositoryModule()));
    }

}