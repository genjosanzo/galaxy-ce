/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.mule.galaxy.web.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.Dao;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.script.CronParseException;
import org.mule.galaxy.script.Script;
import org.mule.galaxy.script.ScriptJob;
import org.mule.galaxy.script.ScriptManager;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.web.client.RPCException;
import org.mule.galaxy.web.rpc.AdminService;
import org.mule.galaxy.web.rpc.ItemExistsException;
import org.mule.galaxy.web.rpc.WScript;
import org.mule.galaxy.web.rpc.WScriptJob;

public class AdminServiceImpl implements AdminService {
    private final Log log = LogFactory.getLog(getClass());
    private ScriptManager scriptManager;
    private Dao<ScriptJob> scriptJobDao;

    public String executeScript(String scriptText) throws RPCException {
        try {
            return scriptManager.execute(scriptText);
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        } catch (RegistryException e) {
            log.error(e);
            Throwable t = ExceptionUtils.getRootCause(e);
            String msg = t != null
                    ? t.getMessage()
                    : e.getMessage();
            throw new RPCException(msg);
        }
    }

    public void setScriptManager(ScriptManager scriptManager) {
        this.scriptManager = scriptManager;
    }

    public void deleteScript(String id) throws RPCException {
        scriptManager.delete(id);
    }

    public List<WScript> getScripts() throws RPCException {
        List<WScript> wscripts = new ArrayList<WScript>();
        List<Script> scripts = scriptManager.listAll();
        for (Script s : scripts) {
            wscripts.add(toWeb(s));
        }
        return wscripts;
    }

    private WScript toWeb(Script s) {
        return new WScript(s.getId(), s.getName(), s.isRunOnStartup(), s.getScript());
    }

    public void save(WScript ws) throws RPCException, ItemExistsException {
        Script s = fromWeb(ws);
         
        try {
            scriptManager.save(s);
        } catch (DuplicateItemException e) {
            throw new ItemExistsException();
        } catch (NotFoundException e) {
            throw new RPCException(e.getMessage());
        }
    }

    public void deleteScriptJob(String id) throws RPCException {
        scriptJobDao.delete(id);
    }
    
    public List<WScriptJob> getScriptJobs() throws RPCException {
        List<WScriptJob> wscripts = new ArrayList<WScriptJob>();
        List<ScriptJob> scripts = scriptJobDao.listAll();
        for (ScriptJob s : scripts) {
            wscripts.add(toWeb(s));
        }
        return wscripts;
    }

    public WScriptJob getScriptJob(String id) throws RPCException, ItemExistsException {
        try {
            return toWeb(scriptJobDao.get(id));
        } catch (NotFoundException e) {
            throw new ItemExistsException();
        } 
    }

    public void save(WScriptJob script) throws RPCException, ItemExistsException {
        ScriptJob sj = fromWeb(script);
        
        try {
            scriptJobDao.save(sj);
        } catch (DuplicateItemException e) {
            throw new ItemExistsException();
        } catch (NotFoundException e) {
            throw new RPCException(e.getMessage());
        } catch (CronParseException e) {
            throw new RPCException(e.getMessage());
        }
    }

    private WScriptJob toWeb(ScriptJob s) {
        return new WScriptJob(s.getDescription(), s.getExpression(), s.getId(), 
                              s.getName(), s.getScript().getId(), s.getScript().getName());
    }


    private Script fromWeb(WScript ws) {
        Script s = new Script();
        s.setId(ws.getId());
        s.setName(ws.getName());
        s.setRunOnStartup(ws.isRunOnStartup());
        s.setScript(ws.getScript());
        
        return s;
    }
    
    private ScriptJob fromWeb(WScriptJob ws) throws RPCException {
        ScriptJob s = new ScriptJob();
        s.setId(ws.getId());
        s.setName(ws.getName());
        
        try {
            s.setScript(scriptManager.get(ws.getScript()));
        } catch (NotFoundException e) {
            throw new RPCException("Script " + ws.getScript() + " was not found.");
        }
        
        s.setDescription(ws.getDescription());
        s.setExpression(ws.getExpression());
        
        return s;
    }

    public void setScriptJobDao(Dao<ScriptJob> scriptJobDao) {
        this.scriptJobDao = scriptJobDao;
    }
    
}
