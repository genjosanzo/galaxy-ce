package org.mule.galaxy.cxf;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.axiom.om.util.Base64;
import org.apache.cxf.Bus;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.ws.policy.PolicyBuilder;
import org.apache.cxf.ws.policy.PolicyEngine;
import org.apache.neethi.Policy;

import org.xml.sax.SAXException;

public class GalaxyFeature extends AbstractFeature {
    private static final Logger LOGGER = LogUtils.getL7dLogger(GalaxyFeature.class);
    
    protected AbderaClient client = new AbderaClient();

    protected List<String> policyQueries = new ArrayList<String>();
    
    protected String username;
    protected String password;

    protected RequestOptions opts;
    
    protected Collection<Policy> loadedPolicies = null;

    @Override
    public void initialize(Bus bus) {
        PolicyEngine pe = bus.getExtension(PolicyEngine.class);
        
        synchronized (pe) {
            pe.setEnabled(true);
        }
        
        loadPolicies(bus); 
    }

    private void loadPolicies(Bus bus) {
        opts = client.getDefaultRequestOptions();
        if (username != null) {
            String up = username + ":" + password;
            opts.setAuthorization("Basic " + Base64.encode(up.getBytes()));
        }
        
        if (loadedPolicies != null) {
            return;
        }
        
        if (policyQueries != null) {
            loadedPolicies = new ArrayList<Policy>();
            PolicyBuilder builder = bus.getExtension(PolicyBuilder.class); 
            
            for (String qstr : policyQueries) {
                List<Entry> entries = getQueriedEntries(qstr);
                LOGGER.log(Level.INFO, "Found " + entries.size() + " policy entries");
                for (Entry e : entries) {
                    String policyLink = e.getContentSrc().toString();
                    if (policyLink == null) {
                        throw new RuntimeException("Could not find valid content link for " + e.getTitle());
                    }
                    
                    LOGGER.log(Level.INFO, "Loading policy at " + policyLink);
                    ClientResponse res = client.get(policyLink, opts);
                    
                    // Use this as your handle to the mule configuration
                    
                    try {
                        InputStream is = res.getInputStream();
                        org.w3c.dom.Document policyDoc = DOMUtils.readXml(is);

                        Policy policy = builder.getPolicy(policyDoc.getDocumentElement());
                        LOGGER.log(Level.INFO, "Loaded policy " + policy.getId());
                        loadedPolicies.add(policy);
                    } catch (SAXException e1) {
                        throw new RuntimeException(e1);
                    } catch (IOException e1) {
                        throw new RuntimeException(e1);
                    } catch (ParserConfigurationException e1) {
                        throw new RuntimeException(e1);
                    }
                } 
            }
        }
    }
    
    @Override
    public void initialize(Client client, Bus bus) {
        Endpoint endpoint = client.getEndpoint();
        
        intializeEndpoint(endpoint, bus);
    }

    @Override
    public void initialize(Server server, Bus bus) {
        Endpoint endpoint = server.getEndpoint();
        
        intializeEndpoint(endpoint, bus);
    }

    private void intializeEndpoint(Endpoint endpoint, Bus bus) {
        loadPolicies(bus); 
        
        List<ServiceInfo> sis = endpoint.getService().getServiceInfos();
        for (ServiceInfo si : sis) {
            if (loadedPolicies != null) {
                for (Policy p : loadedPolicies) {
                    si.addExtensor(p);
                }
            }
        }
    }

    public List<Entry> getQueriedEntries(String qstr) {
        // GET a Feed with Mule Configurations which match the criteria
        ClientResponse res = client.get(qstr, opts);

        if (res.getStatus() >= 400) {
            throw new RuntimeException("Could not retrieve entries: " 
                                       + res.getStatusText() + " (" + res.getStatus() + ")");
        }
        Document<Feed> feedDoc = res.getDocument();
        // prettyPrint(feedDoc);
        return feedDoc.getRoot().getEntries();
    }
    
    public List<String> getPolicyQueries() {
        return policyQueries;
    }

    public void setPolicyQueries(List<String> policyQueries) {
        this.policyQueries = policyQueries;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
