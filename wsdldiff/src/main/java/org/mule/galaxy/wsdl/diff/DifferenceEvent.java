package org.mule.galaxy.wsdl.diff;

public class DifferenceEvent {
    private String description;
    private String type;
    private boolean forwardCompatabile;
    private boolean backwardCompatabile;
    
    public DifferenceEvent(String description, String type, 
                           boolean forwardCompatabile, boolean backwardCompatabile) {
        super();
        this.description = description;
        this.type = type;
        this.forwardCompatabile = forwardCompatabile;
        this.backwardCompatabile = backwardCompatabile;
    }

    public String getDescription() {
        return description;
    }
    
    public String getType() {
        return type;
    }

    public boolean isBackwardCompatabile() {
        return backwardCompatabile;
    }

    public boolean isForwardCompatabile() {
        return forwardCompatabile;
    }
    
}
