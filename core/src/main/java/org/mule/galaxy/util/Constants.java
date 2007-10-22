package org.mule.galaxy.util;

import javax.xml.namespace.QName;

public interface Constants {
    QName WSDL_DEFINITION_QNAME = new QName("http://schemas.xmlsoap.org/wsdl/", 
                                      "definitions");

    QName MULE_QNAME = new QName("http://www.mulesource.org/schema/mule/core/2.0", "mule");
}
