package org.mule.galaxy.policy.wsdl;

import java.util.ArrayList;
import java.util.List;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.policy.Approval;
import org.mule.galaxy.policy.ArtifactPolicy;
import org.mule.galaxy.wsi.Message;
import org.mule.galaxy.wsi.WSIRule;
import org.mule.galaxy.wsi.WSIRuleManager;
import org.mule.galaxy.wsi.impl.WSIRuleManagerImpl;
import org.mule.galaxy.wsi.wsdl.AssertionResult;
import org.mule.galaxy.wsi.wsdl.ValidationResult;
import org.mule.galaxy.wsi.wsdl.WsdlRule;

import org.w3c.dom.Document;

public class BasicProfilePolicy implements ArtifactPolicy {

    public static final String WSI_BP_1_1_WSDL = "WSI_BP_1_1_WSDL";
    private WSIRuleManager wsiManager;
    private List<WSIRule> rules;

    public String getId() {
        return WSI_BP_1_1_WSDL;
    }

    public BasicProfilePolicy() throws Exception {
        super();
        
        wsiManager = new WSIRuleManagerImpl();
        rules = wsiManager.getRules(WSIRuleManager.WSI_BP_1_1);
    }

    public String getDescription() {
        return "Ensures that WSDLs meet the criteria outlined by the WS-I BasicProfile.";
    }

    public String getName() {
        return "WS-I BasicProfile 1.1 WSDL Compliance";
    }

    public Approval isApproved(Artifact a, ArtifactVersion previous, ArtifactVersion next) {
        Approval approval = new Approval();
        
        List<ValidationResult> results = new ArrayList<ValidationResult>();
        boolean failed = false;
        for (WSIRule r : rules) {
            if (r instanceof WsdlRule) {
                WsdlRule wr = (WsdlRule) r;
                
                ValidationResult vr = wr.validate((Document) next.getData(), null);
                results.add(vr);
                
                if (!failed && vr.isFailed()) {
                    failed = true;
                }
            }
        }
        
        approval.setApproved(!failed);
        
        for (ValidationResult vr : results) {
            for (AssertionResult ar : vr.getAssertionResults()) {
                StringBuilder sb = new StringBuilder();
            
                sb.append(ar.getName())
                  .append(": ")
                  .append(wsiManager.getDescription(ar.getName()));
                
                if (ar.getMessages().size() > 0) {
                    for (Message m : ar.getMessages()) {
                        sb.append("\n")
                          .append(m.getText());
                        
                        if (m.getLineNumber() > -1) {
                            sb.append(" (Line ")
                              .append(m.getLineNumber())
                              .append(")");
                        }
                    }
                }
                
                approval.getMessages().add(sb.toString());
            }
        }
        return approval;
    }

}
