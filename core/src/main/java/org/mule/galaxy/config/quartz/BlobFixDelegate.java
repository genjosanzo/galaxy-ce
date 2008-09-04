package org.mule.galaxy.config.quartz;

import org.quartz.impl.jdbcjobstore.CloudscapeDelegate;
import org.quartz.impl.jdbcjobstore.StdJDBCDelegate;
import org.apache.commons.logging.Log;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Blob;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * A driver delegate class to fix empty BLOB serialization bugs in Quartz persistence.
 * See <a href="http://forums.opensymphony.com/thread.jspa?messageID=87626">this thread</a>.
 */
public class BlobFixDelegate extends StdJDBCDelegate {

    public BlobFixDelegate(Log log, String s, String s1) {
        super(log, s, s1);
    }

    public BlobFixDelegate(Log log, String s, String s1, Boolean aBoolean) {
        super(log, s, s1, aBoolean);
    }

    @Override
    protected Object getObjectFromBlob(ResultSet resultSet, String columnName) throws ClassNotFoundException, IOException, SQLException {
        Object result = null;

        Blob blobLocator = resultSet.getBlob(columnName);
        if (blobLocator != null) {
            InputStream is = blobLocator.getBinaryStream();
            if (is != null && is.available() > 0) {
                ObjectInputStream ois = new ObjectInputStream(is);
                try {
                    result = ois.readObject();
                } finally {
                    ois.close();
                }
            }
        }

        return result;
    }
}
