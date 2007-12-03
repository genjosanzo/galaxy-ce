package org.mule.galaxy.wsi.wsdl;

public class AssertionResult {
    private String name;
    private boolean failed;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isFailed() {
        return failed;
    }
    public void setFailed(boolean failed) {
        this.failed = failed;
    }
}
