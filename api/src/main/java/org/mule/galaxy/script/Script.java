package org.mule.galaxy.script;

import java.io.Serializable;

import org.mule.galaxy.Identifiable;

public class Script implements Identifiable, Serializable {
    private String id; 
    private String name;
    private String script;
    private boolean runOnStartup;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getScript() {
        return script;
    }
    public void setScript(String script) {
        this.script = script;
    }
    public boolean isRunOnStartup() {
        return runOnStartup;
    }
    public void setRunOnStartup(boolean runOnStartup) {
        this.runOnStartup = runOnStartup;
    }
}
