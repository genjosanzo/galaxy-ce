package org.mule.galaxy.impl;

import java.util.List;

import org.mule.galaxy.Dao;
import org.mule.galaxy.script.Script;
import org.mule.galaxy.script.ScriptJob;
import org.mule.galaxy.script.ScriptManager;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class ScriptManagerImplTest extends AbstractGalaxyTest {
    public static int count = 0;
    
    protected ScriptManager scriptManager;
    protected Dao<ScriptJob> scriptJobDao;
    
    public void testScripts() throws Exception {
    }
    public void xtestScripts() throws Exception {
        Script script = new Script();
        script.setName("test");
        script.setRunOnStartup(true);
        script.setScript("System.out.println(\"hello\"); org.mule.galaxy.impl.ScriptManagerImplTest.count++; return \"hello\";");
        
        scriptManager.save(script);
        
        assertEquals("hello", scriptManager.execute(script.getScript()));
        
        ScriptJob sj = new ScriptJob();
        sj.setName("test");
        sj.setDescription("test");
        sj.setExpression("* * * ? * *");
        sj.setScript(script);
        
        scriptJobDao.save(sj);

        Thread.sleep(2000);
        
        assertTrue(count >= 2);
        
        List<ScriptJob> jobs = scriptJobDao.listAll();
        assertEquals(1, jobs.size());
        
        scriptManager.delete(script.getId());
        
        jobs = scriptJobDao.listAll();
        assertEquals(0, jobs.size());
    }
}
