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

package org.mule.galaxy.web.rpc;

import org.mule.galaxy.web.client.RPCException;
import org.mule.galaxy.web.client.ui.help.PanelConstants;
import org.mule.galaxy.web.client.ui.panel.ErrorPanel;

import com.extjs.gxt.ui.client.widget.Text;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * Default {@link AsyncCallback} implementation with support for long running call detection.
 * <br />
 * <br />
 * <bold>Caution</bold>
 * Because an internal {@link Timer} is scheduled in default constructor this {@link AbstractCallback} should only be created at usage time.
 *
 * @param <T>
 */
public abstract class AbstractCallback<T> implements AsyncCallback<T> {

    private final ErrorPanel errorPanel;
    private static final PanelConstants panelMessages = (PanelConstants) GWT.create(PanelConstants.class);
    private final Timer longRunningCallTimer = new Timer() {
        @Override
        public void run() {
            message.setText(createLongRunningCallErrorMessage());
            setErrorMessage(message);
        }
    };
    private Text message = new Text();
    private final int longRunningCallTimeout;
    private static final int DEFAULT_LONG_RUNNING_CALL_TIMEOUT = 30000;
    private static final String DEFAULT_LONG_RUNNING_CALL_ERROR_MESSAGE = panelMessages.serverTakingLonger();

    public AbstractCallback(final ErrorPanel panel) {
        this(panel, AbstractCallback.DEFAULT_LONG_RUNNING_CALL_TIMEOUT);
    }

    public AbstractCallback(final ErrorPanel panel, boolean enbableLongRunningCallback) {
        this(panel, AbstractCallback.DEFAULT_LONG_RUNNING_CALL_TIMEOUT, enbableLongRunningCallback);
    }

    public AbstractCallback(final ErrorPanel panel, final int longRunningCallTimeout) {
        this(panel, longRunningCallTimeout, true);
    }

    public AbstractCallback(final ErrorPanel panel, final int longRunningCallTimeout, boolean enbableLongRunningCallback) {
        this.errorPanel = panel;
        this.longRunningCallTimeout = longRunningCallTimeout;

        if(enbableLongRunningCallback) {
            startLongRunningCallTimer();
        }
    }

    protected final void startLongRunningCallTimer() {
        this.longRunningCallTimer.schedule(this.longRunningCallTimeout);
    }

    protected final void cancelLongRunningCallTimer() {
        this.longRunningCallTimer.cancel();
        if (message.isAttached()) {
            removeMessage(message);
        }
    }

    protected String createLongRunningCallErrorMessage() {
        return AbstractCallback.DEFAULT_LONG_RUNNING_CALL_ERROR_MESSAGE;
    }

    /**
     *
     * Creates an appropriate error message from provided {@link Throwable}.
     *
     * @param caught
     * @return
     */
    protected String createErrorMessageFromException(final Throwable caught) {
        final String exceptionMessage = caught.getMessage();
        String errorMessage;
        if (caught instanceof InvocationException && !(caught instanceof StatusCodeException)) {
            // happens after server is back online, and got a forward to a login page
            // typically would be displayed with a session killed dialog
            errorMessage = panelMessages.currentSession();
        }else if (caught instanceof RPCException) {
            errorMessage = "An unespected error has occurred: ";
            if (caught.getMessage() != null) {
                errorMessage +=  caught.getMessage();
            }
            if (caught.getCause() != null) {
                errorMessage += ".Caused by :" + caught.getCause().getClass().toString();
            }
        }else if (exceptionMessage != null && !"".equals(exceptionMessage)) {
            errorMessage = panelMessages.errorCommunicatingServer() + exceptionMessage;
        } else {
            errorMessage = panelMessages.errorCommunicatingExeption() + caught.getClass().getName();
        }
        return errorMessage;
    }

    protected void onCallFailure(final Throwable caught) {
        setErrorMessage(createErrorMessageFromException(caught));
        
        if (caught instanceof RPCException) {
            Label lbl = new Label("[See more detail]");
            lbl.addClickHandler(new ClickHandler(){

                public void onClick(ClickEvent event) {    
                    createWindow(((RPCException) caught).getStacktrace());
                }
                
            });
            lbl.setStyleName("a-faux-link");
            errorPanel.addMessage(lbl);
        }
        
    }
    
    public static native void createWindow(String contents) /*-{   
      var win = window.open("", "StackTrace", "scrollbars=1"); // a window object
    win.document.open("text/html", "replace");
    win.document.write("<HTML><HEAD><TITLE>StackTrace</TITLE></HEAD><BODY>"+ contents + "</BODY></HTML>");
    win.document.close(); 
    }-*/;

    /**
     * Do not call this method from {@link #onCallFailure(Throwable)} !
     */
    public final void onFailure(final Throwable caught) {
        cancelLongRunningCallTimer();
        onCallFailure(caught);
    }

    protected abstract void onCallSuccess(final T result);

    /**
     * Do not call this method from {@link #onCallSuccess(Object)} !
     */
    public final void onSuccess(final T result) {
        cancelLongRunningCallTimer();
        onCallSuccess(result);
    }

    /**
    *
    * Display an error message.
    *
    * @see ErrorPanel#setMessage(String)
    * @param message
    */
   public Widget setErrorMessage(final String message) {
       if (errorPanel != null) {
           return errorPanel.setMessage(message);
       }
       return null;
   }

   public void setErrorMessage(final Widget message) {
       if (errorPanel != null) {
           errorPanel.setMessage(message);
       }
   }

   public void removeMessage(final Widget message) {
       if (errorPanel != null) {
           errorPanel.removeMessage(message);
       }
   }

}
