package org.mule.galaxy.util;

import junit.framework.TestCase;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.impl.jcr.JcrArtifact;
import org.mule.galaxy.security.Permission;

public class SecurityUtilsTest extends TestCase {
    public void testAppliesTo() throws Exception {
        assertTrue(SecurityUtils.appliesTo(Permission.READ_ARTIFACT, Artifact.class));
        assertTrue(SecurityUtils.appliesTo(Permission.READ_ARTIFACT, JcrArtifact.class));
    }
}
