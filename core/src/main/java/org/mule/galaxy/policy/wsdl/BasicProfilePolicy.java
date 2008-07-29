package org.mule.galaxy.policy.wsdl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Item;
import org.mule.galaxy.Registry;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.impl.RegistryLocator;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.Policy;
import org.mule.galaxy.util.Constants;
import org.mule.galaxy.wsi.Message;
import org.mule.galaxy.wsi.WSIRule;
import org.mule.galaxy.wsi.WSIRuleManager;
import org.mule.galaxy.wsi.impl.WSIRuleManagerImpl;
import org.mule.galaxy.wsi.wsdl.AssertionResult;
import org.mule.galaxy.wsi.wsdl.ValidationResult;
import org.mule.galaxy.wsi.wsdl.WsdlRule;

import org.w3c.dom.Document;

public class BasicProfilePolicy implements Policy
{

    public static final String WSI_BP_1_1_WSDL = "WSI_BP_1_1_WSDL";
    private WSIRuleManager wsiManager;
    private List<WSIRule> rules;
    private WSDLReader wsdlReader;
    private Registry registry;
    
    public String getId() {
        return WSI_BP_1_1_WSDL;
    }

    public BasicProfilePolicy() throws Exception {
        super();
        
        wsiManager = new WSIRuleManagerImpl();
        rules = wsiManager.getRules(WSIRuleManager.WSI_BP_1_1);
        wsdlReader = WSDLFactory.newInstance().newWSDLReader();
    }
    
    public boolean applies(Item item) {
        return item instanceof ArtifactVersion && 
            Constants.WSDL_DEFINITION_QNAME.equals(((Artifact)item.getParent()).getDocumentType());
    }

    public String getDescription() {
        return "Ensures that WSDLs meet the criteria outlined by the WS-I BasicProfile.";
    }

    public String getName() {
        return "WSDL: WS-I BasicProfile 1.1 Compliance";
    }

    public Collection<ApprovalMessage> isApproved(Item item) {
        ArrayList<ApprovalMessage> messages = new ArrayList<ApprovalMessage>();
        ArtifactVersion v = (ArtifactVersion) item;
        List<ValidationResult> results = new ArrayList<ValidationResult>();
        for (WSIRule r : rules) {
            if (r instanceof WsdlRule) {
                WsdlRule wr = (WsdlRule) r;
                
                Document doc = (Document) v.getData();
                Definition def = null;
                try {
                    def = wsdlReader.readWSDL(new RegistryLocator(registry, (Workspace)v.getParent().getParent()), 
                                              doc.getDocumentElement());
                } catch (Exception e) {
                    // Ignore - its not parsable
                }
                
                ValidationResult vr = wr.validate(doc, def);
                results.add(vr);
            }
        }
        
        for (ValidationResult vr : results) {
            for (AssertionResult ar : vr.getAssertionResults()) {
                StringBuilder sb = new StringBuilder();
            
                sb.append("WS-I BasicProfile 1.1 Assertion ")
                  .append(ar.getName())
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
                
                messages.add(new ApprovalMessage(sb.toString(), !vr.isFailed()));
            }
        }
        return messages;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

}
