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

import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WLifecycle;
import org.mule.galaxy.web.rpc.WPhase;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Hyperlink;

import java.util.Collection;
import java.util.Iterator;

public class LifecycleListPanel extends AbstractAdministrationComposite {

    public LifecycleListPanel(AdministrationPanel a) {
        super(a);
    }

    public void onShow() {
        super.onShow();

        final FlexTable table = createTitledRowTable(panel, "Lifecycles");

        table.setText(0, 0, "Lifecycle");
        table.setText(0, 1, "Phases");

        adminPanel.getRegistryService().getLifecycles(new AbstractCallback(adminPanel) {

            public void onSuccess(Object arg0) {
                showLifecycles(table, (Collection)arg0);
            }

        });
    }

    protected void showLifecycles(FlexTable table, Collection lifecycles) {
         int i = 1;
         for (Iterator itr = lifecycles.iterator(); itr.hasNext();) {
             final WLifecycle l = (WLifecycle)itr.next();

             String text = l.getName();

             if (l.isDefaultLifecycle()) {
                 text += " (Default)";
             }

             table.setWidget(i, 0, new Hyperlink(text, "lifecycles/" + l.getId()));
             table.setText(i, 1, getPhaseList(l));

             i++;
         }
     }

    private String getPhaseList(WLifecycle l) {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (Iterator<WPhase> itr = l.getPhases().iterator(); itr.hasNext();) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }

            sb.append(itr.next().getName());
        }
        return sb.toString();
    }

}
