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

package org.mule.galaxy.web.client;

import java.util.List;

import org.mule.galaxy.web.client.ui.ProgressIndicatorPopup;

/**
 * Base class if you want to make your component a standalone page
 * that can be loaded with parameters. To register as a page,
 * see {@link Galaxy}.createPageInfo.
 * 
 */
public abstract class AbstractShowable extends WidgetHelper implements Showable {

    // we could provide a method to overload and create custom progress dialogs, but no need yet ;)
    protected ProgressIndicatorPopup progressIndicatorPopup = new ProgressIndicatorPopup();

    private boolean useLoadingIndicator = true;

    private void show() {
        onBeforeShowPage();
        doShowPage();
        onAfterShowPage();
    }
    
    public void doShowPage() {
        // no-op
    }

    public void onBeforeShowPage() {
        if (useLoadingIndicator) {
            progressIndicatorPopup.show();
        }
    }

    public void onAfterShowPage() {
        if (useLoadingIndicator) {
            progressIndicatorPopup.hide();
        }
    }

    public void showPage(List<String> params) {
        this.show();
    }

    public boolean isUseLoadingIndicator() {
        return useLoadingIndicator;
    }

    /**
     * Set to false if you don't want to use a standard 'loading' popup.
     * @param useLoadingIndicator default is true
     */
    public void setUseLoadingIndicator(boolean useLoadingIndicator) {
        this.useLoadingIndicator = useLoadingIndicator;
    }

    public void hidePage() {
        // no-op
    }
}
