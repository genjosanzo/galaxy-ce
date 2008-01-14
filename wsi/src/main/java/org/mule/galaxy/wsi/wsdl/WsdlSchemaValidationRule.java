package org.mule.galaxy.wsi.wsdl;

import java.io.IOException;

import javax.wsdl.Definition;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;

import org.xml.sax.SAXException;

/**
 * R2028, R2029 - validate the WSDL via schemas.
 */
public class WsdlSchemaValidationRule extends AbstractWsdlRule {
    

    private SchemaFactory schemaFactory;
    private Schema wsdlSchema;

    public WsdlSchemaValidationRule() throws SAXException {
        super("R2028");
        
        schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
        Source wsdlSchemaSource = new StreamSource(getClass().getResourceAsStream("/org/mule/galaxy/wsi/wsdl/wsdl-2004-08-24.xsd"));
        wsdlSchema = schemaFactory.newSchema(wsdlSchemaSource);
    }
    
    public ValidationResult validate(Document document, Definition def) {
        ValidationResult result = new ValidationResult();
        
        
        try {
            SchemaErrorHandler errorHandler = new SchemaErrorHandler("R2028");

            Validator wsdlValidator = wsdlSchema.newValidator();
            wsdlValidator.setErrorHandler(errorHandler);
            wsdlValidator.validate(new DOMSource(document));
            
            if (errorHandler.hasErrors()) {
                result.addAssertionResult(errorHandler.getAssertionResult());
            }
        } catch (SAXException e) {
            result.addAssertionResult(new AssertionResult("R2028", true, e.getMessage()));
        } catch (IOException e) {
            result.addAssertionResult(new AssertionResult("R2028", true, e.getMessage()));
        }
        
        return result;
    }
}
