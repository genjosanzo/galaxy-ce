/*
 * $Id: LicenseHeader-GPLv2.txt 288 2008-05-14 00:59:35Z mark $
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

package org.mule.galaxy.web.client;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;


/**
 * Used to display version and license information
 */
public class AboutPanel extends LayoutContainer {

    public AboutPanel() {
        final Dialog simple = new Dialog();
        simple.setHeading("About...");
        simple.setButtons(Dialog.OK);
        simple.setBodyStyleName("pad-text");
        simple.add(getAboutText());
        simple.setScrollMode(Style.Scroll.AUTO);
        simple.setHideOnButtonClick(true);
        simple.setWidth(500);
        simple.show();
    }

    // FIXME:
    // we need version info, license info, support info, etc...
    public Html getAboutText() {
        Html html = new Html();
        html.setHtml("<b>Sed ut perspiciatis unde</b>" +
                "<br><br> " +
                "omnis iste natus error " +
                "sit voluptatem accusantium doloremque laudantium, totam " +
                "rem aperiam, eaque ipsa quae ab illo inventore veritatis" +
                " et quasi architecto beatae vitae dicta sunt explicabo. " +
                "Nemo enim ipsam voluptatem quia voluptas sit aspernatur " +
                "<br><br>" +
                "aut odit aut fugit, sed quia consequuntur magni dolores eos " +
                "qui ratione voluptatem sequi nesciunt. Neque porro quisquam " +
                "est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci " +
                "velit, sed quia non numquam eius modi tempora incidunt ut labore " +
                "<br><br>" +
                "et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima " +
                "veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, " +
                "nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure " +
                "reprehenderit qui in ea voluptate velit esse quam nihil molestiae" +
                " consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?");

        return html;
    }

}
