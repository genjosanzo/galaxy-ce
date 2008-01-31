package org.mule.galaxy.lifecycle;

import org.mule.galaxy.api.Dao;
import org.mule.galaxy.api.lifecycle.PhaseLogEntry;
import org.mule.galaxy.test.AbstractGalaxyTest;

import java.util.Calendar;

public class PhaseLogEntryDaoTest extends AbstractGalaxyTest {
    protected Dao<PhaseLogEntry> phaseLogEntryDao;
    
    public void testDao() throws Exception {
        PhaseLogEntry entry = new PhaseLogEntry();
        entry.setUser(getAdmin());
        entry.setCalendar(Calendar.getInstance());
        entry.setPhase(lifecycleManager.getDefaultLifecycle().getInitialPhase());

        phaseLogEntryDao.save(entry);
        
        PhaseLogEntry entry2 = phaseLogEntryDao.get(entry.getId());
        assertEquals("admin", entry2.getUser().getUsername());
        assertNotNull(entry2.getPhase());
    }
}
