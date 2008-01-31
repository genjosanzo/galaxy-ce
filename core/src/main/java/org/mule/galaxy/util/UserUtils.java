package org.mule.galaxy.util;

import org.mule.galaxy.api.security.User;
import org.mule.galaxy.impl.jcr.UserDetailsWrapper;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;

public final class UserUtils {

    public static User getCurrentUser() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        if (ctx == null) {
            return null;
        }
        Authentication auth = ctx.getAuthentication();
        if (auth == null) {
            return null;
        }
        
        UserDetailsWrapper wrapper = (UserDetailsWrapper) auth.getPrincipal();
        if (wrapper == null) {
            return null;
        }
        return wrapper.getUser();
    }
    
}
