package org.mule.galaxy.query;

import org.mule.galaxy.RegistryException;
import org.mule.galaxy.util.Message;

public class QueryException extends RegistryException {

    public QueryException(Message message) {
        super(message);
    }

}
