package org.mule.galaxy.wsi.wsdl;

import org.mule.galaxy.wsi.WSIRule;

/**
 * R2028, R2029
 */
public class WsdlSchemaValidationRule implements WSIRule {

    public String[] getRuleNames() {
        return new String[] { "R2028", "R2029" };
    }

}
