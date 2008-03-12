package org.mule.galaxy.impl.jcr;

import java.util.ArrayList;
import java.util.Set;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.userdetails.UserDetails;
import org.mule.galaxy.security.Permission;
import org.mule.galaxy.security.User;

public class UserDetailsWrapper implements UserDetails {
    private User user;
    private String password;
    private Set<Permission> permissions;
    
    public UserDetailsWrapper(User user, Set<Permission> set, String password) {
        super();
        this.user = user;
        this.permissions = set;
        this.password = password;
    }

    public User getUser() {
        return user;
    }

    public GrantedAuthority[] getAuthorities() {
        ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (Permission p : permissions) {
            authorities.add(new GrantedAuthorityImpl(p.getName()));
        }
        authorities.add(new GrantedAuthorityImpl("role_user"));
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
