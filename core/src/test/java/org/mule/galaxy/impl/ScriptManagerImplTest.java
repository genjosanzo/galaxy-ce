package org.mule.galaxy.impl;

import java.util.List;

import org.mule.galaxy.Dao;
import org.mule.galaxy.script.Script;
import org.mule.galaxy.script.ScriptJob;
import org.mule.galaxy.script.ScriptManager;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class ScriptManagerImplTest extends AbstractGalaxyTest {
    protected ScriptManager scriptManager;
    protected Dao<ScriptJob> scriptJobDao;
    
    public void testScripts() throws Exception {
        Script script = new Script();
        script.setName("test");
        script.setRunOnStartup(true);
        script.setScript("return \"hello\"");
        
        scriptManager.save(script);
        
        assertEquals("hello", scriptManager.execute(script.getScript()));
        
        ScriptJob sj = new ScriptJob();
        sj.setName("test");
        sj.setDescription("test");
        sj.setExpression("test");
        sj.setScript(script);
        
        scriptJobDao.save(sj);
        
        List<ScriptJob> jobs = scriptJobDao.listAll();
        assertEquals(1, jobs.size());
        
        scriptManager.delete(script.getId());
        
        jobs = scriptJobDao.listAll();
        assertEquals(0, jobs.size());
    }
}
