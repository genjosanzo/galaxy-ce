package org.mule.galaxy.query;


public class FunctionCall extends Restriction {
    private String module;
    private String name;
    private Object[] arguments;
    
    public FunctionCall(String module, String name, Object... arguments) {
        super();
        this.module = module;
        this.name = name;
        this.arguments = arguments;
    }

    public String getModule() {
        return module;
    }

    public String getName() {
        return name;
    }

    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public void toString(StringBuilder sb) {
        sb.append(name).append("()");
    }
    
}
