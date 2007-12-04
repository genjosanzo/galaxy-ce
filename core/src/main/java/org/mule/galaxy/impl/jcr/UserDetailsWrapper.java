package org.mule.galaxy.impl.jcr;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.userdetails.UserDetails;
import org.mule.galaxy.security.User;

public class UserDetailsWrapper implements UserDetails {
    private User user;
    private String password;
    
    public UserDetailsWrapper(User user, String password) {
        super();
        this.user = user;
        this.password = password;
    }

    public User getUser() {
        return user;
    }

    public GrantedAuthority[] getAuthorities() {
        return new GrantedAuthority[] {
            new GrantedAuthorityImpl("ROLE_USER")
        };
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return user.getUsername();
    }

    public boolean isAccountNonExpired() {
        return user.isEnabled();
    }

    public boolean isAccountNonLocked() {
        return user.isEnabled();
    }

    public boolean isCredentialsNonExpired() {
        return user.isEnabled();
    }

    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return user.isEnabled();
    }

}
