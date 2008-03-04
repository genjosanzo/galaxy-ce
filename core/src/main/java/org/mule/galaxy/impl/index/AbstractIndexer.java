package org.mule.galaxy.impl.index;

import java.util.Map;
import java.util.ResourceBundle;

import org.mule.galaxy.index.IndexException;
import org.mule.galaxy.index.Indexer;
import org.mule.galaxy.util.Message;

public abstract class AbstractIndexer implements Indexer {
    public String getValue(Map<String, String> config, String key, Message message) throws IndexException {
        String val = config.get(key);
        
        if (val == null) {
            throw new IndexException(message);
        }
        
        return val;
    }
}
