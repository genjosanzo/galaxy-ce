package org.mule.galaxy.impl;

import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;
import org.mule.galaxy.script.ScriptJob;

public class ScriptJobDaoImpl extends AbstractReflectionDao<ScriptJob>  {

    public ScriptJobDaoImpl() throws Exception {
        super(ScriptJob.class, "scriptJobs", true);
    }

    @Override
    protected String getNodeType() {
        return "galaxy:scriptJob";
    }
    
}
