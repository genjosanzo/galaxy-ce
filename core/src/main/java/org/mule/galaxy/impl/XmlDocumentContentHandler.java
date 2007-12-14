package org.mule.galaxy.impl;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathFactory;

import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.XmlContentHandler;
import org.mule.galaxy.util.DOMUtils;
import org.mule.galaxy.util.QNameUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.xml.sax.SAXException;

public class XmlDocumentContentHandler extends AbstractContentHandler implements XmlContentHandler {

    private MimeType primaryContentType;

    protected XPathFactory factory = XPathFactory.newInstance();
    
    public XmlDocumentContentHandler() throws MimeTypeParseException {
        this(true);
    }

    protected XmlDocumentContentHandler(boolean init) throws MimeTypeParseException {
        super();
        if (init) {
            primaryContentType = new MimeType("application/xml");
            supportedContentTypes.add(primaryContentType);
            supportedContentTypes.add(new MimeType("application/xml"));
            supportedTypes.add(Document.class);
        }
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

}
