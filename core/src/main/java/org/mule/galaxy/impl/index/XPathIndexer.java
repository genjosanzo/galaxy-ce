package org.mule.galaxy.impl.index;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.XmlContentHandler;
import org.mule.galaxy.index.Index;
import org.mule.galaxy.index.IndexException;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.util.BundleUtils;
import org.mule.galaxy.util.DOMUtils;
import org.mule.galaxy.util.Message;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XPathIndexer extends AbstractIndexer {
    public static final String PROPERTY_NAME = "property";
    public static final String XPATH_EXPRESSION = "expression";
    private static XPathFactory factory = XPathFactory.newInstance();
    private static final ResourceBundle BUNDLE = BundleUtils.getBundle(XPathIndexer.class);

    public void index(ArtifactVersion jcrVersion, 
                      ContentHandler contentHandler, 
                      Index index) throws IOException, IndexException {
        Document document = ((XmlContentHandler)contentHandler).getDocument(jcrVersion.getData());

        XPath xpath = factory.newXPath();
        try {
            String property = getValue(index.getConfiguration(), PROPERTY_NAME, new Message("NO_PROPERTY", BUNDLE));
            XPathExpression expr = xpath.compile(getValue(index.getConfiguration(), XPATH_EXPRESSION, new Message("NO_XPATH", BUNDLE)));

            NodeList nodes = (NodeList)expr.evaluate(document, XPathConstants.NODESET);

            if (nodes.getLength() >= 1) {
                jcrVersion.setProperty(property, DOMUtils.getContent(nodes.item(0)));
                jcrVersion.setLocked(property, true);
            }
        } catch (XPathExpressionException e) {
            throw new IndexException(e);
        } catch (PropertyException e) {
            throw new IndexException(e);
        } catch (PolicyException e) {
            throw new IndexException(e);
	} catch (AccessException e) {
            throw new IndexException(e);
        }
    }
}
