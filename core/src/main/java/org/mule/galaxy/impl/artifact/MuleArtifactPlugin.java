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

public class MuleArtifactPlugin extends AbstractArtifactPlugin {

    public void initializeOnce() throws Exception {
        artifactTypeDao.save(new ArtifactType("Mule Configurations", 
                                              "application/mule+xml", 
                                              Constants.MULE_QNAME));

        // Read <mule-configuration id=> attributes
        String exp =
            "declare variable $document external;\n" +
            "" +
            "<values> {\n" +
            "for $e in $document/mule-configuration\n" +
            "    return <value>{data($e/@id)}</value>\n" +
            "} </values>";

        indexManager.save(new Index("mule.id", // index field name
                                    "Mule Id", // Display Name
                                    Index.Language.XQUERY,
                                    String.class, // search input type
                                    exp, // the xquery expression
                                    Constants.MULE_QNAME)); // document QName which this applies to

        // Read <mule-configuration version=> attributes
        exp =
            "declare variable $document external;\n" +
            "" +
            "<values> {\n" +
            "for $e in $document/mule-configuration\n" +
            "    return <value>{data($e/@version)}</value>\n" +
            "} </values>";

        indexManager.save(new Index("mule.version", // index field name
                                    "Mule Version", // Display Name
                                    Index.Language.XQUERY,
                                    String.class, // search input type
                                    exp, // the xquery expression
                                    Constants.MULE_QNAME)); // document QName which this applies to

        // Read <mule-discriptor> elements
        exp =
            "declare variable $document external;\n" +
            "" +
            "<values> {\n" +
            "for $e in $document//mule-descriptor\n" +
            "    return <value>{data($e/@name)}</value>\n" +
            "} </values>";
       
        indexManager.save(new Index("mule.descriptor", // index field name
                                    "Mule Descriptors", // Display Name
                                    Index.Language.XQUERY,
                                    String.class, // search input type
                                    exp, // the xquery expression
                                    Constants.MULE_QNAME)); // document QName which this applies to
        exp = 
            "declare variable $document external;\n" +
            "" +
            "<values> {\n" +
            "for $e in $document//model\n" +
            "    return <value>{data($e/@name)}</value>\n" +
            "} </values>";
       
        indexManager.save(new Index("mule.model", // index field name
                                    "Mule Models", // Display Name
                                    Index.Language.XQUERY,
                                    String.class, // search input type
                                    exp, // the xquery expression
                                    Constants.MULE_QNAME)); // document QName which this applies to
        exp = 
            "declare variable $document external;\n" +
            "" +
            "<values> {\n" +
            "for $e in $document//transformers/transformer\n" +
            "    return <value>{data($e/@name)}</value>\n" +
            "} </values>";
       
        indexManager.save(new Index("mule.transformer", // index field name
                                    "Mule Transformers", // Display Name
                                    Index.Language.XQUERY,
                                    String.class, // search input type
                                    exp, // the xquery expression
                                    Constants.MULE_QNAME)); // document QName which this applies to
            
        indexManager.save(new Index("mule.description", // index field name
                                    "Mule Description", // Display Name
                                    Index.Language.XPATH,
                                    String.class, // search input type
                                    "/mule-configuration/description", // the xquery expression
                                    Constants.MULE_QNAME)); // document QName which this applies to

        indexManager.save(new Index("mule.descriptor.description", // index field name
                                    "Mule Descriptor Description", // Display Name
                                    Index.Language.XPATH,
                                    String.class, // search input type
                                    "//mule-descriptor/description", // the xquery expression
                                    Constants.MULE_QNAME)); // document QName which this applies to
            
    }

    public void initializeEverytime() throws Exception {

        // Create a custom view
        CustomArtifactTypeView view = new CustomArtifactTypeView();
        view.getColumns().add(new Column("Descriptors", true, new ColumnEvaluator() {
            public Object getValue(Object artifact) {
                Object o = ((Artifact)artifact).getActiveVersion().getProperty("mule.descriptor");
                
                if (o != null) {
                    return ((Collection) o).size();
                }
                return 0;
            }
        }));
        view.getColumns().add(new Column("Transformers", true, new ColumnEvaluator() {
            public Object getValue(Object artifact) {
                Object o = ((Artifact)artifact).getActiveVersion().getProperty("mule.transformer");
                
                if (o != null) {
                    return ((Collection) o).size();
                }
                return 0;
            }
        }));
        view.getColumns().add(new Column("Models", true, new ColumnEvaluator() {
            public Object getValue(Object artifact) {
                Object o = ((Artifact)artifact).getActiveVersion().getProperty("mule.model");
                
                if (o != null) {
                    return ((Collection) o).size();
                }
                return 0;
            }
        }));
        viewManager.addView(view, Constants.MULE_QNAME);
    }


}
