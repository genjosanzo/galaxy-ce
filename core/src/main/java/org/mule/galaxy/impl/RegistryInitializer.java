package org.mule.galaxy.impl;

import javax.xml.namespace.QName;

import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.Dao;
import org.mule.galaxy.Index;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.util.Constants;

/**
 * Does some base initialization for the registry.
 */
public class RegistryInitializer {
    private Dao<ArtifactType> artifactTypeDao;
    
    
    public void initialize() throws Exception {
        artifactTypeDao.save(new ArtifactType("WS-Policy", "application/xml", Constants.POLICY_QNAME));
        artifactTypeDao.save(new ArtifactType("XML Schema", "application/xml", Constants.SCHEMA_QNAME));
        artifactTypeDao.save(new ArtifactType("XSLT Stylesheet", "application/xml", Constants.XSLT_QNAME));
    }

    public void setArtifactTypeDao(Dao<ArtifactType> artifactTypeDao) {
        this.artifactTypeDao = artifactTypeDao;
    }
    
}
