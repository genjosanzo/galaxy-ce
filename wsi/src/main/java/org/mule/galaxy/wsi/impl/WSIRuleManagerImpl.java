package org.mule.galaxy.wsi.impl;

import java.util.ArrayList;
import java.util.List;

import org.mule.galaxy.wsi.WSIRule;
import org.mule.galaxy.wsi.WSIRuleManager;
import org.mule.galaxy.wsi.wsdl.ImportUriRule;
import org.mule.galaxy.wsi.wsdl.NonEmptyWsdlLocationImportRule;
import org.mule.galaxy.wsi.wsdl.WsdlSchemaValidationRule;

public class WSIRuleManagerImpl implements WSIRuleManager {

    
    protected List<WSIRule> wsi11Rules = new ArrayList<WSIRule>();
    
    public WSIRuleManagerImpl() throws Exception {
        super();

        wsi11Rules.add(new WsdlSchemaValidationRule());
        wsi11Rules.add(new ImportUriRule());
        wsi11Rules.add(new NonEmptyWsdlLocationImportRule());
    }

    public String getDescription(String ruleNumber) {
        // TODO Auto-generated method stub
        return "";
    }

    public List<WSIRule> getRules(String version) {
        if (version == WSIRuleManager.WSI_BP_1_1) {
            return wsi11Rules;
        }
        throw new UnsupportedOperationException();
    }

}
