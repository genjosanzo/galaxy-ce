package org.mule.galaxy.impl.jcr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.naming.directory.Attributes;
import javax.naming.ldap.Control;

import org.mule.galaxy.security.Permission;
import org.mule.galaxy.security.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapUserDetails;

public class UserDetailsWrapper implements LdapUserDetails {

    private static final long serialVersionUID = 1L;

    private User user;
    private String password;
    private Set<Permission> permissions;
    private GrantedAuthority[] authorities;
    private Attributes attributes;
    private Control[] controls;
    private String userDn;

    public UserDetailsWrapper(User user, Set<Permission> set, String password) {
        super();
        this.user = user;
        this.permissions = set;
        this.password = password;
    }

    public User getUser() {
        return user;
    }

    public Collection<GrantedAuthority> getAuthorities() {
        if (authorities == null) {
            Object[] pArray = permissions.toArray();
            authorities = new GrantedAuthority[pArray.length+1];
            for (int i = 0; i < pArray.length; i++) {
                authorities[i] = new SimpleGrantedAuthority(pArray[i].toString());
            }
            authorities[pArray.length] = new SimpleGrantedAuthority("role_user");
        }
        return Arrays.asList(authorities);
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

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public Control[] getControls() {
        return controls;
    }

    public void setControls(Control[] controls) {
        this.controls = controls;
    }

    public String getDn() {
        return userDn;
    }

    public void setDn(String dn) {
        userDn = dn;
    }

    public void setAuthorities(final GrantedAuthority[] auths) {
        final List<GrantedAuthority> list = new ArrayList<GrantedAuthority>(Arrays.asList(auths));
        list.add(new SimpleGrantedAuthority("role_user"));
        authorities = (GrantedAuthority[]) list.toArray(new GrantedAuthority[list.size()]);
    }

    public void setPermissions(Set<Permission> set) {
        permissions = set;
    }
}
