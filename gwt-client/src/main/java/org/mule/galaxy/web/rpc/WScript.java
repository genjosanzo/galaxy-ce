package org.mule.galaxy.web.rpc;

import java.util.Collection;

import com.google.gwt.user.client.rpc.IsSerializable;

public class WScript implements IsSerializable {
    private String id; 
    private String name;
    private String script;
    private Collection<String> jobExpressions;
    private boolean runOnStartup;
    
    public WScript(String id, Collection<String> jobExpressions, String name, boolean runOnStartup,
                   String script) {
        super();
        this.id = id;
        this.jobExpressions = jobExpressions;
        this.name = name;
        this.runOnStartup = runOnStartup;
        this.script = script;
    }
    public WScript() {
        super();
    }
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
