package org.mule.galaxy.impl.content;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.MimeType;

import org.mule.galaxy.api.Workspace;

public class DefaultContentHandler extends AbstractContentHandler {

    public MimeType getContentType(Object o) {
        return null;
    }

    public String getName(Object o) {
        return null;
    }

    public Object read(InputStream stream, Workspace workspace) throws IOException {
        return null;
    }

    public void write(Object o, OutputStream stream) throws IOException {
    }

}
