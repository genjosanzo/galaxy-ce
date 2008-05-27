package org.mule.galaxy.impl.jcr.query;

import org.mule.galaxy.impl.jcr.JcrArtifact;
import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.query.OpRestriction.Operator;

public class ArtifactPropertyQueryBuilder extends SimpleQueryBuilder {

    public ArtifactPropertyQueryBuilder() {
        super(new String [] { JcrArtifact.DOCUMENT_TYPE,
                              JcrArtifact.CONTENT_TYPE,
                              JcrArtifact.NAME,
                              JcrArtifact.DESCRIPTION }, true);
    }

}
