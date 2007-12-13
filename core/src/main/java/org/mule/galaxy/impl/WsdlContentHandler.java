package org.mule.galaxy.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.wsdl.Definition;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLLocator;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.GalaxyIOException;
import org.mule.galaxy.Registry;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.XmlContentHandler;
import org.mule.galaxy.util.Constants;
import org.mule.galaxy.util.QNameUtil;
import org.mule.galaxy.wsdl.diff.DifferenceEvent;
import org.mule.galaxy.wsdl.diff.DifferenceListener;
import org.mule.galaxy.wsdl.diff.WsdlDiff;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WsdlContentHandler extends AbstractContentHandler implements XmlContentHandler {

    private WSDLFactory factory;
    private MimeType primaryContentType;
    
    public WsdlContentHandler() throws WSDLException, MimeTypeParseException {
        super();
        factory = WSDLFactory.newInstance();
        
        primaryContentType = new MimeType("application/wsdl+xml");
        supportedContentTypes.add(primaryContentType);
        supportedContentTypes.add(new MimeType("text/wsdl+xml"));
        
        supportedTypes.add(Definition.class);
    }

    public Document getDocument(Object o) throws IOException {
        Definition d = (Definition) o;
        
        try {
            return factory.newWSDLWriter().getDocument(d);
        } catch (WSDLException e) {
            throw new GalaxyIOException(e);
        }
    }
    
    public MimeType getContentType(Object o) {
        return primaryContentType;
    }

    public String getName(Object o) {
        Definition d = (Definition) o;
        
        Object name = d.getExtensionAttribute(new QName("name"));
        
        if (name != null) {
            return name.toString();
        }
        
        return null;
    }

    public Object read(Source source, Workspace workspace) throws Exception {
        if (source instanceof DOMSource) {
            DOMSource ds = (DOMSource) source;
            
            return factory.newWSDLReader().readWSDL(new RegistryLocator(registry, workspace), 
                                                    (Element) ds.getNode());
        }
        
        throw new UnsupportedOperationException();
    }

    public Object read(InputStream stream, Workspace workspace) throws IOException {
        try {
            WSDLReader reader = factory.newWSDLReader();
            
            return reader.readWSDL(new RegistryLocator(stream, registry, workspace));
        } catch (WSDLException e) {
            throw new GalaxyIOException(e);
        }
    }

    public void write(Object o, OutputStream stream) throws IOException {
        // TODO Auto-generated method stub
        
    }
    
    public QName getDocumentType(Object o) {
        return Constants.WSDL_DEFINITION_QNAME;
    }

    @Override
    public void addMetadata(ArtifactVersion v) {
        super.addMetadata(v);
        
        Definition d = (Definition) v.getData();
        
        List<QName> svcNames = new ArrayList<QName>();
        Map svcs = d.getAllServices();
        if (svcs != null) {
            for (Object sObj : svcs.values()) {
                Service svc = (Service) sObj;
             
                svcNames.add(svc.getQName());
            }
        }
        v.setProperty("services", svcNames);
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
