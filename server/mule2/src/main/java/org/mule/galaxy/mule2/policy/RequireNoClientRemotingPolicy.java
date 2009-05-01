package org.mule.galaxy.mule2.policy;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.mule.galaxy.Item;
import org.mule.galaxy.Registry;
import org.mule.galaxy.artifact.Artifact;
import org.mule.galaxy.policy.ApprovalMessage;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class RequireNoClientRemotingPolicy extends AbstractMulePolicy
{
    public static final String ID = "RequireNoClientRemotingPolicy";

    protected XPathFactory factory = XPathFactory.newInstance();
    private XPathExpression xpath;

    public RequireNoClientRemotingPolicy() throws XPathExpressionException {
        super();

        xpath = factory.newXPath().compile("//*[local-name()='remote-dispatcher-agent']");
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
            Artifact artifact = (Artifact) item.getProperty("artifact");

            NodeList result = (NodeList) xpath.evaluate((Document) artifact.getData(), XPathConstants.NODESET);

            if (result.getLength() > 0) {
                return Arrays.asList(new ApprovalMessage("The Mule configuration has the serverUrl set for client remoting. set /mule-configuration/mule-environment-properties/@serverUrl to \"\"", false));
            }
        } catch (XPathExpressionException e) {
            return Arrays.asList(new ApprovalMessage("Could not evaluate Mule configuration: " + e.getMessage(), false));
        } catch (IOException e) {
        }
        return Collections.emptyList();
    }

    public void setRegistry(Registry registry) {

    }

}