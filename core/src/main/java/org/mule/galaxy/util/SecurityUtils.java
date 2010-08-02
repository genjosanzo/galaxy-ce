package org.mule.galaxy.util;

import java.util.Collections;
import java.util.Set;
import java.util.Stack;

import org.acegisecurity.Authentication;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.runas.RunAsUserToken;
import org.mule.galaxy.impl.jcr.UserDetailsWrapper;
import org.mule.galaxy.security.Permission;
import org.mule.galaxy.security.User;

public final class SecurityUtils {
    public static final User SYSTEM_USER = new User("system");
    private static final ThreadLocal<Stack<Authentication>> authentications = new ThreadLocal<Stack<Authentication>>();
    
    static {
        SYSTEM_USER.setId("system");
        SYSTEM_USER.setName("System");
    }
    
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

    public static void startDoPrivileged() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication prevAuth = context.getAuthentication();
        if (prevAuth == null) {
            throw new IllegalStateException("Previous authorization cannot be null");
        }

        Stack<Authentication> stack = authentications.get();
        if (stack == null) {
            stack = new Stack<Authentication>();
            authentications.set(stack);
        }
        
        Set<Permission> perms = Collections.emptySet();
        UserDetailsWrapper wrapper = new UserDetailsWrapper(SYSTEM_USER, perms, "");
        Authentication auth = new RunAsUserToken("system", wrapper, "", new GrantedAuthority[0], User.class);
        context.setAuthentication(auth);
        
        stack.push(prevAuth);
    }

    public static void endDoPrivileged() {
        Stack<Authentication> stack = authentications.get();
        if (stack == null || stack.size() == 0) {
            throw new IllegalStateException("Previous authorization cannot be null");
        }
        
        Authentication auth = stack.pop();
        
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);
        
        if (stack.size() == 0) {
            authentications.set(null);
        }
    }
    
    public static void doPrivileged(Runnable runnable) {
        try {
            startDoPrivileged();
            runnable.run();
        } finally {
            endDoPrivileged();
        }
    }

}
