package org.mule.galaxy.wsi.wsdl;

import javax.wsdl.Definition;

import org.mule.galaxy.wsi.WSIRule;
import org.w3c.dom.Document;

public interface WsdlRule extends WSIRule {
    ValidationResult validate(Document document, Definition def);
}
