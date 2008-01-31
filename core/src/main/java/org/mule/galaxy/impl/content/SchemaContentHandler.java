package org.mule.galaxy.impl.content;

import java.io.IOException;
import java.io.OutputStream;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;

import org.mule.galaxy.api.XmlContentHandler;
import org.mule.galaxy.api.util.Constants;

import org.w3c.dom.Document;

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
        
        supportedFileExtensions.clear();
        supportedFileExtensions.add("xsd");
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
