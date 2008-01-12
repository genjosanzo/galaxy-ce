package org.mule.galaxy.policy.mule;

import java.util.Arrays;
import java.util.Collection;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Registry;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.ArtifactPolicy;
import org.mule.galaxy.util.Constants;

public class RequireSslPolicy implements ArtifactPolicy {
    public static final String ID = "RequireSslPolicy";
    
    protected XPathFactory factory = XPathFactory.newInstance();
    private XPathExpression xpath;

    public RequireSslPolicy() throws XPathExpressionException {
        super();

        xpath = factory.newXPath().compile("/mule-configuration//endpoint[starts-with(@address, 'http:')]");
    }

    public boolean applies(Artifact a) {
        return Constants.MULE_QNAME.equals(a.getDocumentType());
    }

    public String getDescription() {
        return "Requires all HTTP endpoints to be SSL enabled.";
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
