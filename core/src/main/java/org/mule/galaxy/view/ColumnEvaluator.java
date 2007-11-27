package org.mule.galaxy.view;

import org.mule.galaxy.Artifact;

public interface ColumnEvaluator {
    public Object getValue(Artifact artifact);
}
