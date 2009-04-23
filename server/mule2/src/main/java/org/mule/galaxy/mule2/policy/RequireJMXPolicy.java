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

public class RequireJMXPolicy extends AbstractMulePolicy
{
    public static final String ID = "RequireJMXPolicy";

    protected XPathFactory factory = XPathFactory.newInstance();
    private XPathExpression xpath;

    public RequireJMXPolicy() throws XPathExpressionException {
        super();
        xpath = factory.newXPath().compile("/mule-configuration/agents/agent/@className = 'org.mule.management.agents.JmxAgent'");
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

    public Collection<ApprovalMessage> isApproved(Item item) {
        try {
            Artifact artifact = (Artifact) item.getProperty("artifact");

            if(!(Boolean) xpath.evaluate((Document) artifact.getData(), XPathConstants.BOOLEAN))
            {
                return Arrays.asList(new ApprovalMessage("The Mule configuration does not have JMX support Enabled", false));
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