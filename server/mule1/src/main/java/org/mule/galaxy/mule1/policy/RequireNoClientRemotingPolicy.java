package org.mule.galaxy.mule1.policy;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Item;
import org.mule.galaxy.Registry;
import org.mule.galaxy.policy.ApprovalMessage;

import org.w3c.dom.Document;

public class RequireNoClientRemotingPolicy extends AbstractMulePolicy
{
    public static final String ID = "RequireNoClientRemotingPolicy";

    protected XPathFactory factory = XPathFactory.newInstance();
    private XPathExpression xpath;

    public RequireNoClientRemotingPolicy() throws XPathExpressionException {
        super();

        xpath = factory.newXPath().compile("/mule-configuration/mule-environment-properties/@serverUrl = ''");
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

    public Collection<ApprovalMessage> isApproved(Item item) {
        try {

            if(!(Boolean) xpath.evaluate((Document) ((ArtifactVersion) item).getData(), XPathConstants.BOOLEAN))
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