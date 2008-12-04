package org.mule.galaxy.util;

import org.mule.galaxy.security.User;

public class UserUtils {
    /**
     * Handle the case of creating a username for a User which does not exist.
     * @param user
     * @return
     */
    public static String getUsername(User user) {
        String username = "[User was deleted]";
        if (user != null) {
            username = user.getName();
        }
        return username;
    }
}
