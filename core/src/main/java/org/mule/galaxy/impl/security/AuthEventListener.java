package org.mule.galaxy.impl.security;

import org.mule.galaxy.activity.ActivityManager;
import org.mule.galaxy.activity.ActivityManager.EventType;
import org.mule.galaxy.util.SecurityUtils;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.access.event.AbstractAuthorizationEvent;
import org.springframework.security.access.event.AuthorizedEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.util.ClassUtils;

public class AuthEventListener implements ApplicationListener {
    
    private ActivityManager activityManager;

    public void onApplicationEvent(ApplicationEvent event) {
        String message;
        WebAuthenticationDetails details;
        String source = event.getSource().toString();
        if (event instanceof AbstractAuthenticationEvent) {
            AbstractAuthenticationEvent authEvent = (AbstractAuthenticationEvent) event;
            details = (WebAuthenticationDetails) authEvent.getAuthentication().getDetails();
            String username = authEvent.getAuthentication().getName();
            if (event instanceof AbstractAuthenticationFailureEvent) {
                // Interactive web console or REST API authentication failure.
                message = "Authentication failure for user: " + username + "; "
                    + ((AbstractAuthenticationFailureEvent) event).getException().getMessage() + "; details: " + details;
                activityManager.logActivity(message, EventType.WARNING, SecurityUtils.getCurrentUser(), null);
            } else if (event instanceof InteractiveAuthenticationSuccessEvent) {
                // Interactive web console authentication was successful.
                message = "Authenticated user " + username + "; details: " + details + "; "
                    + ClassUtils.getShortName(authEvent.getClass());
                activityManager.logActivity(message, EventType.INFO, SecurityUtils.getCurrentUser(), null);
            }
        } else if (event instanceof AbstractAuthorizationEvent) {
            if (event instanceof AuthorizedEvent) {
                AuthorizedEvent authEvent = (AuthorizedEvent) event;
                details = (WebAuthenticationDetails) authEvent.getAuthentication().getDetails();
                if (details.toString().contains("SessionId: null")) {
                    // Rest API authentication was successful.
                    String username = authEvent.getAuthentication().getName();
                    message = "Authenticated user " + username + "; details: " + details + "; source: " + source + "; "
                        + ClassUtils.getShortName(authEvent.getClass());
                    activityManager.logActivity(message, EventType.INFO, SecurityUtils.getCurrentUser(), null);
                }
            }
        }
    }

    public ActivityManager getActivityManager() {
        return activityManager;
    }

    public void setActivityManager(ActivityManager activityManager) {
        this.activityManager = activityManager;
    }
}