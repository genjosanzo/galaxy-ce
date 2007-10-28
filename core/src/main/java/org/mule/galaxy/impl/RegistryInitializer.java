package org.mule.galaxy.impl;

import javax.xml.namespace.QName;

import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.Dao;
import org.mule.galaxy.Index;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.util.Constants;

public class RegistryInitializer {
    private Registry registry;
    private Dao<ArtifactType> artifactTypeDao;
    
    public void setRegistry(Registry registry) {
        this.registry = registry;
    }
    
    public void initialize() throws Exception {
        
        createIndexes();
        createTypes();
    }

    private void createTypes() {
        artifactTypeDao.save(new ArtifactType("WS-Policy", "application/xml", Constants.POLICY_QNAME));
        artifactTypeDao.save(new ArtifactType("XML Schema", "application/xml", Constants.SCHEMA_QNAME));
        artifactTypeDao.save(new ArtifactType("WSDL", "application/wsdl+xml", Constants.WSDL_DEFINITION_QNAME));
        artifactTypeDao.save(new ArtifactType("Mule Configuration", "application/mule+xml", Constants.MULE_QNAME));
    }

    private void createIndexes() throws RegistryException {
        /**
         * Creates a document like:
         * <values>
         * <value>{http://acme.com}EchoService</value>
         * <value>{http://acme.com}EchoService2</value>
         * </values>
         * etc.
         */
        String exp = 
            "declare namespace wsdl=\"http://schemas.xmlsoap.org/wsdl/\";\n" +
            "declare variable $document external;\n" +
            "" +
            "for $svc in $document//wsdl:service\n" +
            "let $ns := $document/wsdl:definition/@targetNamespace\n" +
            "    return <value>{data($svc/@name)}</value>\n" +
            "";
       
        registry.registerIndex("wsdl.service", // index field name
                               "WSDL Service", // Display Name
                               Index.Language.XQUERY,
                               QName.class, // search input type
                               exp, // the xquery expression
                               Constants.WSDL_DEFINITION_QNAME); // document QName which this applies to
              
        // Read <mule:service> elements
        exp = 
            "declare namespace mule=\"http://www.mulesource.org/schema/mule/core/2.0\";\n" +
            "declare variable $document external;\n" +
            "" +
            "for $svc in $document//mule:service\n" +
            "    return <value>{data($svc/@name)}</value>\n" +
            "";
       
        registry.registerIndex("mule.service", // index field name
                               "Mule Service", // Display Name
                               Index.Language.XQUERY,
                               String.class, // search input type
                               exp, // the xquery expression
                               Constants.MULE_QNAME); // document QName which this applies to
    }

    public void setArtifactTypeDao(Dao<ArtifactType> artifactTypeDao) {
        this.artifactTypeDao = artifactTypeDao;
    }
    
}
