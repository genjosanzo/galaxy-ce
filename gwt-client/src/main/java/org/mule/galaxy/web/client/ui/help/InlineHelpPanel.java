package org.mule.galaxy.web.client.ui.help;

import org.mule.galaxy.web.client.WidgetHelper;
import org.mule.galaxy.web.client.util.StringUtil;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;


/**
 * ContenPanel has base styles hard coded (funky stuff in onRender too) so it needed to be extended
 */
public class InlineHelpPanel extends ContentPanel {

    private String status;

    public InlineHelpPanel() {
        super();
        baseStyle = "help-panel-inline";
        setDeferHeight(false);
    }

    public InlineHelpPanel(String content, int num) {
        this();
        setBorders(false);
        setTitleCollapse(true);
        setCollapsible(true);
        setHideCollapseTool(true);
        setAutoWidth(true);
        setAnimCollapse(true);
        collapse();
        setContent(content, num);
    }

    public void setContent(String content, int num) {
        clearState();
        String[] sa;
        if (num == -1) {
            // don't split it, there is content for the body...
            sa = new String[]{content, null};
        } else {
            sa = StringUtil.wordCountSplitter(content, num, true);
        }
        final String header = sa[0];
        final String body = sa[1];

        addListener(Events.Expand, new Listener<ComponentEvent>() {

            public void handleEvent(ComponentEvent ce) {
                String s = header;
                if (body != null) {
                    s = s + WidgetHelper.createFauxLink(" [less]");
                    getHeader().setToolTip("Click to Collapse.");
                }
                setHeading(s);
            }
        });
        addListener(Events.Collapse, new Listener<ComponentEvent>() {

            public void handleEvent(ComponentEvent ce) {
                String s = header;
                if (body != null) {
                    s = s + WidgetHelper.createFauxLink(" [more]");
                    getHeader().setToolTip("Click to Expand.");
                }
                setHeading(s);
            }
        });
        addText(body);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
