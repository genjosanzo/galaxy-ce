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
            "declare namespace mule=\"http://www.mulesource.org/schema/mule/core/2.0\";\n" +
            "declare variable $document external;\n" +
            "" +
            "<values> {\n" +
            "for $svc in $document//mule:service\n" +
            "    return <value>{data($svc/@name)}</value>\n" +
            "} </values>";
       
        indexManager.save(new Index("mule2.service", // index field name
                                    "Mule 2 Services", // Display Name
                                    Index.Language.XQUERY,
                                    String.class, // search input type
                                    exp, // the xquery expression
                                    Constants.MULE2_QNAME)); // document QName which this applies to
            
    }

    public void initializeEverytime() throws Exception {

        // Create a custom view
        CustomArtifactTypeView view = new CustomArtifactTypeView();
        view.getColumns().add(new Column("Services", true, new ColumnEvaluator() {
            public Object getValue(Object artifact) {
                Object o = ((Artifact)artifact).getActiveVersion().getProperty("mule2.service");
                
                if (o != null) {
                    return ((Collection) o).size();
                }
                return 0;
            }
        }));
        viewManager.addView(view, Constants.MULE_QNAME);
    }


}
