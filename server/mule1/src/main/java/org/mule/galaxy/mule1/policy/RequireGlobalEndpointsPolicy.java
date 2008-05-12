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
import org.w3c.dom.NodeList;

public class RequireGlobalEndpointsPolicy implements ArtifactPolicy
{
    public static final String ID = "RequireGlobalEPPolicy";

    protected XPathFactory factory = XPathFactory.newInstance();
    private XPathExpression xpath;

    public RequireGlobalEndpointsPolicy() throws XPathExpressionException {
        super();

        xpath = factory.newXPath().compile("/mule-configuration/*/mule-descriptor/endpoint");
    }

    public boolean applies(Artifact a) {
        return Constants.MULE_QNAME.equals(a.getDocumentType());
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

    public Collection<ApprovalMessage> isApproved(Artifact a, ArtifactVersion previous, ArtifactVersion next) {
        try {

            NodeList result = (NodeList) xpath.evaluate((Document) next.getData(), XPathConstants.NODESET);

            if (result.getLength() > 0) {
                return Arrays.asList(new ApprovalMessage("The Mule configuration contains local endpoints!", false));
            }

        } catch (XPathExpressionException e) {
            return Arrays.asList(new ApprovalMessage("Could not evaluate Mule configuration: " + e.getMessage(), false));
        }
        return Collections.emptyList();
    }

    public void setRegistry(Registry registry) {

    }

}