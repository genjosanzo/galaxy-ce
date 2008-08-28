package org.mule.galaxy.impl;

import org.mule.galaxy.script.Script;
import org.mule.galaxy.script.ScriptManager;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class ScriptManagerImplTest extends AbstractGalaxyTest {
    protected ScriptManager scriptManager;
    
    public void testScripts() throws Exception {
        Script script = new Script();
        script.setName("test");
        script.setRunOnStartup(true);
        script.setScript("return \"hello\"");
        scriptManager.save(script);
        
        assertEquals("hello", scriptManager.execute(script.getScript()));
    }
}
