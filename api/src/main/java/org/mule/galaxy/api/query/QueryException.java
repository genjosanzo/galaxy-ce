package org.mule.galaxy.api.query;

import org.mule.galaxy.api.RegistryException;
import org.mule.galaxy.api.util.Message;

public class QueryException extends RegistryException
{

    public QueryException(Message message) {
        super(message);
    }

}
