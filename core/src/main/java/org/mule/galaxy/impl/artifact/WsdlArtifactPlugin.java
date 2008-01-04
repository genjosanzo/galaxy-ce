package org.mule.galaxy.impl.artifact;

import java.util.Collection;

import javax.wsdl.xml.WSDLLocator;
import javax.xml.namespace.QName;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.Index;
import org.mule.galaxy.util.Constants;
import org.mule.galaxy.view.Column;
import org.mule.galaxy.view.ColumnEvaluator;
import org.mule.galaxy.view.CustomArtifactTypeView;


import org.xml.sax.InputSource;

public class WsdlArtifactPlugin extends AbstractArtifactPlugin {

    public void initializeOnce() throws Exception {
        artifactTypeDao.save(new ArtifactType("WSDL Documents", "application/wsdl+xml", Constants.WSDL_DEFINITION_QNAME));
        
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
            "<values visible=\"false\"> {\n" +
            "for $svc in $document//wsdl:service\n" +
            "let $ns := $document/wsdl:definition/@targetNamespace\n" +
            "    return <value>{data($svc/@name)}</value>\n" +
            "} </values>";
       
        registry.registerIndex("wsdl.service", // index field name
                               "WSDL Services", // Display Name
                               Index.Language.XQUERY,
                               QName.class, // search input type
                               exp, // the xquery expression
                               Constants.WSDL_DEFINITION_QNAME); // document QName which this applies to
        exp = 
            "" +
            "declare namespace wsdl=\"http://schemas.xmlsoap.org/wsdl/\";\n" +
            "declare variable $document external;\n" +
            "" +
            "<values visible=\"false\"> {\n" +
            "for $ep in $document//wsdl:service/wsdl:port\n" +
            "let $ns := $document/wsdl:definition/@targetNamespace\n" +
            "    return <value>{data($ep/@name)}</value>\n" +
            "} </values>";
       
        registry.registerIndex("wsdl.endpoint", // index field name
                               "WSDL Endpoints", // Display Name
                               Index.Language.XQUERY,
                               QName.class, // search input type
                               exp, // the xquery expression
                               Constants.WSDL_DEFINITION_QNAME); // document QName which this applies to
       
        exp = 
            "" +
            "declare namespace wsdl=\"http://schemas.xmlsoap.org/wsdl/\";\n" +
            "declare variable $document external;\n" +
            "" +
            "<values visible=\"false\"> {\n" +
            "for $b in $document//wsdl:bindingt\n" +
            "let $ns := $document/wsdl:definition/@targetNamespace\n" +
            "    return <value>{data($b/@name)}</value>\n" +
            "} </values>";
       
        registry.registerIndex("wsdl.binding", // index field name
                               "WSDL Bindings", // Display Name
                               Index.Language.XQUERY,
                               QName.class, // search input type
                               exp, // the xquery expression
                               Constants.WSDL_DEFINITION_QNAME); // document QName which this applies to
       
        exp = 
            "" +
            "declare namespace wsdl=\"http://schemas.xmlsoap.org/wsdl/\";\n" +
            "declare variable $document external;\n" +
            "" +
            "<values visible=\"false\"> {\n" +
            "for $pt in $document//wsdl:portType\n" +
            "let $ns := $document/wsdl:definition/@targetNamespace\n" +
            "    return <value>{data($pt/@name)}</value>\n" +
            "} </values>";
       
        registry.registerIndex("wsdl.portType", // index field name
                               "WSDL PortTypes", // Display Name
                               Index.Language.XQUERY,
                               QName.class, // search input type
                               exp, // the xquery expression
                               Constants.WSDL_DEFINITION_QNAME); // document QName which this applies to
       
        // Index the target namespace
        registry.registerIndex("wsdl.targetNamespace", // index field name
                               "WSDL Target Namespace", // Display Name
                               Index.Language.XPATH,
                               String.class, // search input type
                               "/*/@targetNamespace", // the xquery expression
                               Constants.WSDL_DEFINITION_QNAME); // document QName which this applies to
                 

    }

    public void initializeEverytime() throws Exception {
        
        CustomArtifactTypeView view = new CustomArtifactTypeView();
        view.getColumns().add(new Column("Port Types", new ColumnEvaluator() {
            public Object getValue(Object artifact) {
                Object o = ((Artifact)artifact).getActiveVersion().getProperty("wsdl.portType");
                
                if (o != null) {
                    return ((Collection) o).size();
                }
                return 0;
            }
        }));

        view.getColumns().add(new Column("Bindings", new ColumnEvaluator() {
            public Object getValue(Object artifact) {
                Object o = ((Artifact)artifact).getActiveVersion().getProperty("wsdl.binding");
                
                if (o != null) {
                    return ((Collection) o).size();
                }
                return 0;
            }
        }));
        view.getColumns().add(new Column("Services", new ColumnEvaluator() {
            public Object getValue(Object artifact) {
                Object o = ((Artifact)artifact).getActiveVersion().getProperty("wsdl.service");
                
                if (o != null) {
                    return ((Collection) o).size();
                }
                return 0;
            }
        }));
        view.getColumns().add(1, new Column("Namespace", new ColumnEvaluator() {
            public Object getValue(Object artifact) {
                return ((Artifact)artifact).getActiveVersion().getProperty("wsdl.targetNamespace");
            }
        }));
        viewManager.addView(view, Constants.WSDL_DEFINITION_QNAME);
    }


}
