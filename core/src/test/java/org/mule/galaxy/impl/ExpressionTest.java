package org.mule.galaxy.impl;

import java.util.Set;

import javax.xml.namespace.QName;

import org.mule.galaxy.AbstractGalaxyTest;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.Index;
import org.mule.galaxy.util.Constants;

public class ExpressionTest extends AbstractGalaxyTest {
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-test.xml" };
        
    }
    public void testExpressions() throws Exception {
        /**
         * Creates a document like:
         * 
         * <service>{http://acme.com}EchoService</service>
         * <service>{http://acme.com}EchoService2</service>
         * etc.
         */
        String exp = 
            "<values>{" +
            "let $ns = $document/wsdl:definition[@targetNamespace]" +
            "for ($name in $document//wsdl:service[@name])" +
            "    return <value>\\{$ns\\}$name</value>" +
            "}</values>";
       
        registry.registerIndex("wsdl.service", // index field name
                               "WSDL Service", // Display Name
                               Index.Language.XQUERY,
                               QName.class, // search input type
                               exp, // the xquery expression
                               Constants.WSDL_DEFINITION); // document QName which this applies to
        
        Set<Index> indices = registry.getIndices(Constants.WSDL_DEFINITION);
        assertNotNull(indices);
        assertEquals(1, indices.size());
        
        Index idx = indices.iterator().next();
        assertEquals("wsdl.service", idx.getId());
        assertEquals("WSDL Service", idx.getName());
        assertEquals(Index.Language.XQUERY, idx.getLanguage());
        assertEquals(QName.class, idx.getQueryType());
        assertEquals(exp, idx.getExpression());
        assertEquals(1, idx.getDocumentTypes().size());
        
        Set<Artifact> results = registry.search("//wsdl.service", 
                                                new QName("http://acme.com", "EchoService"));
        
    }
}
