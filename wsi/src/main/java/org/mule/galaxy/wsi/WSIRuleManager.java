package org.mule.galaxy.wsi;

import java.util.List;

public interface WSIRuleManager {
    
    String WSI_BP_1_1 = "1.1";
    
    List<WSIRule> getRules(String version);
    
    String getDescription(String ruleNumber);
}
