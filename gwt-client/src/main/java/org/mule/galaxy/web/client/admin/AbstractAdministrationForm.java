/*
 * $Id: LicenseHeader-GPLv2.txt 288 2008-01-29 00:59:35Z andrew $
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

package org.mule.galaxy.web.client.admin;

import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.ui.panel.AbstractForm;
import org.mule.galaxy.web.rpc.SecurityServiceAsync;

public abstract class AbstractAdministrationForm extends AbstractForm {

    protected AdministrationPanel adminPanel;
    protected Galaxy galaxy;

    public AbstractAdministrationForm(AdministrationPanel adminPanel, 
                                      String successToken,
                                      String successMessage, 
                                      String deleteMessage, String existsMessage) {
        super(adminPanel, successToken, successMessage, deleteMessage, existsMessage);
        
        this.adminPanel = adminPanel;
        this.galaxy = adminPanel.getGalaxy();
    }
    
    protected SecurityServiceAsync getSecurityService() {
        return adminPanel.getGalaxy().getSecurityService();
    }
}
