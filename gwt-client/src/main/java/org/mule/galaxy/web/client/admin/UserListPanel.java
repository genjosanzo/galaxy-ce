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
import org.mule.galaxy.web.rpc.WUser;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Hyperlink;

import java.util.Collection;
import java.util.Iterator;

public class UserListPanel
    extends AbstractAdministrationComposite
{
    public UserListPanel(AdministrationPanel a) {
        super(a);
    }

    @Override
    public void doShow() {
        super.doShow();

        final FlexTable table = createTitledRowTable(panel, "Users");

        table.setText(0, 0, "Username");
        table.setText(0, 1, "Name");
        table.setText(0, 2, "Email");

        adminPanel.getSecurityService().getUsers(new AbstractCallback(adminPanel) {

            public void onSuccess(Object result) {
                Collection users = (Collection) result;

                int i = 1;
                for (Iterator itr = users.iterator(); itr.hasNext();) {
                    final WUser u = (WUser) itr.next();

                    Hyperlink hyperlink = new Hyperlink(u.getUsername(),
                                                        "users/" + u.getId());

                    table.setWidget(i, 0, hyperlink);
                    table.setText(i, 1, u.getName());
                    table.setText(i, 2, u.getEmail());
                    table.getRowFormatter().setStyleName(i, "artifactTableEntry");
                    i++;
                }
            }
        });
    }

    public String getTitle()
    {
        return "Users";
    }
}
