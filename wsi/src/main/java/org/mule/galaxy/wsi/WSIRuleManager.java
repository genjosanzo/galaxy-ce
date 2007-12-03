package org.mule.galaxy.wsi;

import java.util.List;

public interface WSIRuleManager {
    List<WSIRule> getRules(String version);
    
    String getDescription(String ruleNumber);
}
