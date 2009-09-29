package org.mule.galaxy.web.client.ui;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;

public interface ButtonIcons extends ImageBundle {

    @Resource("images/delete.gif")
    AbstractImagePrototype delete();

    @Resource("images/add.gif")
    AbstractImagePrototype add();

}
