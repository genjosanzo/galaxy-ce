package org.mule.galaxy.cxf;

import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;

import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.axiom.om.util.Base64;
import org.apache.cxf.BusFactory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.ws.policy.WSPolicyFeature;
import org.apache.hello_world_soap_http.Greeter;
import org.apache.hello_world_soap_http.SOAPService;
import org.apache.neethi.Policy;
import org.mule.galaxy.test.AbstractAtomTest;

public class CxfPolicyTest extends AbstractAtomTest {

    @Override
    protected void setUp() throws Exception {
        System.setProperty("javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema",
        "");

        super.setUp();
    }

    public void testPolicyLoadingWithConfig() throws Exception {
        System.setProperty("cxf.config.file", "cxf-spring.xml");
        
        testPolicyLoading();
    }
    
    public void testPolicyLoading() throws Exception {
        AbderaClient client = new AbderaClient(abdera);
        
        RequestOptions opts = new RequestOptions();
        opts.setContentType("application/xml; charset=utf-8");
        opts.setSlug("wsrm-policy.wsdl");
        opts.setHeader("X-Artifact-Version", "0.1");
        opts.setHeader("X-Workspace", "Default Workspace");
        opts.setAuthorization("Basic " + Base64.encode("admin:admin".getBytes()));
        
        ClientResponse res = client.post("http://localhost:9002/api/registry", 
                                         getClass().getResourceAsStream("/policies/wsrm-policy.xml"), 
                                         opts);
        assertEquals(201, res.getStatus());
        
        // Test service creation with policy
        EndpointImpl ep = (EndpointImpl) Endpoint.create(new org.apache.hello_world_soap_http.GreeterImpl());
        
        WSPolicyFeature policyFeature = new WSPolicyFeature();
        policyFeature.setNamespace("http://www.w3.org/2006/07/ws-policy");
        ep.getFeatures().add(policyFeature);

        GalaxyFeature feature = new GalaxyFeature();
        feature.setUsername("admin");
        feature.setPassword("admin");
        
        String url = "http://localhost:9002/api/registry?q=" 
            + UrlEncoding.encode("select artifact where documentType = " +
            		"{http://www.w3.org/2006/07/ws-policy}Policy");
        
        feature.getPolicyQueries().add(url);
        ep.getFeatures().add(feature);
        
        ep.publish("http://localhost:9003/greeter");
        
        // test to see if policy was loaded in
        ServiceInfo si = ep.getService().getServiceInfos().iterator().next();
        List<Policy> policies = si.getExtensors(Policy.class);
        assertNotNull(policies);
        assertEquals(1, policies.size());
        
        // Test client setup
        URL wsdlUrl = getClass().getResource("/wsdl/hello_world.wsdl");
        assertNotNull(url);
        SOAPService service = new SOAPService(wsdlUrl, new QName("http://apache.org/hello_world_soap_http", 
                                                                 "SOAPService"));
        Greeter greeter = service.getSoapPort();
        
        ((BindingProvider) greeter).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                                                            "http://localhost:9003/greeter");
        
        Client c = ClientProxy.getClient(greeter);
        feature.initialize(c, BusFactory.getDefaultBus());
        
        si = c.getEndpoint().getService().getServiceInfos().iterator().next();
        policies = si.getExtensors(Policy.class);
        assertNotNull(policies);
        assertEquals(1, policies.size());
        
//        String sayHi = greeter.sayHi();
//        System.out.println(sayHi);
    }
}
