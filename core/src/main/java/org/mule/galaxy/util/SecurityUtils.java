package org.mule.galaxy.util;

import java.util.Collections;
import java.util.Set;
import java.util.Stack;

import org.acegisecurity.Authentication;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.context.SecurityContextImpl;
import org.acegisecurity.runas.RunAsUserToken;
import org.mule.galaxy.impl.jcr.UserDetailsWrapper;
import org.mule.galaxy.security.Permission;
import org.mule.galaxy.security.User;

public final class SecurityUtils {
    public static final User SYSTEM_USER = new User("system");
    private static final ThreadLocal<Stack<SecurityContext>> contexts = new ThreadLocal<Stack<SecurityContext>>();
    
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
        SecurityContext prevContext = SecurityContextHolder.getContext();

        Set<Permission> perms = Collections.emptySet();
        UserDetailsWrapper wrapper = new UserDetailsWrapper(SYSTEM_USER, perms, "");
        Authentication auth = new RunAsUserToken("system", wrapper, "", new GrantedAuthority[0], User.class);
        SecurityContextImpl context = new SecurityContextImpl();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        
        if (prevContext != null) {
            Stack<SecurityContext> stack = contexts.get();
            if (stack == null) {
                stack = new Stack<SecurityContext>();
                contexts.set(stack);
            }
            stack.push(prevContext);
        }
    }

    public static void endDoPrivileged() {
        Stack<SecurityContext> stack = contexts.get();
        SecurityContext context = null;
        if (stack != null && stack.size() > 0) {
            context = stack.pop();

            if (stack.size() == 0) {
                contexts.set(null);
            }
        }
        if (context == null) {
            SecurityContextHolder.clearContext();
        } else {
            SecurityContextHolder.setContext(context);
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
