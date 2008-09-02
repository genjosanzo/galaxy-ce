package org.mule.galaxy.script;

import org.mule.galaxy.Identifiable;

public class ScriptJob implements Identifiable {
    private String id;
    private Script script;
    private String name;
    private String description;
    private String expression;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Script getScript() {
        return script;
    }
    public void setScript(Script script) {
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
}
