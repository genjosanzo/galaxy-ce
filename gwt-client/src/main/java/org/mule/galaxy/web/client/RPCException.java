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

import com.google.gwt.user.client.rpc.IsSerializable;

public class RPCException extends Exception implements IsSerializable {

    private static final long serialVersionUID = 1L;
    
    private String stacktrace;
    private String highLevelDescription;

    public RPCException() {
    }

    public RPCException(final String message) {
        super(message);
    }

    
    /**
     * Exception to encapsulate most of the error from server services
     * the stacktrace is translated in order to see the exception from the client UI.
     * @param message Brief message that explains the current error
     * @param cause Exception that caused this exception
     * 
     */
    
    public RPCException(final String message, final Throwable cause) {
        super(message, cause);
           setStacktrace(translateExceptionToString(cause));
    }
    

    /**
     * Exception to encapsulate most of the error from server services
     * the stacktrace is translated in order to see the exception from the client UI.
     * @param message Brief message that explains the current error
     * @param cause Exception that caused this exception
     * @param highLevelDesc Description in high level of what happened and optionally some tip about how to fix it.
     */
    
    public RPCException(final String message, final Throwable cause, String highLevelDesc) {
        super(message, cause);
           setStacktrace(translateExceptionToString(cause));
           setHighLevelDescription(highLevelDesc);
    }
    
    private String translateExceptionToString(Throwable cause) {
        //TODO ivan: test what happens if something prints out to system.out at the same time
        String trace = "";
        
        cause.getClass().toString();
        
        trace += "<br/> <b> <font size=6 color= red> StackTrace: </font> </b>";
        
        for (int i = 0; i < cause.getStackTrace().length; i++) {
            trace += "<br/>" + cause.getStackTrace()[i].toString();
        }
        
           return trace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setHighLevelDescription(String highLevelDescription) {
        this.highLevelDescription = highLevelDescription;
    }

    public String getHighLevelDescription() {
        return highLevelDescription;
    }

}
