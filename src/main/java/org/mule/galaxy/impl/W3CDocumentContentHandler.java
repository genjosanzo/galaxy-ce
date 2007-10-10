package org.mule.galaxy.impl;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.wsdl.WSDLException;
import javax.wsdl.xml.WSDLLocator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.cxf.helpers.DOMUtils;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.util.Constants;
import org.mule.galaxy.util.QNameUtil;
import org.mule.galaxy.wsdl.diff.DifferenceEvent;
import org.mule.galaxy.wsdl.diff.DifferenceListener;
import org.mule.galaxy.wsdl.diff.WsdlDiff;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.xml.sax.SAXException;

public class W3CDocumentContentHandler extends AbstractContentHandler implements ContentHandler {

    public String getName(Object o) {
        Document doc = (Document) o;
        // TODO: look for name attribute
        return doc.getDocumentElement().getAttribute("name");
    }

    public String getContentType(Object o) {
        return "application/xml";
    }

    public String describe(ArtifactVersion v) {
        return "Initial version.";
    }

    public String desribeDifferences(ArtifactVersion v1, ArtifactVersion v2) {
        
        Document doc1 = (Document) v1.getData();
        Document doc2 = (Document) v2.getData();
        
        if (Constants.WSDL_DEFINITION.equals(QNameUtil.getName(doc1.getDocumentElement()))) {
            return createWsdlDiff(v1, v2, doc1, doc2);
        }
        
        return "Version " + v2.getVersion();
    }

    private String createWsdlDiff(ArtifactVersion v1, ArtifactVersion v2, Document doc1, Document doc2) {
        WsdlDiff diff = new WsdlDiff();
        // TODO - get a reference to the registry
        WSDLLocator l = new RegistryWSDLLocator();
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
            return "Version " + v2.getVersion() + ". There were no structural changes since the previous version.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Version ")
          .append(v2.getVersion())
          .append(". The following changes since the previous version. <ul>");
        for (String c : changes) {
            sb.append("<li>")
              .append(c)
              .append("</li>");
        }
        sb.append("</ul>");
        
        return sb.toString();
    }

    public Object read(InputStream stream) throws IOException {
        try {
            return DOMUtils.readXml(stream);
        } catch (SAXException e) {
            IOException e2 = new IOException("Could not read XML.");
            e2.initCause(e);
            throw e2;
        } catch (ParserConfigurationException e) {
            IOException e2 = new IOException("Could not read XML.");
            e2.initCause(e);
            throw e2;
        }
    }

    public void write(Object o, OutputStream stream) throws IOException {
        try {
            DOMUtils.writeXml((Node) o, stream);
        } catch (TransformerException e) {
            IOException e2 = new IOException("Could not write XML.");
            e2.initCause(e);
            throw e2;
        }
    }

}
