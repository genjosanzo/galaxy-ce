package org.mule.galaxy.web.client.util;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is an adapted version of GWanTed's ExternalHyperlink (original class under LGPL).
 */
public class ExternalHyperlink extends Widget
{
    private final Element anchorElem;

    public ExternalHyperlink(final String text, final String link)
    {
        this(text, link, null);
    }

    public ExternalHyperlink(final String text, final String link,
                             final String target)
    {
        super();

            setElement(DOM.createDiv());
            this.anchorElem = DOM.createAnchor();
            DOM.appendChild(getElement(), this.anchorElem);
            setLink(link);
            setText(text);

            if (target != null)
            {
                setTarget(target);
            }
    }

    public final void setText(final String text)
    {
        DOM.setInnerHTML(this.anchorElem, text);
    }

    public final void setLink(final String link)
    {
        DOM.setAttribute(this.anchorElem, "href", link);
    }

    public final String getText()
    {
        return DOM.getInnerHTML(this.anchorElem);
    }

    public final String getLink()
    {
        return DOM.getAttribute(this.anchorElem, "href");
    }

    public final String getTarget()
    {
        return DOM.getAttribute(this.anchorElem, "target");
    }

    public final void setTarget(final String target)
    {
        DOM.setAttribute(this.anchorElem, "target", target);
    }
}

