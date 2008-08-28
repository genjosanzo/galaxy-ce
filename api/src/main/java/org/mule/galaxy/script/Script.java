package org.mule.galaxy.script;

import java.util.Collection;

import org.mule.galaxy.Identifiable;

public class Script implements Identifiable {
    private String id; 
    private String name;
    private String script;
    private Collection<String> jobExpressions;
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
    public Collection<String> getJobExpressions() {
        return jobExpressions;
    }
    public void setJobExpressions(Collection<String> jobExpressions) {
        this.jobExpressions = jobExpressions;
    }
    public boolean isRunOnStartup() {
        return runOnStartup;
    }
    public void setRunOnStartup(boolean runOnStartup) {
        this.runOnStartup = runOnStartup;
    }
}
