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

import org.mule.galaxy.web.rpc.AdminService;
import org.mule.galaxy.web.client.RPCException;
import org.mule.galaxy.security.AccessControlManager;
import org.mule.galaxy.security.Permission;
import org.mule.galaxy.security.AccessException;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.BeansException;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class AdminServiceImpl implements AdminService, ApplicationContextAware
{
    private ApplicationContext applicationContext;

    private AccessControlManager accessControlManager;

    public String executeScript(String scriptText) throws RPCException {
        try {
            accessControlManager.assertAccess(Permission.EXECUTE_ADMIN_SCRIPTS);
        } catch (AccessException e) {
            throw new RPCException(e.getMessage());
        }

        Binding binding = new Binding();
        binding.setProperty("applicationContext", applicationContext);
        GroovyShell shell = new GroovyShell(binding);
        Object result = shell.evaluate(scriptText);
        return result == null ? null : result.toString();
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setAccessControlManager(AccessControlManager accessControlManager) {
        this.accessControlManager = accessControlManager;
    }
}