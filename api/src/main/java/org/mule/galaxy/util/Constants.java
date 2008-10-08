package org.mule.galaxy.util;

import javax.xml.namespace.QName;

public final class Constants {

    private Constants()
    {
        // never instantiate
    }

    public static final String ATOM_NAMESPACE = "http://galaxy.mule.org/1.0";
    
    public static final QName WSDL_DEFINITION_QNAME = new QName("http://schemas.xmlsoap.org/wsdl/",
                                      "definitions");

    public static final QName MULE_QNAME = new QName("mule-configuration");

    public static final QName MULE2_0_QNAME = new QName("http://www.mulesource.org/schema/mule/core/2.0", "mule");
    
    public static final QName MULE2_1_QNAME = new QName("http://www.mulesource.org/schema/mule/core/2.1", "mule");
    
    public static final QName SPRING_QNAME = new QName("http://www.springframework.org/schema/beans", "beans");

    public static final QName SCHEMA_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "schema");

    public static final QName POLICY_QNAME = new QName("http://schemas.xmlsoap.org/ws/2004/09/policy", "Policy");

    public static final QName POLICY_2006_QNAME = new QName("http://www.w3.org/2006/07/ws-policy", "Policy");
    
    public static final QName XSLT_QNAME = new QName("http://www.w3.org/1999/XSL/Transform", "stylesheet");
    
}
