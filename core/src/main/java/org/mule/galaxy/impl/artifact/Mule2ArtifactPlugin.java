package org.mule.galaxy.impl.artifact;

import java.util.Collection;

import javax.wsdl.xml.WSDLLocator;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.Index;
import org.mule.galaxy.util.Constants;
import org.mule.galaxy.view.Column;
import org.mule.galaxy.view.ColumnEvaluator;
import org.mule.galaxy.view.CustomArtifactTypeView;


import org.xml.sax.InputSource;

public class Mule2ArtifactPlugin extends AbstractArtifactPlugin {

    public void initializeOnce() throws Exception {
        artifactTypeDao.save(new ArtifactType("Mule 2 Configurations", 
                                              "application/mule+xml", 
                                              Constants.MULE2_QNAME));
        
        // Read <mule:service> elements
        String exp = 
            "declare default element namespace \"" + Constants.MULE2_QNAME.getNamespaceURI() + "\";\n" +
            "declare variable $document external;\n" +
            "" +
            "<values> {\n" +
            "for $svc in $document//service\n" +
            "    return <value>{data($svc/@name)}</value>\n" +
            "} </values>";
       
        indexManager.save(new Index("mule2.service", // index field name
                                    "Mule 2 Services", // Display Name
                                    Index.Language.XQUERY,
                                    String.class, // search input type
                                    exp, // the xquery expression
                                    Constants.MULE2_QNAME), true); // document QName which this applies to

        exp =
            "declare default element namespace \"" + Constants.MULE2_QNAME.getNamespaceURI() + "\";\n" +
            "declare variable $document external;\n" +
            "" +
            "<values> {\n" +
            "for $svc in $document/mule/model\n" +
            "    return <value>{data($svc/@name)}</value>\n" +
            "} </values>";

        indexManager.save(new Index("mule2.model", // index field name
                                    "Mule 2 Models", // Display Name
                                    Index.Language.XQUERY,
                                    String.class, // search input type
                                    exp, // the xquery expression
                                    Constants.MULE2_QNAME), true); // document QName which this applies to

        exp =
            "declare default element namespace \"" + Constants.MULE2_QNAME.getNamespaceURI() + "\";\n" +
            "declare variable $document external;\n" +
            "" +
            "<values> {\n" +
            "for $svc in $document/mule/endpoint\n" +
            "    return <value>{data($svc/@name)}</value>\n" +
            "} </values>";

        indexManager.save(new Index("mule2.endpoint", // index field name
                                    "Mule 2 Endpoints", // Display Name
                                    Index.Language.XQUERY,
                                    String.class, // search input type
                                    exp, // the xquery expression
                                    Constants.MULE2_QNAME), true); // document QName which this applies to

        indexManager.save(new Index("mule2.server.id", // index field name
                                    "Mule 2 Server ID", // Display Name
                                    Index.Language.XPATH,
                                    String.class, // search input type
                                    "/*[local-name()='mule' and namespace-uri()='" + Constants.MULE2_QNAME.getNamespaceURI() + "']/*[local-name()='configuration']/@serverId",
                                    Constants.MULE2_QNAME), true); // document QName which this applies to

        indexManager.save(new Index("mule2.cluster.id", // index field name
                                    "Mule 2 Cluster ID", // Display Name
                                    Index.Language.XPATH,
                                    String.class, // search input type
                                    "/*[local-name()='mule' and namespace-uri()='" + Constants.MULE2_QNAME.getNamespaceURI() + "']/*[local-name()='configuration']/@clusterId",
                                    Constants.MULE2_QNAME), true); // document QName which this applies to

        indexManager.save(new Index("mule2.domain.id", // index field name
                                    "Mule 2 Domain ID", // Display Name
                                    Index.Language.XPATH,
                                    String.class, // search input type
                                    "/*[local-name()='mule' and namespace-uri()='" + Constants.MULE2_QNAME.getNamespaceURI() + "']/*[local-name()='configuration']/@domainId", // the xquery expression
                                    Constants.MULE2_QNAME), true); // document QName which this applies to

        indexManager.save(new Index("mule2.description", // index field name
                                    "Mule 2 Description", // Display Name
                                    Index.Language.XPATH,
                                    String.class, // search input type
                                    "/*[local-name()='mule' and namespace-uri()='" + Constants.MULE2_QNAME.getNamespaceURI() + "']/*[local-name()='description']", // the xquery expression
                                    Constants.MULE2_QNAME), true); // document QName which this applies to

//        indexManager.save(new Index("mule2.model.description", // index field name
//                                    "Mule 2 Model Description", // Display Name
//                                    Index.Language.XPATH,
//                                    String.class, // search input type
//                                    "/mule/model/description", // the xquery expression
//                                    Constants.MULE2_QNAME)); // document QName which this applies to
//
//        indexManager.save(new Index("mule2.service.description", // index field name
//                                    "Mule 2 Service Description", // Display Name
//                                    Index.Language.XPATH,
//                                    String.class, // search input type
//                                    "//service/description", // the xquery expression
//                                    Constants.MULE2_QNAME)); // document QName which this applies to

    }

    public void initializeEverytime() throws Exception {

        // Create a custom view
        CustomArtifactTypeView view = new CustomArtifactTypeView();
        view.getColumns().add(new Column("Services", true, false, new ColumnEvaluator() {
            public Object getValue(Object artifact) {
                Object o = ((Artifact)artifact).getActiveVersion().getProperty("mule2.service");
                
                if (o != null) {
                    return ((Collection) o).size();
                }
                return 0;
            }
        }));
        viewManager.addView(view, Constants.MULE2_QNAME);
    }


}
