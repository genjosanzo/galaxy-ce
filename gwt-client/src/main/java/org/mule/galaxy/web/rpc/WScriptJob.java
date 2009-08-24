package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.extjs.gxt.ui.client.data.BeanModelTag;

public class WScriptJob implements IsSerializable, BeanModelTag {
    private String id;
    private String script;
    private String name;
    private String description;
    private String expression;
    private String scriptName;
    private boolean concurrentExecutionAllowed;
    
    public WScriptJob(String description, String expression, String id, String name, boolean concurrentExecutionAllowed,
                      String script, String scriptName) {
        super();
        this.description = description;
        this.expression = expression;
        this.id = id;
        this.name = name;
        this.concurrentExecutionAllowed = concurrentExecutionAllowed;
        this.script = script;
        this.scriptName = scriptName;
    }
    
    public WScriptJob() {
        super();
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getScript() {
        return script;
    }
    public void setScript(String script) {
        this.script = script;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getExpression() {
        return expression;
    }
    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getScriptName() {
        return scriptName;
    }

    public boolean isConcurrentExecutionAllowed() {
        return concurrentExecutionAllowed;
    }

    public void setConcurrentExecutionAllowed(boolean concurrentExecutionAllowed) {
        this.concurrentExecutionAllowed = concurrentExecutionAllowed;
    }
    
}
