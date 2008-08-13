package org.mule.galaxy.impl.jcr.query;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.Entry;
import org.mule.galaxy.impl.jcr.JcrArtifact;

public class ArtifactPropertyQueryBuilder extends SimpleQueryBuilder {

    public ArtifactPropertyQueryBuilder() {
        super(new String [] { JcrArtifact.DOCUMENT_TYPE,
                              JcrArtifact.CONTENT_TYPE,
                              JcrArtifact.NAME,
                              JcrArtifact.DESCRIPTION });
     
        appliesTo.add(Artifact.class);
        appliesTo.add(Entry.class);
    }

}
