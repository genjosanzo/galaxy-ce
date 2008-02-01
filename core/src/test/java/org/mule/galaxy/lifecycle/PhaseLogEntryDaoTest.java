package org.mule.galaxy.lifecycle;

import java.util.Calendar;

import org.mule.galaxy.Dao;
import org.mule.galaxy.test.AbstractGalaxyTest;

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
