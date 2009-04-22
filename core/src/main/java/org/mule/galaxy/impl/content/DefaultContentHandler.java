package org.mule.galaxy.impl.content;

import org.mule.galaxy.Item;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.MimeType;

public class DefaultContentHandler extends AbstractContentHandler {

    public MimeType getContentType(Object o) {
        return null;
    }

    public String getName(Object o) {
        return null;
    }

    public Object read(InputStream stream, Item workspace) throws IOException {
        return null;
    }

    public void write(Object o, OutputStream stream) throws IOException {
    }

}
