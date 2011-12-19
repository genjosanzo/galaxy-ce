package org.mule.galaxy.web.client.ui.panel;

import org.mule.galaxy.web.client.ui.util.Images;

import com.google.gwt.user.client.ui.Image;

public class LoadingIndicatorPanel extends PaddedContentPanel {

	public LoadingIndicatorPanel() {
		// setLayout(new CenterLayout());
		this("loading-panel-indicatior");
	}

	public LoadingIndicatorPanel(String styleName) {
		Image i = new Image(Images.LOADING_INDICATOR);
		i.setStyleName(styleName);
		add(i);
		setStyleName(styleName);
		hide();
	}

	public void stop() {
		hide();
	}

	// load the spinning orb
	public void start() {
		show();
	}

}
