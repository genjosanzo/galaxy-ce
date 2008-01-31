package org.mule.galaxy.mule1.policy;

import java.util.Arrays;
import java.util.Collection;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import org.mule.galaxy.api.Artifact;
import org.mule.galaxy.api.ArtifactVersion;
import org.mule.galaxy.api.Registry;
import org.mule.galaxy.api.util.Constants;
import org.mule.galaxy.api.policy.ApprovalMessage;
import org.mule.galaxy.api.policy.ArtifactPolicy;

public class RequireSslPolicy implements ArtifactPolicy {
    public static final String ID = "RequireSslPolicy";
    
    protected XPathFactory factory = XPathFactory.newInstance();
    private XPathExpression xpath;

    public RequireSslPolicy() throws XPathExpressionException {
        super();

        xpath = factory.newXPath().compile("/mule-configuration//endpoint[starts-with(@address, 'http:') or starts-with(@address, 'tcp:')]");
    }

    public boolean applies(Artifact a) {
        return Constants.MULE_QNAME.equals(a.getDocumentType());
    }

    public String getDescription() {
        return "Requires all HTTP and TCP endpoints to be SSL enabled.";
    }

    public String getId() {
        return ID;
    }

    public String getName() {
        return "Mule: Require SSL Policy";
    }

    public Collection<ApprovalMessage> isApproved(Artifact a, ArtifactVersion previous, ArtifactVersion next) {
        try {
            
            NodeList result = (NodeList) xpath.evaluate((Document) next.getData(), XPathConstants.NODESET);
            
            if (result.getLength() > 0) {
                return Arrays.asList(new ApprovalMessage("The Mule configuration contains unsecured HTTP endpoints!", false));
            }
            
        } catch (XPathExpressionException e) {
            return Arrays.asList(new ApprovalMessage("Could not evaluate Mule configuration: " + e.getMessage(), false));
        }
        return null;
    }

    public void setRegistry(Registry registry) {
        
    }

}
