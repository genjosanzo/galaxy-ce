package org.mule.galaxy.impl.artifact;

import java.util.Collection;

import javax.wsdl.xml.WSDLLocator;

import org.mule.galaxy.AbstractArtifactPlugin;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.Index;
import org.mule.galaxy.util.Constants;
import org.mule.galaxy.view.Column;
import org.mule.galaxy.view.ColumnEvaluator;
import org.mule.galaxy.view.CustomArtifactTypeView;


import org.xml.sax.InputSource;

public class MuleArtifactPlugin extends AbstractArtifactPlugin {

    @Override
    public void initialize() throws Exception {
        artifactTypeDao.save(new ArtifactType("Mule Configuration", 
                                              "application/mule+xml", 
                                              Constants.MULE_QNAME));
        
        // Read <mule:service> elements
        String exp = 
            "declare namespace mule=\"http://www.mulesource.org/schema/mule/core/2.0\";\n" +
            "declare variable $document external;\n" +
            "" +
            "<values visible=\"false\"> {\n" +
            "for $svc in $document//mule:service\n" +
            "    return <value>{data($svc/@name)}</value>\n" +
            "} </values>";
       
        registry.registerIndex("mule.service", // index field name
                               "Mule Service", // Display Name
                               Index.Language.XQUERY,
                               String.class, // search input type
                               exp, // the xquery expression
                               Constants.MULE_QNAME); // document QName which this applies to
        
        // Create a custom view
        CustomArtifactTypeView view = new CustomArtifactTypeView();
        view.getColumns().add(new Column("Services", new ColumnEvaluator() {
            public Object getValue(Object artifact) {
                Object o = ((Artifact)artifact).getLatestVersion().getProperty("mule.service");
                
                if (o != null) {
                    return ((Collection) o).size();
                }
                return 0;
            }
        }));
        viewManager.addView(view, Constants.MULE_QNAME);
    }


}
