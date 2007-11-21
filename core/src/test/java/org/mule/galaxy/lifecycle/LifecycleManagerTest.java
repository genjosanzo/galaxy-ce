package org.mule.galaxy.lifecycle;

import java.util.Collection;
import java.util.Set;

import org.mule.galaxy.AbstractGalaxyTest;
import org.mule.galaxy.Artifact;

public class LifecycleManagerTest extends AbstractGalaxyTest {
    protected LifecycleManager lifecycleManager;
    
    public void testLifecycleInitialization() throws Exception {
        Collection<Lifecycle> lifecycles = lifecycleManager.getLifecycles();
        
        assertEquals(1, lifecycles.size());
        
        Lifecycle l = lifecycles.iterator().next();
        
        assertEquals("Default", l.getName());
        
        Set<Phase> phases = l.getInitialPhases();
        assertEquals(1, phases.size());
        
        Phase p = phases.iterator().next();
        assertEquals("Created", p.getName());
        
        phases = p.getNextPhases();
        assertEquals(1, phases.size());
        
        p = phases.iterator().next();
        assertEquals("Developed", p.getName());
    }
    
    public void testLifecyclesAndArtifact() throws Exception {
        Collection<Lifecycle> lifecycles = lifecycleManager.getLifecycles();
        assertEquals(1, lifecycles.size());
        
        Lifecycle l = lifecycles.iterator().next();
        Set<Phase> phases = l.getInitialPhases();
        Phase created = phases.iterator().next();
        Phase dev = created.getNextPhases().iterator().next();
        
        Artifact artifact = importHelloWsdl();
        assertTrue(lifecycleManager.isTransitionAllowed(artifact, created));
        assertFalse(lifecycleManager.isTransitionAllowed(artifact, dev));
        
        lifecycleManager.transition(artifact, created);
        
        Phase current = artifact.getPhase();
        assertEquals(created.getName(), current.getName());
        
        lifecycleManager.transition(artifact, dev);
        
        current = artifact.getPhase();
        assertEquals(dev.getName(), current.getName());
        
        try {
            lifecycleManager.transition(artifact, dev);
            fail("Expected Transition Exception");
        } catch (TransitionException e) {
            // expected
        }
    }
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] {
            "/META-INF/applicationContext-core.xml"
        };
    }
    
}
