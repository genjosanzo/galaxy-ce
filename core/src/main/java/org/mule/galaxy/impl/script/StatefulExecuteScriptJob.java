package org.mule.galaxy.impl.script;

import org.quartz.StatefulJob;

/**
 * A job execution class which doesn't allow concurrent execution of jobs
 * because it implements {@link StatefulJob}
 */
public class StatefulExecuteScriptJob extends ExecuteScriptJob implements StatefulJob {

}
