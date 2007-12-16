package org.mule.galaxy.util;

import javax.xml.namespace.QName;

public interface Constants {
    QName WSDL_DEFINITION_QNAME = new QName("http://schemas.xmlsoap.org/wsdl/", 
                                      "definitions");

    QName MULE_QNAME = new QName("http://www.mulesource.org/schema/mule/core/2.0", "mule");
    
    QName SCHEMA_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "schema");
    
    QName POLICY_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/09/policy", "Policy");

    QName POLICY_2006_QNAME = new QName("http://www.w3.org/2006/07/ws-policy", "Policy");
    
    QName XSLT_QNAME = new QName("http://www.w3.org/1999/XSL/Transform", "stylesheet");
    
}
