package org.mule.galaxy.impl;

import javax.xml.namespace.QName;

import org.mule.galaxy.Index;
import org.mule.galaxy.Registry;
import org.mule.galaxy.util.Constants;

public class InitialIndexBuilder {
    private Registry registry;

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }
    
    public void initialize() throws Exception {
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
}
