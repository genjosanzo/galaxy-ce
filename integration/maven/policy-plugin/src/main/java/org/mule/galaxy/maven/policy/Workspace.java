package org.mule.galaxy.maven.policy;

import java.util.List;

public class Workspace {
    /**
     * The workspace URL.
     *
     * @parameter
     */
    private String url;
    
    /**
     * Files to check against workspace policies.
     *
     * @parameter
     */
    private String[] includes;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String[] getIncludes() {
        return includes;
    }

    public void setIncludes(String[] includes) {
        this.includes = includes;
    }
}
