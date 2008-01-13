package org.mule.galaxy.impl.artifact;

import javax.xml.namespace.QName;

import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.Dao;
import org.mule.galaxy.Index;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.util.Constants;

/**
 * Does some initialization for other artifact types.
 */
public class OtherArtifactPlugin extends AbstractArtifactPlugin implements Constants {

    public void initializeOnce() throws Exception {
        artifactTypeDao
            .save(new ArtifactType("WS-Policy Documents", "application/policy+xml", POLICY_QNAME, POLICY_2006_QNAME));
        artifactTypeDao.save(new ArtifactType("XML Schemas", "application/xmlschema+xml", SCHEMA_QNAME));
        artifactTypeDao.save(new ArtifactType("XSLT Stylesheets", "application/xslt+xml", XSLT_QNAME));
        artifactTypeDao.save(new ArtifactType("Other Artifacts", "*/*"));
    }

    public void initializeEverytime() throws Exception {
        // TODO Auto-generated method stub
        
    }
    
}
