package org.mule.galaxy.impl.content;


import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.XmlContentHandler;
import org.mule.galaxy.impl.MapNamespaceContext;
import org.mule.galaxy.util.DOMUtils;
import org.mule.galaxy.util.QNameUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlDocumentContentHandler extends AbstractContentHandler implements XmlContentHandler {

    protected XPathFactory factory = XPathFactory.newInstance();

    protected XPath xpath;

    protected HashMap<String, String> namespaces;

    protected List<XPathExpression> imports = new ArrayList<XPathExpression>();
    
    protected MimeType primaryContentType;

    protected Set<QName> supportedDocumentTypes = new HashSet<QName>();
    
    public XmlDocumentContentHandler() throws MimeTypeParseException {
        this(true);
        supportedFileExtensions.add("xml");
    }

    protected XmlDocumentContentHandler(boolean init) throws MimeTypeParseException {
        super();
        if (init) {
            primaryContentType = new MimeType("application/xml");
            supportedContentTypes.add(primaryContentType);
            supportedContentTypes.add(new MimeType("application/xml"));
            supportedTypes.add(Document.class);
        }
        
        xpath = factory.newXPath();
        namespaces = new HashMap<String, String>();
        xpath.setNamespaceContext(new MapNamespaceContext(namespaces));
    }

    public Set<QName> getSupportedDocumentTypes() {
        return supportedDocumentTypes;
    }

    @Override
    public Set<Artifact> detectDependencies(Object o, Workspace w) {
        HashSet<Artifact> deps = new HashSet<Artifact>();
        try {
            for (XPathExpression expr : imports) {
                NodeList result = (NodeList) expr.evaluate((Document) o, 
                                                           XPathConstants.NODESET);
                
                for (int i = 0; i < result.getLength(); i++) {
                    Node item = result.item(i);
                    
                    String location = item.getNodeValue();
                    
                    Artifact a = registry.resolve(w, location);
                    if (a != null) {
                        deps.add(a);
                    }
                }
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        
        return deps;
    }
    
    @Override
    public void addMetadata(ArtifactVersion v) {
        super.addMetadata(v);
    }


    public Document getDocument(Object o) {
        return (Document) o;
    }

    public String getName(Object o) {
        return null;
    }

    public MimeType getContentType(Object o) {
        return primaryContentType;
    }

    public QName getDocumentType(Object o) {
        Document doc = (Document)o;
        
        return QNameUtil.getName(doc.getDocumentElement());
    }

    public Object read(InputStream stream, Workspace workspace) throws IOException {
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

    // TODO maybe should go, neither used nor overridden anywhere
    public Object read(Source source, Workspace workspace) throws Exception {
        throw new UnsupportedOperationException();
    }
}
