package org.mule.galaxy.wsi.wsdl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.wsdl.Definition;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
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
        
        xpath.setNamespaceContext(new MapNamespaceContext(namespaces));
    }
    
    public ValidationResult validate(Document document, Definition def) {
        ValidationResult result = new ValidationResult();
        try {
            for (XPathExpression expr : expressions) {
                NodeList nodeset = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
                
                validate(nodeset, document, result);
            }
        } catch (XPathException e) {
            AssertionResult ar = new AssertionResult(name, true);
            result.addAssertionResult(ar);
        }
        return result;
    }

    protected abstract void validate(NodeList nodeset, Document document, ValidationResult result);
    
}
