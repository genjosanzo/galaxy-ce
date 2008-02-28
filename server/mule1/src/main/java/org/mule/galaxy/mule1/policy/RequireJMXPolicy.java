package org.mule.galaxy.mule1.policy;


import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Registry;
import org.mule.galaxy.policy.ApprovalMessage;
import org.mule.galaxy.policy.ArtifactPolicy;
import org.mule.galaxy.util.Constants;

import java.util.Arrays;
import java.util.Collection;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

public class RequireJMXPolicy implements ArtifactPolicy
{
    public static final String ID = "RequireJMXPolicy";

    protected XPathFactory factory = XPathFactory.newInstance();
    private XPathExpression xpath;

    public RequireJMXPolicy() throws XPathExpressionException {
        super();


        xpath = factory.newXPath().compile("/mule-configuration/agents/agent/@className = 'org.mule.management.agents.JmxAgent'");
    }

    public boolean applies(Artifact a) {
        return Constants.MULE_QNAME.equals(a.getDocumentType());
    }

    public String getDescription() {
        return "Requires that Jmx Monitoring is enabled enabled.";
    }

    public String getId() {
        return ID;
    }

    public String getName() {
        return "Mule: Require JMX Policy";
    }

    public Collection<ApprovalMessage> isApproved(Artifact a, ArtifactVersion previous, ArtifactVersion next) {
        try {

            if(!(Boolean) xpath.evaluate((Document) next.getData(), XPathConstants.BOOLEAN))
            {
                return Arrays.asList(new ApprovalMessage("The Mule configuration does not have JMX support Enabled", false));
            }

        } catch (XPathExpressionException e) {
            return Arrays.asList(new ApprovalMessage("Could not evaluate Mule configuration: " + e.getMessage(), false));
        }
        return null;
    }

    public void setRegistry(Registry registry) {

    }

}