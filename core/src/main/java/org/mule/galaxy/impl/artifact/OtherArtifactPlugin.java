package org.mule.galaxy.impl.artifact;

import org.mule.galaxy.api.artifact.ArtifactType;
import org.mule.galaxy.api.util.Constants;

/**
 * Does some initialization for other artifact types.
 */
public class OtherArtifactPlugin extends AbstractArtifactPlugin implements Constants
{

    public void initializeOnce() throws Exception {
//        artifactTypeDao
//            .save(new ArtifactTypeImpl("WS-Policy Documents", "application/policy+xml", POLICY_QNAME, POLICY_2006_QNAME));
//        artifactTypeDao.save(new ArtifactTypeImpl("XML Schemas", "application/xmlschema+xml", SCHEMA_QNAME));
//        artifactTypeDao.save(new ArtifactTypeImpl("XSLT Stylesheets", "application/xslt+xml", XSLT_QNAME));
        artifactTypeDao.save(new ArtifactType("Other Artifacts", "*/*"));
    }

    public void initializeEverytime() throws Exception {
        // TODO Auto-generated method stub
        
    }
    
}
