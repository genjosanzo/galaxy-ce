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

public class RequireGlobalEndpointsPolicy extends AbstractMulePolicy
{
    public static final String ID = "RequireGlobalEPPolicy";

    protected XPathFactory factory = XPathFactory.newInstance();
    private XPathExpression inboundXPath;
    private XPathExpression outboundXPath;

    public RequireGlobalEndpointsPolicy() throws XPathExpressionException {
        super();

        inboundXPath = factory.newXPath().compile("//*[local-name()='service']//*[local-name()='inbound-endpoint'][not(ref)]");
        outboundXPath = factory.newXPath().compile("//*[local-name()='service']//*[local-name()='outbound-endpoint'][not(ref)]");
    }

    public String getDescription() {
        return "Requires all All Endpoints are defined as top level Endpoints";
    }

    public String getId() {
        return ID;
    }

    public String getName() {
        return "Mule: Require Non-Local Endpoints Policy";
    }

    public Collection<ApprovalMessage> isApproved(Item item) {
        try {
            Artifact artifact = (Artifact) item.getProperty("artifact");
            Document data = (Document) artifact.getData();

            NodeList result = (NodeList) outboundXPath.evaluate(data, XPathConstants.NODESET);

            if (result.getLength() > 0) {
                return Arrays.asList(new ApprovalMessage("The Mule configuration contains local endpoints!", false));
            }

            result = (NodeList) inboundXPath.evaluate(data, XPathConstants.NODESET);

            if (result.getLength() > 0) {
                return Arrays.asList(new ApprovalMessage("The Mule configuration contains local endpoints!", false));
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