package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

public class WScriptJob implements IsSerializable {
    private String id;
    private String script;
    private String name;
    private String description;
    private String expression;
    private String scriptName;
    
    public WScriptJob(String description, String expression, String id, String name, String script, String scriptName) {
        super();
        this.description = description;
        this.expression = expression;
        this.id = id;
        this.name = name;
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
}
