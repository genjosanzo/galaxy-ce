package org.mule.galaxy.wsdl.diff;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLLocator;
import javax.wsdl.xml.WSDLReader;

import org.w3c.dom.Document;

import org.mule.galaxy.wsdl.diff.rule.Rule;
import org.mule.galaxy.wsdl.diff.rule.WsdlStructureRule;

public class WsdlDiff {
    private Definition original;
    private Definition newWsdl;
    private List<Rule> rules;
    
    public WsdlDiff() {
        this.rules = new ArrayList<Rule>();
        
        rules.add(new WsdlStructureRule());
    }
    
    public void setOriginalWSDL(String resource) throws WSDLException {
        setOriginalWSDL(readDefinition(resource));
    }
    
    public void setOriginalWSDL(Definition definition) {
        this.original = definition;
    }

    public void setNewWSDL(String resource) throws WSDLException {
        setNewWSDL(readDefinition(resource));
    }


    public void setNewWSDL(Definition definition) {
        this.newWsdl = definition;
        
    }
    
    private Definition readDefinition(String resource) throws WSDLException {
        WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
        
        Definition d = reader.readWSDL(resource);
        
        readTypes(d);
        
        return d;
    }

    private void readTypes(Definition d) {
        Types types = d.getTypes();
        if (types == null) {
            return;
        }
        
        List ees = types.getExtensibilityElements();
        
        for (Iterator itr = ees.iterator(); itr.hasNext();) {
            ExtensibilityElement ee = (ExtensibilityElement) itr.next();
            
            if (ee instanceof Schema) {
                Schema s = (Schema) ee;
                
                // TODO read in schema to XSOM
                s.toString();
            }
        }
    }

    public void check(DifferenceListener listener) {
        for (Rule r : rules) {
            r.check(original, newWsdl, null, null, listener);
        }
    }

    public void setOriginalWSDL(Document wsdl, WSDLLocator loc) throws WSDLException {
        WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
        
        this.original = reader.readWSDL(loc, wsdl.getDocumentElement());
    }

    public void setNewWSDL(Document wsdl, WSDLLocator loc) throws WSDLException {
        WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
        
        this.original = reader.readWSDL(loc, wsdl.getDocumentElement());
    }

}
