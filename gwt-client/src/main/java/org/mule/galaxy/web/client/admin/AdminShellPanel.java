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

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;

public class AdminShellPanel
    extends AbstractAdministrationComposite
{
    public AdminShellPanel(AdministrationPanel a) {
        super(a);
    }

    public void onShow() {
        super.onShow();


        FlowPanel scriptOutputPanel = new FlowPanel();
        final Label scriptResultsLabel = new Label();
        scriptResultsLabel.setWordWrap(false);
        scriptOutputPanel.add(scriptResultsLabel);

        panel.add(createPrimaryTitle("Galaxy Admin Shell"));

        panel.add(new Label("Type or paste a Groovy script to be executed on the server. A return value will be displayed below the area. " +
                "Tips: \n   Spring's context is available as 'applicationContext' variable" +
                "\n   only String return values are supported (or null)"));

        final TextArea scriptArea = new TextArea();
        scriptArea.setCharacterWidth(100);
        scriptArea.setVisibleLines(30);
        panel.add(scriptArea);


        final Button evaluateBtn = new Button("Evaluate");
        evaluateBtn.addClickListener(new ClickListener() {
            public void onClick(Widget widget) {
                evaluateBtn.setEnabled(false);
                adminPanel.getGalaxy().getAdminService().executeScript(scriptArea.getText(), new AbstractCallback(adminPanel) {
                    public void onFailure(Throwable caught) {
                        evaluateBtn.setEnabled(true);
                        scriptResultsLabel.setText("");
                        super.onFailure(caught);
                    }

                    public void onSuccess(Object o) {
                        adminPanel.clearErrorMessage();
                        evaluateBtn.setEnabled(true);
                        scriptResultsLabel.setText(o == null ? "No value returned" : o.toString());
                    }
                });
            }
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.addClickListener(new ClickListener() {
            public void onClick(Widget widget) {
                History.back();
            }
        });

        FlowPanel buttons = new FlowPanel();
        buttons.add(evaluateBtn);
        buttons.add(cancelBtn);
        panel.add(buttons);

        panel.add(scriptOutputPanel);

        // spacer above the footer
        panel.add(new Label(" ")); // spacer
    }
}