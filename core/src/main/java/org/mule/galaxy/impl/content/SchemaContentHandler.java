package org.mule.galaxy.impl.content;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.wsdl.WSDLException;
import javax.wsdl.xml.WSDLLocator;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.XmlContentHandler;
import org.mule.galaxy.impl.MapNamespaceContext;
import org.mule.galaxy.impl.RegistryLocator;
import org.mule.galaxy.util.Constants;
import org.mule.galaxy.util.QNameUtil;
import org.mule.galaxy.wsdl.diff.DifferenceEvent;
import org.mule.galaxy.wsdl.diff.DifferenceListener;
import org.mule.galaxy.wsdl.diff.WsdlDiff;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SchemaContentHandler extends XmlDocumentContentHandler implements XmlContentHandler {

    private MimeType primaryContentType;
    
    public SchemaContentHandler() 
        throws WSDLException, MimeTypeParseException, XPathExpressionException {
        super(false);
        
        primaryContentType = new MimeType("application/xmlschema+xml");
        supportedContentTypes.add(primaryContentType);
        supportedContentTypes.add(new MimeType("text/xmlschema+xml"));
        
        supportedTypes.add(Document.class);
        supportedDocumentTypes.add(Constants.SCHEMA_QNAME);
        
        namespaces.put("xsd", Constants.SCHEMA_QNAME.getNamespaceURI());
        imports.add(xpath.compile("//xsd:import/@schemaLocation"));
    }

    public String getName(Object o) {
        return null;
    }

    public void write(Object o, OutputStream stream) throws IOException {
        // TODO Auto-generated method stub
    }
    
    public QName getDocumentType(Object o) {
        return Constants.SCHEMA_QNAME;
    }


}
