package org.mule.galaxy.web.client.ui.panel;

import org.mule.galaxy.web.client.ui.ProgressIndicatorPopup;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

import java.util.List;

/**
 * Base class if you want to make your component a standalone page
 * that can be loaded with parameters. To register as a page,
 * see {@link org.mule.galaxy.web.client.Galaxy}.createPageInfo.
 *
 */
public abstract class AbstractShowableContainer extends LayoutContainer implements Showable {

    // we could provide a method to overload and create custom progress dialogs, but no need yet ;)
    protected ProgressIndicatorPopup progressIndicatorPopup = new ProgressIndicatorPopup();

    private boolean useLoadingIndicator = true;

    public void show() {
        onBeforeShowPage();
        doShowPage();
        onAfterShowPage();
    }

    public void doShowPage() {
        // no-op
    }

    public void onBeforeShowPage() {
        if (useLoadingIndicator) {
            // progressIndicatorPopup.show();
        }
    }

    public void onAfterShowPage() {
        if (useLoadingIndicator) {
            // progressIndicatorPopup.hide();
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

    public void clear() {
        removeAll();
    }


}
