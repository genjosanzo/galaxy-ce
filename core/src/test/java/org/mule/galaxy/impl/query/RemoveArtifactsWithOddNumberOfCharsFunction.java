package org.mule.galaxy.impl.query;

import java.util.Iterator;
import java.util.Set;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.query.AbstractFunction;

public class RemoveArtifactsWithOddNumberOfCharsFunction extends AbstractFunction {

    @Override
    public void modifyArtifactVersions(Object[] args, Set<ArtifactVersion> artifacts) {
        for (Iterator<ArtifactVersion> itr = artifacts.iterator(); itr.hasNext();) {
            Artifact next = (Artifact) itr.next().getParent();
            
            if (next.getName().length() % 2 == 1) itr.remove();
            else {
                System.out.println("kept " + next.getName());

            }
        }
        
    }

    @Override
    public void modifyArtifacts(Object[] args, Set<Artifact> artifacts) {
        for (Iterator<Artifact> itr = artifacts.iterator(); itr.hasNext();) {
            Artifact next = itr.next();
            
            if (next.getName().length() % 2 == 1) itr.remove();
            else {
                System.out.println("kept " + next.getName());
            }
        }
    }

    @Override
    public String getModule() {
        return "test";
    }

    @Override
    public String getName() {
        return "removeOddChars";
    }

}
