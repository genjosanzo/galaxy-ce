package org.mule.galaxy.impl.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;

import net.sf.saxon.javax.xml.xquery.XQConnection;
import net.sf.saxon.javax.xml.xquery.XQDataSource;
import net.sf.saxon.javax.xml.xquery.XQException;
import net.sf.saxon.javax.xml.xquery.XQItem;
import net.sf.saxon.javax.xml.xquery.XQPreparedExpression;
import net.sf.saxon.javax.xml.xquery.XQResultSequence;
import net.sf.saxon.xqj.SaxonXQDataSource;
import org.apache.commons.lang.BooleanUtils;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.ContentHandler;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.XmlContentHandler;
import org.mule.galaxy.index.Index;
import org.mule.galaxy.index.IndexException;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.util.BundleUtils;
import org.mule.galaxy.util.DOMUtils;
import org.mule.galaxy.util.Message;
import org.mule.galaxy.util.QNameUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class XQueryIndexer extends AbstractIndexer {
    public static final String XQUERY_EXPRESSION = "expression";
    public static final String PROPERTY_NAME = "property";
    private static final ResourceBundle BUNDLE = BundleUtils.getBundle(XQueryIndexer.class);
    
    private XQDataSource ds = new SaxonXQDataSource();
    
    public void index(ArtifactVersion artifact, 
                      ContentHandler contentHandler, 
                      Index index) throws IOException,
        IndexException {

        try {
            XQConnection conn = ds.getConnection();
            
            String property = getValue(index.getConfiguration(), PROPERTY_NAME, new Message("NO_PROPERTY", BUNDLE));
            XQPreparedExpression ex = conn.prepareExpression(getValue(index.getConfiguration(), XQUERY_EXPRESSION, new Message("NO_XQUERY", BUNDLE)));
            XmlContentHandler ch = (XmlContentHandler) contentHandler;
            Document doc = ch.getDocument(artifact.getData());
            ex.bindNode(new QName("document"), doc, null);
            
            XQResultSequence result = ex.executeQuery();
            
            List<Object> results = new ArrayList<Object>();
            
            boolean visible = true;
            
            if (result.next()) {
                XQItem item = result.getItem();
    
                org.w3c.dom.Node values = item.getNode();

                // check locking & visibility
                NamedNodeMap atts = values.getAttributes();
                org.w3c.dom.Node visibleNode = atts.getNamedItem("visible");
                if (visibleNode != null) {
                    visible = BooleanUtils.toBoolean(visibleNode.getNodeValue());
                }
                
                // loop through the values
                Element value = DOMUtils.getFirstElement(values);
                while (value != null) {
                    String content = DOMUtils.getContent(value);

                    if (content == null) {
                        content = "";
                    }
                    
                    if (index.getQueryType().equals(QName.class)) {
                        results.add(QNameUtil.fromString(content)); 
                    } else {
                        results.add(content);
                    }
                    
                    value = (Element) DOMUtils.getNext(value, "value", org.w3c.dom.Node.ELEMENT_NODE);
                }
            }
            
            if (results.size() > 0) {
                artifact.setProperty(property, results);
                artifact.setLocked(property, true);
                artifact.setVisible(property, visible);
            } else {
                artifact.setProperty(property, null);
            }
            
            conn.close();
        } catch(PropertyException e) {
            // this will never happen
            throw new IndexException(e);
        } catch (XQException e) {
            throw new IndexException(e);
        } catch (PolicyException e) {
            throw new IndexException(e);
	}
    }

}
