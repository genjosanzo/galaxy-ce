package org.mule.galaxy.web;

import org.gwtwidgets.server.spring.RPCServiceExporter;
import org.gwtwidgets.server.spring.RPCServiceExporterFactory;

public class GWTRPCServiceExporterFactory implements RPCServiceExporterFactory{

    public RPCServiceExporter create() {
        return new GWTRPCServiceExporter();
    }
 
}
