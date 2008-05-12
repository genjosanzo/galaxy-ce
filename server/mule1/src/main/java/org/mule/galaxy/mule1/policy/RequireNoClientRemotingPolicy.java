package org.mule.galaxy.mule1.policy;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Registry;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.ArtifactPolicy;
import org.mule.galaxy.util.Constants;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

public class RequireNoClientRemotingPolicy implements ArtifactPolicy
{
    public static final String ID = "RequireNoClientRemotingPolicy";

    protected XPathFactory factory = XPathFactory.newInstance();
    private XPathExpression xpath;

    public RequireNoClientRemotingPolicy() throws XPathExpressionException {
        super();

        xpath = factory.newXPath().compile("/mule-configuration/mule-environment-properties/@serverUrl = ''");
    }

    public boolean applies(Artifact a) {
        return Constants.MULE_QNAME.equals(a.getDocumentType());
    }

    public String getDescription() {
        return "Requires that Client Remote Dispatcher support is not enabled.";
    }

    public String getId() {
        return ID;
    }

    public String getName() {
        return "Mule: Require No Client Remoting Policy";
    }

    public Collection<ApprovalMessage> isApproved(Artifact a, ArtifactVersion previous, ArtifactVersion next) {
        try {

            if(!(Boolean) xpath.evaluate((Document) next.getData(), XPathConstants.BOOLEAN))
            {
                return Arrays.asList(new ApprovalMessage("The Mule configuration has the serverUrl set for client remoting. set /mule-configuration/mule-environment-properties/@serverUrl to \"\"", false));
            }

        } catch (XPathExpressionException e) {
            return Arrays.asList(new ApprovalMessage("Could not evaluate Mule configuration: " + e.getMessage(), false));
        }
        return Collections.emptyList();
    }

    public void setRegistry(Registry registry) {

    }

}