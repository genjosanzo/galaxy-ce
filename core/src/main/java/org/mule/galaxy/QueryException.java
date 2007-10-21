package org.mule.galaxy;

import org.mule.galaxy.util.Message;

public class QueryException extends GalaxyException {

    public QueryException(Message message) {
        super(message);
    }

}
