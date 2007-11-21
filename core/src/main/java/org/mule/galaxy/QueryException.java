package org.mule.galaxy;

import org.mule.galaxy.util.Message;

public class QueryException extends RegistryException {

    public QueryException(Message message) {
        super(message);
    }

}
