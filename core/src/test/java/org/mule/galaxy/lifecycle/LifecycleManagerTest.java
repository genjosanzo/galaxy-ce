package org.mule.galaxy.lifecycle;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.Dao;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class LifecycleManagerTest extends AbstractGalaxyTest {
    protected LifecycleManager lifecycleManager;
    protected Dao<PhaseLogEntry> phaseLogEntryDao;
    
    public void testLifecycleInitialization() throws Exception {
        Collection<Lifecycle> lifecycles = lifecycleManager.getLifecycles();
        
        assertEquals(1, lifecycles.size());
        
        Lifecycle l = lifecycles.iterator().next();
        
        assertEquals("Default", l.getName());
        
        Phase init = l.getInitialPhase();
        assertNotNull(init);
        
        assertEquals("Created", init.getName());
        
        Set<Phase> phases = init.getNextPhases();
        assertEquals(1, phases.size());
        
        Phase p = phases.iterator().next();
        assertEquals("Developed", p.getName());
    }
    
    public void testLifecyclesAndArtifact() throws Exception {
        Collection<Lifecycle> lifecycles = lifecycleManager.getLifecycles();
        assertEquals(1, lifecycles.size());
        
        Lifecycle l = lifecycles.iterator().next();
        Phase created = l.getInitialPhase();
        Phase dev = created.getNextPhases().iterator().next();
        
        Artifact artifact = importHelloWsdl();
        assertFalse(lifecycleManager.isTransitionAllowed(artifact, created));
        assertTrue(lifecycleManager.isTransitionAllowed(artifact, dev));
        
        Phase current = artifact.getPhase();
        assertEquals(created.getName(), current.getName());
        
        lifecycleManager.transition(artifact, dev, getAdmin());
        
        current = artifact.getPhase();
        assertEquals(dev.getName(), current.getName());
        
        try {
            lifecycleManager.transition(artifact, dev, getAdmin());
            fail("Expected Transition Exception");
        } catch (TransitionException e) {
            // expected
        }
        
        List<PhaseLogEntry> entries = phaseLogEntryDao.listAll();
        assertEquals(1, entries.size());
        PhaseLogEntry e = entries.get(0);
        assertNotNull(e.getUser());
        assertEquals(artifact.getId(), e.getArtifact().getId());
        assertNotNull(e.getCalendar());
        assertNotNull(e.getPhase());
    }
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] {
            "/META-INF/applicationContext-core.xml"
        };
    }
    
}
