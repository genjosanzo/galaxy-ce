package org.mule.galaxy.wsi.wsdl;

import java.io.IOException;

import javax.wsdl.Definition;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.xerces.jaxp.validation.XMLSchemaFactory;

import org.w3c.dom.Document;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * R2028, R2029 - validate the WSDL via schemas.
 */
public class WsdlSchemaValidationRule implements WsdlRule {
    

    private SchemaFactory schemaFactory;
    private Schema wsdlSchema;
    private Schema wsdlSoapSchema;

    public WsdlSchemaValidationRule() throws SAXException {
        super();
        // Force Xerces!
        schemaFactory = new XMLSchemaFactory();
        Source wsdlSchemaSource = new StreamSource(getClass().getResourceAsStream("/org/mule/galaxy/wsi/wsdl/wsdl-2004-08-24.xsd"));
        wsdlSchema = schemaFactory.newSchema(wsdlSchemaSource);
        
        Source wsdlSoapSchemaSource = new StreamSource(getClass().getResourceAsStream("/org/mule/galaxy/wsi/wsdl/wsdl-2004-08-24.xsd"));
        wsdlSoapSchema = schemaFactory.newSchema(wsdlSoapSchemaSource);
    }

    public String[] getRuleNames() {
        return new String[] {"R2028", "R2029"};
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
            
            errorHandler = new SchemaErrorHandler("R2029");
            Validator wsdlSoapValidator = wsdlSoapSchema.newValidator();
            wsdlSoapValidator.setErrorHandler(errorHandler);
            wsdlSoapValidator.validate(new DOMSource(document));
        } catch (SAXException e) {
            result.addAssertionResult(new AssertionResult("R2028", true, e.getMessage()));
        } catch (IOException e) {
            result.addAssertionResult(new AssertionResult("R2028", true, e.getMessage()));
        }
        
        return result;
    }


    private final class SchemaErrorHandler implements ErrorHandler {
        private String code;
        private AssertionResult result;

        public SchemaErrorHandler(String code) {
            this.code = code;
        }

        public void error(SAXParseException exception) throws SAXException {
            getAssertionResult().addMessage(exception.getMessage(),
                                            exception.getSystemId(),
                                            exception.getLineNumber(), 
                                            exception.getColumnNumber());
        }

        public void fatalError(SAXParseException exception) throws SAXException {
            getAssertionResult().addMessage(exception.getMessage(),
                                            exception.getSystemId(),
                                            exception.getLineNumber(), 
                                            exception.getColumnNumber());
        }

        public void warning(SAXParseException exception) throws SAXException {
            // getAssertionResult().getMessages().add(exception.getMessage());
        }

        public boolean hasErrors() {
            return result != null;
        }

        public AssertionResult getAssertionResult() {
            if (result == null) {
                result = new AssertionResult(code, true);
            }
            return result;
        }
    }
}
