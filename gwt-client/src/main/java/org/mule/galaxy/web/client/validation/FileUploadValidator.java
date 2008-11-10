/*
 * $Id: MinLengthValidator.java 948 2008-05-23 20:25:59Z andrew $
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

package org.mule.galaxy.web.client.validation;

import org.mule.galaxy.web.client.util.StringUtil;

import com.google.gwt.user.client.ui.FileUpload;

public class FileUploadValidator implements Validator {


    protected static final int MIN_FILE_EXTENSION_LENGTH = 1;
    protected static final int MAX_FILE_EXTENSION_LENGTH = 5;
    private String failureMessage;

    public FileUploadValidator() {
    }


    public boolean validate(final Object value) {
        if (value == null) {
            return false;
        }

        if (!(value instanceof FileUpload)) {
            // GWT doesn't emulate Object.getClass()
            throw new IllegalArgumentException("This validator accepts FileUpload objects only. Got " + value.toString());
        }

        FileUpload f = (FileUpload) value;
        if (f.getFilename().length() == 0) {
            this.setFailureMessage("Filename is empty.");
            return false;
        }

        String e = StringUtil.getFileExtension(f.getFilename());
        if (e.length() < MIN_FILE_EXTENSION_LENGTH || e.length() > MAX_FILE_EXTENSION_LENGTH) {
            this.setFailureMessage("Filename extension is not valid.");
            return false;
        }

        return true;
    }

    public String getFailureMessage() {
        return this.failureMessage;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

}