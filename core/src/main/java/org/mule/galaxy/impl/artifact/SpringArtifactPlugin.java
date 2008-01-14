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

public class SpringArtifactPlugin extends AbstractArtifactPlugin {

    public void initializeOnce() throws Exception {
        artifactTypeDao.save(new ArtifactType("Spring Configurations",
                                              "application/spring+xml",
                                              Constants.SPRING_QNAME));


        String exp = "declare default element namespace \"" + Constants.SPRING_QNAME.getNamespaceURI() + "\";\n" +
                "declare variable $document external;\n" +
                "<values> \n" +
                "{\n" +
                "for $e in $document//bean\n" +
                "  return if ($e/@name) \n" +
                "then <value>{data($e/@name)}</value> \n" +
                "else <value>{data($e/@id)}</value>\n" +
                "}\n" +
                "</values>";

        indexManager.save(new Index("spring.bean", // index field name
                                    "Spring Beans", // Display Name
                                    Index.Language.XQUERY,
                                    String.class, // search input type
                                    exp, // the xquery expression
                                    Constants.SPRING_QNAME), true); // document QName which this applies to
       
        indexManager.save(new Index("spring.description", // index field name
                                    "Spring Description", // Display Name
                                    Index.Language.XPATH,
                                    String.class, // search input type
                                    "/beans/description", // the xquery expression
                                    Constants.SPRING_QNAME), true); // document QName which this applies to
        
        // TODO: reenable this once we can make xpath queries non visible on the artifact page
//
//        indexManager.save(new Index("spring.bean.description", // index field name
//                                    "Spring Bean Descriptions", // Display Name
//                                    Index.Language.XPATH,
//                                    String.class, // search input type
//                                    "/beans/bean/description", // the xquery expression
//                                    Constants.SPRING_QNAME)); // document QName which this applies to

    }

    public void initializeEverytime() throws Exception {

        // Create a custom view
        CustomArtifactTypeView view = new CustomArtifactTypeView();
        view.getColumns().add(new Column("Beans", true, false, new ColumnEvaluator() {
            public Object getValue(Object artifact) {
                Object o = ((Artifact)artifact).getActiveVersion().getProperty("spring.bean");

                if (o != null) {
                    return ((Collection) o).size();
                }
                return 0;
            }
        }));
        viewManager.addView(view, Constants.SPRING_QNAME);
    }


}