package org.mule.galaxy.impl.lifecycle;

import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;
import org.mule.galaxy.lifecycle.PhaseLogEntry;

public class PhaseLogEntryDaoImpl extends AbstractReflectionDao<PhaseLogEntry> {

    public PhaseLogEntryDaoImpl() throws Exception {
        super(PhaseLogEntry.class, "phaseLogEntries", true);
    }

}
