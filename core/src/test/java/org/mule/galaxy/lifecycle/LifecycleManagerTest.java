package org.mule.galaxy.lifecycle;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.Dao;
import org.mule.galaxy.NotFoundException;
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
        
        assertEquals(created, artifact.getPhase());
        
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
    
    public void testSave() throws Exception {
        Lifecycle l = lifecycleManager.getDefaultLifecycle();
        l.setName("test");
        
        lifecycleManager.save("Default", l);
        assertEquals("test", l.getName());

        Collection<Lifecycle> lcs = lifecycleManager.getLifecycles();
        assertEquals(1, lcs.size());
        
        Lifecycle l2 = lifecycleManager.getLifecycle("test");
        assertNotNull(l2);
        
        Artifact a = importHelloWsdl();
        
        l.setName("test2");
        lifecycleManager.save("test", l);
        
        a = registry.getArtifact(a.getId());
        assertEquals(l, a.getPhase().getLifecycle());
        
        try {
            lifecycleManager.delete(l.getName(), "bad");
            fail("Expected not found exception");
        } catch (NotFoundException e) {
            
        }
        
        Lifecycle newLc = new Lifecycle();
        newLc.setName("another");
        
        Phase phase = new Phase(newLc);
        phase.setName("p1");
        newLc.setInitialPhase(phase);
        
        newLc.addPhase(phase);
        
        lifecycleManager.save(newLc);
        
        lcs = lifecycleManager.getLifecycles();
        assertEquals(2, lcs.size());
    }
    
    public void testPhaseChanges() throws Exception {
        Lifecycle l = lifecycleManager.getDefaultLifecycle();
        
        Artifact artifact = importHelloWsdl();
        
        Phase p1 = l.getInitialPhase();
        p1.setName("new name");
        
        lifecycleManager.save(l.getName(), l);
        
        artifact = registry.getArtifact(artifact.getId());
        assertEquals(p1, artifact.getPhase());
        
        // todo: deletion of a node
        
        // todo: invalid nextPhases
    }
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] {
            "/META-INF/applicationContext-core.xml"
        };
    }
    
}
