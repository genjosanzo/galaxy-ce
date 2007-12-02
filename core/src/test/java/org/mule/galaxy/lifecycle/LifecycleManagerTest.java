package org.mule.galaxy.lifecycle;

import java.util.Collection;
import java.util.Set;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class LifecycleManagerTest extends AbstractGalaxyTest {
    protected LifecycleManager lifecycleManager;
    
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
