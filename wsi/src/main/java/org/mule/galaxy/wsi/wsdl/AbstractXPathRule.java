package org.mule.galaxy.wsi.wsdl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.wsdl.Definition;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractXPathRule extends AbstractWsdlRule {
    protected XPathFactory factory = XPathFactory.newInstance();

    protected XPath xpath;

    protected HashMap<String, String> namespaces;

    protected List<XPathExpression> expressions = new ArrayList<XPathExpression>();

    
    public AbstractXPathRule(String name) {
        super(name);
        xpath = factory.newXPath();
        namespaces = new HashMap<String, String>();
        namespaces.put("wsdl", "http://schemas.xmlsoap.org/wsdl/");
        namespaces.put("soap", "http://schemas.xmlsoap.org/wsdl/soap/");
        
        xpath.setNamespaceContext(new MapNamespaceContext(namespaces));
    }
    
    public ValidationResult validate(Document document, Definition def) {
        ValidationResult result = new ValidationResult();
        AssertionResult ar = new AssertionResult(getName(), true);
        boolean validated = true;
        
        try {
            for (XPathExpression expr : expressions) {
                NodeList nodeset = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
                
                boolean v1 = validate(nodeset, document, ar);
                if (!v1) validated = false;
            }
            
        } catch (XPathException e) {
            result.addAssertionResult(ar);
        }

        if (!validated || ar.getMessages().size() > 0) {
            ar.setFailed(!validated);
            result.addAssertionResult(ar);
        }
        
        return result;
    }

    protected boolean validate(NodeList nodeset, Document document, AssertionResult ar)
        throws XPathExpressionException {
        boolean validated = true;
        
        for (int i = 0; i < nodeset.getLength(); i++) {
            Node n = nodeset.item(i);
            
            boolean v1 = validate(ar, n);
            if (!v1) validated = false;
        }
        
        return validated;
    }

    protected abstract boolean validate(AssertionResult ar, Node n) throws XPathExpressionException;
    
}
