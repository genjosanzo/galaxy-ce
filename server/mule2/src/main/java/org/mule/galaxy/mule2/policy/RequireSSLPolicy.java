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

public class RequireSSLPolicy extends AbstractMulePolicy
{
    public static final String ID = "RequireSSLPolicy";

    protected XPathFactory factory = XPathFactory.newInstance();
    private XPathExpression xpath;

    public RequireSSLPolicy() throws XPathExpressionException {
        super();

        xpath = factory.newXPath().compile("/mule-configuration//endpoint[starts-with(@address, 'http:') or starts-with(@address, 'tcp:')]");
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

    public Collection<ApprovalMessage> isApproved(Item item) {
        try {
            Artifact artifact = (Artifact) item.getProperty("artifact");
            NodeList result = (NodeList) xpath.evaluate((Document) artifact.getData(), XPathConstants.NODESET);

            if (result.getLength() > 0) {
                return Arrays.asList(new ApprovalMessage("The Mule configuration contains unsecured HTTP endpoints!", false));
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
