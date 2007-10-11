package org.mule.galaxy.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.mule.galaxy.ContentHandler;

public abstract class AbstractContentHandler implements ContentHandler {

    public InputStream read(Object data) throws IOException {
        final File temp = File.createTempFile("galaxyOut", "tmp");
        FileOutputStream fileOutputStream = new FileOutputStream(temp);
        try {
            write(data, fileOutputStream);
        } finally {
            fileOutputStream.close();
        }
        
        return new FileInputStream(temp) {
            @Override
            public void close() throws IOException {
                super.close();
                
                temp.delete();
            }
        };
    }

}
