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

package org.mule.galaxy.web.client.ui;

import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * This is an adapted version of GWanTed's ExternalHyperlink (original class under
 * LGPL).
 */
public class ExternalHyperlink extends Component {
    private final Element anchorElem;

    public ExternalHyperlink(final String text, final String link) {
        this.anchorElem = getAnchorElement(text, link, null);
    }

    public ExternalHyperlink(final String text, final String link, final String target) {
        this.anchorElem = getAnchorElement(text, link, target);
    }

    private Element getAnchorElement(final String text,
                                     final String link,
                                     final String target) {
        setElement(DOM.createDiv());
        final Element anchorElement = DOM.createAnchor();
        DOM.appendChild(getElement(), anchorElement);
        setLink(link);
        setText(text);

        if (target != null) {
            setTarget(target);
        }
        return anchorElement;
    }
    
    public final void setId(String id) {
        anchorElem.setId(id);
    }

    public final void setText(final String text) {
        DOM.setInnerHTML(this.anchorElem, text);
    }

    public final void setLink(final String link) {
        DOM.setAttribute(this.anchorElem, "href", link);
    }

    public final String getText() {
        return DOM.getInnerHTML(this.anchorElem);
    }

    public final String getLink() {
        return DOM.getAttribute(this.anchorElem, "href");
    }

    public final String getTarget() {
        return DOM.getAttribute(this.anchorElem, "target");
    }

    public final void setTarget(final String target) {
        DOM.setAttribute(this.anchorElem, "target", target);
    }
}
