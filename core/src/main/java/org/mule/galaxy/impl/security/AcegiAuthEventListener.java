package org.mule.galaxy.impl.security;

import org.acegisecurity.event.authentication.AbstractAuthenticationEvent;
import org.acegisecurity.event.authentication.AbstractAuthenticationFailureEvent;
import org.acegisecurity.event.authentication.InteractiveAuthenticationSuccessEvent;
import org.acegisecurity.event.authorization.AbstractAuthorizationEvent;
import org.acegisecurity.event.authorization.AuthorizationFailureEvent;
import org.acegisecurity.event.authorization.AuthorizedEvent;
import org.acegisecurity.ui.WebAuthenticationDetails;
import org.mule.galaxy.activity.ActivityManager;
import org.mule.galaxy.activity.ActivityManager.EventType;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.ClassUtils;

public class AcegiAuthEventListener implements ApplicationListener {
    
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
                activityManager.logActivity(message, EventType.WARNING);
            } else if (event instanceof InteractiveAuthenticationSuccessEvent) {
                // Interactive web console authentication was successful.
                message = "Authenticated user " + username + "; details: " + details + "; "
                    + ClassUtils.getShortName(authEvent.getClass());
                activityManager.logActivity(message, EventType.INFO);
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
                    activityManager.logActivity(message, EventType.INFO);
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