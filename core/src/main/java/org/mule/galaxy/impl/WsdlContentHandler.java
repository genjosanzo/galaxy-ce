package org.mule.galaxy.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.wsdl.Definition;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLLocator;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.XmlContentHandler;
import org.mule.galaxy.util.Constants;
import org.mule.galaxy.util.QNameUtil;
import org.mule.galaxy.wsdl.diff.DifferenceEvent;
import org.mule.galaxy.wsdl.diff.DifferenceListener;
import org.mule.galaxy.wsdl.diff.WsdlDiff;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class WsdlContentHandler extends XmlDocumentContentHandler implements XmlContentHandler {

    private MimeType primaryContentType;
    
    public WsdlContentHandler() throws WSDLException, MimeTypeParseException {
        super(false);
        
        primaryContentType = new MimeType("application/wsdl+xml");
        supportedContentTypes.add(primaryContentType);
        supportedContentTypes.add(new MimeType("text/wsdl+xml"));
        
        supportedTypes.add(Document.class);
    }

    public MimeType getContentType(Object o) {
        return primaryContentType;
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

    @Override
    public Set<Artifact> detectDependencies(Object o, Workspace w) {
        // TODO Auto-generated method stub
        return super.detectDependencies(o, w);
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
