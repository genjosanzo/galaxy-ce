package org.mule.galaxy.util;

import java.util.Collections;
import java.util.Set;
import java.util.Stack;

import org.mule.galaxy.impl.jcr.UserDetailsWrapper;
import org.mule.galaxy.security.Permission;
import org.mule.galaxy.security.User;
import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

public final class SecurityUtils {
    public static final User SYSTEM_USER = new User("system");
    private static final ThreadLocal<Stack<SecurityContext>> contexts = new ThreadLocal<Stack<SecurityContext>>();
    
    static {
        SYSTEM_USER.setId("system");
        SYSTEM_USER.setName("System");
    }
    
    public static User getCurrentUser() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        return getUserFromSecurityContext(ctx);
    }

    public static User getLoggedInUser() {
        Stack<SecurityContext> contextStack = contexts.get();
        if (contextStack == null || contextStack.isEmpty()) {
            return null;
        }
        SecurityContext ctx = contextStack.lastElement();
        User user = getUserFromSecurityContext(ctx);
        if (user == null) {
            user = getCurrentUser();
        }
        return user;
    }

    private static User getUserFromSecurityContext(SecurityContext ctx) {
        if (ctx == null) {
            return null;
        }
        Authentication auth = ctx.getAuthentication();
        if (auth == null) {
            return null;
        }
        
        Object principal = auth.getPrincipal();
        if (principal instanceof String) {
            return SYSTEM_USER;
        }
        UserDetailsWrapper wrapper = (UserDetailsWrapper) principal;
        if (wrapper == null) {
            return null;
        }
        return wrapper.getUser();
    }
    
    public static void startDoPrivileged() {
        SecurityContext prevContext = SecurityContextHolder.getContext();

        Set<Permission> perms = Collections.emptySet();
        UserDetailsWrapper wrapper = new UserDetailsWrapper(SYSTEM_USER, perms, "");
        Authentication auth = new RunAsUserToken("system", wrapper, "", Collections.<GrantedAuthority>emptyList(), UsernamePasswordAuthenticationToken.class);
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
