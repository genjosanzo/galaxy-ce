package org.mule.galaxy.wsi.wsdl;

import javax.wsdl.Definition;

import org.w3c.dom.Document;

public abstract class AbstractWsdlRule implements WsdlRule {

    protected String name;

    public AbstractWsdlRule(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    
}
