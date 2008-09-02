/*
 * $Id: LicenseHeader-GPLv2.txt 288 2008-08-25 00:59:35Z mark $
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

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Hyperlink;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WLifecycle;
import org.mule.galaxy.web.rpc.WPhase;
import org.mule.galaxy.web.rpc.WScriptJob;

/**
 *  Show all scheduled items
 */
public class ScheduleListPanel extends AbstractAdministrationComposite {
    
    public ScheduleListPanel(AdministrationPanel a) {
        super(a);
    }

    public void onShow() {
        super.onShow();
        
        final FlexTable table = createTitledRowTable(panel, "Scheduled Jobs");

        table.setText(0, 0, "Name");
        table.setText(0, 1, "Script");
        table.setText(0, 2, "Cron Command");
        table.setText(0, 3, "Description");

        adminPanel.getGalaxy().getAdminService().getScriptJobs(new AbstractCallback<List<WScriptJob>>(adminPanel) {

            public void onSuccess(List<WScriptJob> jobs) {
                showJobs(table, jobs);
            }

        });
    }

    protected void showJobs(FlexTable table, List<WScriptJob> jobs) {
         int i = 1;
         for (WScriptJob j : jobs) {
             table.setWidget(i, 0, new Hyperlink(j.getName(), "schedules/" + j.getId()));
             table.setText(i, 1, j.getScriptName());
             table.setText(i, 2, j.getExpression());
             table.setText(i, 3, j.getDescription());

             i++;
         }
     }

}
