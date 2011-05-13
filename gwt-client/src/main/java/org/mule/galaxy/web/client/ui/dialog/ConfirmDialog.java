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

package org.mule.galaxy.web.client.ui.dialog;

import org.mule.galaxy.web.client.ui.help.PanelConstants;
import org.mule.galaxy.web.client.ui.panel.InlineFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

public class ConfirmDialog extends DialogBox
{
	
	private static final PanelConstants panelMessages = (PanelConstants) GWT.create(PanelConstants.class);
	
    public ConfirmDialog(final ConfirmDialogListener confirmListener, final String caption) {
      setText(caption);

      InlineFlowPanel buttonPanel = new InlineFlowPanel();

      Button cancelButton = new Button(panelMessages.cancel());
      cancelButton.addClickListener(new ClickListener() {
        public void onClick(Widget sender) {
            ConfirmDialog.this.hide();
            confirmListener.onCancel();
        }
      });

      Button okButton = new Button("OK");
      okButton.addClickListener(new ClickListener() {
        public void onClick(Widget sender) {
            ConfirmDialog.this.hide();
            confirmListener.onConfirm();
        }
      });
      buttonPanel.add(okButton);
      buttonPanel.add(cancelButton);

      setWidget(buttonPanel);
    }
  }
