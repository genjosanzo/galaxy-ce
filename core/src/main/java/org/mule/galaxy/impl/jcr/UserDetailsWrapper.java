package org.mule.galaxy.impl.jcr;

import org.mule.galaxy.api.security.User;

import java.util.ArrayList;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.userdetails.UserDetails;

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
        ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (String role : user.getRoles()) {
            authorities.add(new GrantedAuthorityImpl(role));
        }
        return authorities.toArray(new GrantedAuthority[authorities.size()]);
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
