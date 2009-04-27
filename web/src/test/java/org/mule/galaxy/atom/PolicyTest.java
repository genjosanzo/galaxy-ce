package org.mule.galaxy.atom;


import java.io.IOException;
import java.io.InputStream;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.axiom.om.util.Base64;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.policy.PolicyManager;
import org.mule.galaxy.policy.wsdl.BasicProfilePolicy;
import org.mule.galaxy.test.AbstractAtomTest;
import org.mule.galaxy.util.DOMUtils;
import org.springmodules.jcr.JcrCallback;
import org.w3c.dom.Node;

public class PolicyTest extends AbstractAtomTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        JcrUtil.doInTransaction(sessionFactory, new JcrCallback() {

            public Object doInJcr(Session session) throws IOException, RepositoryException {

                try {
                    login("admin", "admin");
                    org.mule.galaxy.Item workspace = registry.getItems().iterator().next();
                    
                    PolicyManager policyManager = (PolicyManager) getApplicationContext().getBean("policyManager");
                    LifecycleManager lifecycleManager = workspace.getLifecycleManager();
                    
                    // activate all polices
                    
                    policyManager.setActivePolicies(lifecycleManager.getDefaultLifecycle(), 
                                                    policyManager.getPolicy(BasicProfilePolicy.WSI_BP_1_1_WSDL));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                
                return null;
            }
            
        });
    }

    public void testAddWsdl() throws Exception {
        // activate policies so our entry addition will fail.
        
        AbderaClient client = new AbderaClient(abdera);
        RequestOptions defaultOpts = client.getDefaultRequestOptions();
        defaultOpts.setAuthorization("Basic " + Base64.encode("admin:admin".getBytes()));
        
        String colUri = "http://localhost:9002/api/registry";
        
        // Testing of entry creation
        System.out.println("Creating Entry from a WSDL " + colUri.toString());
        
        RequestOptions opts = new RequestOptions();
        opts.setContentType("application/xml; charset=utf-8");
        opts.setSlug("hello_world.wsdl");
        opts.setHeader("X-Artifact-Version", "0.1");
        opts.setAuthorization(defaultOpts.getAuthorization());
        
        ClientResponse res = client.post(colUri.toString() + "/Default%20Workspace", getWsdl(), opts);
        
        org.w3c.dom.Document doc = DOMUtils.readXml(res.getInputStream());
        org.w3c.dom.Element root = doc.getDocumentElement();
        assertEquals("html", root.getLocalName());
        
        Node body = DOMUtils.getChild(root, "body");
        assertNotNull(body);
        
        Node artifact = DOMUtils.getChild(body, "div");
        assertNotNull(artifact);
        
        Node failure = DOMUtils.getChild(artifact, "div");
        assertNotNull(failure);
        
        String content = DOMUtils.getContent(failure);
        
        assertTrue(content.indexOf("WS-I BasicProfile") != -1);
        
        assertEquals(400, res.getStatus());
        res.release();
        
    }
    
    private InputStream getWsdl() {
        return getClass().getResourceAsStream("/wsdl/wsi/soapbinding/r2701.wsdl");
    }
}
