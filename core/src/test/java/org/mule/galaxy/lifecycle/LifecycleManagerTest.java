package org.mule.galaxy.lifecycle;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Dao;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.Workspace;
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
        ArtifactVersion version = artifact.getDefaultVersion();
        assertEquals(created, version.getPhase());
        
        assertFalse(lifecycleManager.isTransitionAllowed(version, created));
        assertTrue(lifecycleManager.isTransitionAllowed(version, dev));
        
        Phase current = version.getPhase();
        assertEquals(created.getName(), current.getName());
        
        lifecycleManager.transition(version, dev, getAdmin());
        
        current = version.getPhase();
        assertEquals(dev.getName(), current.getName());
        
        try {
            lifecycleManager.transition(version, dev, getAdmin());
            fail("Expected Transition Exception");
        } catch (TransitionException e) {
            // expected
        }
        
        List<PhaseLogEntry> entries = phaseLogEntryDao.listAll();
        assertEquals(1, entries.size());
        PhaseLogEntry e = entries.get(0);
        assertNotNull(e.getUser());
        assertEquals(artifact.getId(), e.getArtifactVersion().getParent().getId());
        assertNotNull(e.getCalendar());
        assertNotNull(e.getPhase());
    }
    
    public void testSave() throws Exception {    
        Lifecycle l = lifecycleManager.getDefaultLifecycle();
        l.setName("test");
        assertNotNull(l.getId());
        
        lifecycleManager.save(l);
        assertEquals("test", l.getName());

        Collection<Lifecycle> lcs = lifecycleManager.getLifecycles();
        assertEquals(1, lcs.size());
        
        Lifecycle l2 = lifecycleManager.getLifecycle("test");
        assertNotNull(l2);
        
        Artifact a = importHelloWsdl();
        
        l.setName("test2");
        lifecycleManager.save(l);
        
        a = registry.getArtifact(a.getId());
        assertEquals(l, a.getDefaultVersion().getPhase().getLifecycle());
        
        try {
            lifecycleManager.delete(l.getId(), null);
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
        
        lifecycleManager.delete(l.getId(), newLc.getId());
        
        Workspace wkspc = registry.getWorkspaceByPath("Default Workspace");
        Artifact artifact = registry.getArtifact(wkspc, "hello_world.wsdl");
        assertEquals(newLc, artifact.getDefaultVersion().getPhase().getLifecycle());
        
        // try saving a lifecycle with a duplicate name
        newLc.setId(null);
        
        try {
            lifecycleManager.save(newLc);
            fail("Duplicate lifecycles should not be allowed.");
        } catch (DuplicateItemException e) {
            // this is expected
        }
        
        
    }
    
    public void testWorkspaceInteraction() throws Exception {
        Lifecycle newLc = new Lifecycle();
        newLc.setName("another");
        
        Phase phase = new Phase(newLc);
        phase.setName("p1");
        newLc.setInitialPhase(phase);
        newLc.addPhase(phase);
        
        lifecycleManager.save(newLc);
        
        assertNotNull(newLc.getId());
        
        Workspace w = registry.getWorkspaces().iterator().next();
        w.setDefaultLifecycle(newLc);
        registry.save(w);
        
        Workspace w2 = registry.getWorkspaceByPath(w.getPath());
        assertEquals(newLc.getName(), w2.getDefaultLifecycle().getName());
    }
    
    public void testPhaseChanges() throws Exception {
        Lifecycle l = lifecycleManager.getDefaultLifecycle();
        
        Artifact artifact = importHelloWsdl();
        
        Phase p1 = l.getInitialPhase();
        p1.setName("new name");
        
        lifecycleManager.save(l);
        
        artifact = registry.getArtifact(artifact.getId());
        assertEquals(p1, artifact.getDefaultVersion().getPhase());
        
        Phase p2 = p1.getNextPhases().iterator().next();
        
        l.removePhase(p2);
        lifecycleManager.save(l);
        
        Lifecycle l2 = lifecycleManager.getDefaultLifecycle();
        assertEquals(l.getPhases().size(), l2.getPhases().size());
        
        // todo: invalid nextPhases
    }
    
}
