package org.mule.galaxy.impl;

import java.util.List;

import org.mule.galaxy.Dao;
import org.mule.galaxy.script.CronParseException;
import org.mule.galaxy.script.Script;
import org.mule.galaxy.script.ScriptJob;
import org.mule.galaxy.script.ScriptManager;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class ScriptManagerImplTest extends AbstractGalaxyTest {
    public static int count = 0;
    
    protected ScriptManager scriptManager;
    protected Dao<ScriptJob> scriptJobDao;
    
    public void testScripts() throws Exception {
        Script script = new Script();
        script.setName("test");
        script.setRunOnStartup(true);
        script.setScript("println 'hello'; org.mule.galaxy.impl.ScriptManagerImplTest.count++; 'hello'");
        
        scriptManager.save(script);
        
        assertEquals("hello", scriptManager.execute(script.getScript()));
        
        ScriptJob sj = new ScriptJob();
        sj.setName("test");
        sj.setDescription("test");
        sj.setExpression("bad expression");
        sj.setScript(script);
        
        try {
            scriptJobDao.save(sj);
            fail("Expected parse exception");
        } catch (CronParseException e) {
            System.out.println(e.getMessage());
        }
        
        sj.setExpression("* * * ? * *");
        scriptJobDao.save(sj);
        Thread.sleep(3000);
        
        assertTrue(count >= 2);
        
        List<ScriptJob> jobs = scriptJobDao.listAll();
        assertEquals(1, jobs.size());
        
        sj.setName("Foo");
        scriptJobDao.save(sj);

        count = 0;
        Thread.sleep(2000);
        assertTrue(count >= 1);
        
        scriptManager.delete(script.getId());
        
        jobs = scriptJobDao.listAll();
        assertEquals(0, jobs.size());
    }
}
