package org.mule.galaxy.util;

import javax.jcr.Session;

import org.springmodules.jcr.SessionHolder;
import org.springmodules.jcr.jackrabbit.support.UserTxSessionHolder;

public class JackrabbitSessionFactory extends org.springmodules.jcr.jackrabbit.JackrabbitSessionFactory {

    @Override
    public SessionHolder getSessionHolder(Session session) {
        return new UserTxSessionHolder(session);
    }

}
