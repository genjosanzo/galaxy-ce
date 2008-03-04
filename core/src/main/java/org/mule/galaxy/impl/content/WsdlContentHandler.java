package org.mule.galaxy.impl.content;

import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.XmlContentHandler;
import org.mule.galaxy.impl.RegistryLocator;
import org.mule.galaxy.util.Constants;
import org.mule.galaxy.util.QNameUtil;
import org.mule.galaxy.wsdl.diff.DifferenceEvent;
import org.mule.galaxy.wsdl.diff.DifferenceListener;
import org.mule.galaxy.wsdl.diff.WsdlDiff;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.wsdl.WSDLException;
import javax.wsdl.xml.WSDLLocator;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class WsdlContentHandler extends XmlDocumentContentHandler implements XmlContentHandler {

    public WsdlContentHandler() 
        throws WSDLException, MimeTypeParseException, XPathExpressionException {
        super(false);
        
        primaryContentType = new MimeType("application/wsdl+xml");
        supportedContentTypes.add(primaryContentType);
        supportedContentTypes.add(new MimeType("text/wsdl+xml"));
        
        supportedTypes.add(Document.class);
        
        supportedDocumentTypes.add(Constants.WSDL_DEFINITION_QNAME);
        
        namespaces.put("xsd", Constants.SCHEMA_QNAME.getNamespaceURI());
        namespaces.put("wsdl", Constants.WSDL_DEFINITION_QNAME.getNamespaceURI());
        imports.add(xpath.compile("//wsdl:import/@location"));
        imports.add(xpath.compile("//xsd:import/@schemaLocation"));
        
        supportedFileExtensions.clear();
        supportedFileExtensions.add("wsdl");
    }

    public String getName(Object o) {
        Document d = (Document) o;
        
        Node n = d.getAttributes().getNamedItem("name");
        
        if (n != null) {
            return n.getNodeValue();
        }
        
        return null;
    }

    public void write(Object o, OutputStream stream) throws IOException {
        // TODO Auto-generated method stub
    }
    
    public QName getDocumentType(Object o) {
        return Constants.WSDL_DEFINITION_QNAME;
    }

    public String describeDifferences(ArtifactVersion v1, ArtifactVersion v2) {
        
        Document doc1 = (Document) v1.getData();
        Document doc2 = (Document) v2.getData();
        
        if (Constants.WSDL_DEFINITION_QNAME.equals(QNameUtil.getName(doc1.getDocumentElement()))) {
            return createWsdlDiff(v1, v2, doc1, doc2);
        }
        
        return "Version " + v2.getVersionLabel();
    }
    
    private String createWsdlDiff(ArtifactVersion v1, ArtifactVersion v2, Document doc1, Document doc2) {
        WsdlDiff diff = new WsdlDiff();
        // TODO - get a reference to the registry for the locator
        WSDLLocator l = new RegistryLocator(registry, v1.getParent().getWorkspace());
        try {
            diff.setOriginalWSDL(doc1, l);
        } catch (WSDLException e) {
            return "The previous WSDL was not valid or could not be read: " + e.getMessage();
        }
        
        try {
            diff.setNewWSDL(doc2, l);
        } catch (WSDLException e) {
            return "The WSDL was not valid or could not be read: " + e.getMessage();
        }
        
        final List<String> changes = new ArrayList<String>();
        diff.check(new DifferenceListener() {
            public void onEvent(DifferenceEvent event) {
                changes.add(event.getDescription());
            }
        });
        
        if (changes.size() == 0) {
            return "Version " + v2.getVersionLabel() + ". There were no structural changes since the previous version.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Version ")
          .append(v2.getVersionLabel())
          .append(". The following changes since the previous version. <ul>");
        for (String c : changes) {
            sb.append("<li>")
              .append(c)
              .append("</li>");
        }
        sb.append("</ul>");
        
        return sb.toString();
    }

}
