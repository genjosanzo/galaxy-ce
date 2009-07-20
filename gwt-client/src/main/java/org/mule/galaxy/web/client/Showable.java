package org.mule.galaxy.web.client;

import java.util.List;

/**
 * Implemented by components which want to receive events when they
 * are shown or hidden. showPage() will also pass in the URL parameters
 * for the component to parse.
 * 
 */
public interface Showable {
    public void showPage(List<String> params);
    
    public void hidePage();
}
