package org.mule.galaxy.security.ldap;

import java.util.HashSet;
import java.util.Set;

import javax.naming.NamingException;

import org.acegisecurity.AuthenticationCredentialsNotFoundException;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.ldap.LdapEntryMapper;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.ldap.LdapAuthenticationProvider;
import org.acegisecurity.providers.ldap.LdapAuthenticator;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.ldap.LdapUserDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.impl.jcr.UserDetailsWrapper;
import org.mule.galaxy.security.AccessControlManager;
import org.mule.galaxy.security.Group;
import org.mule.galaxy.security.User;


/**
 * Custom ldap authentication provider for populating the Galaxy
 * User with granted authorities and permissions. 
 * 
 * @author: Brian Lichtenwalter 
 */
public class GalaxyAuthenticationProvider extends LdapAuthenticationProvider {

    private final Log log = LogFactory.getLog(getClass());
    private AccessControlManager accessControlManager;
    private LdapEntryMapper userMapper;
    private String requiredAuthority;
    
    public GalaxyAuthenticationProvider(LdapAuthenticator authenticator, LdapAuthoritiesPopulator authoritiesPopulator) {
        super(authenticator, authoritiesPopulator);
    }

    public void setAccessControlManager(AccessControlManager accessControlManager) {
        this.accessControlManager = accessControlManager;
    }

    public void setUserMapper(LdapEntryMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        super.additionalAuthenticationChecks(userDetails, authentication);
        
        GrantedAuthority[] authorities = userDetails.getAuthorities();
        boolean found = false;
        if (authorities != null && requiredAuthority != null) {
            for (GrantedAuthority auth : authorities) {
                if (auth.getAuthority().equals(requiredAuthority)) {
                    found = true;  
                    break;
                }
            }
        }
        if (!found) {
            throw new AuthenticationCredentialsNotFoundException("Invalid username/password.");
        }
    }

    public UserDetails createUserDetails(LdapUserDetails ldapUser, String username, String password) {

        LdapUserDetails user = (LdapUserDetails) super.createUserDetails(ldapUser, username, password);

        User userObj;
        try {
            userObj = (User) userMapper.mapAttributes(ldapUser.getDn(), ldapUser.getAttributes());
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        
        UserDetailsWrapper wrapper = new UserDetailsWrapper(userObj, null, password);
        wrapper.setAuthorities(user.getAuthorities());
        wrapper.setAttributes(user.getAttributes());
        wrapper.setControls(user.getControls());
        wrapper.setDn(user.getDn());

        Set<Group> galaxyGroups = new HashSet<Group>();
        for (GrantedAuthority authority : user.getAuthorities()) {
            try {
                galaxyGroups.add(accessControlManager.getGroupByName(authority.toString()));
            } catch (NotFoundException ex) {
                log.warn("Galaxy group not found " + authority.toString());
            }
        }
        wrapper.getUser().setGroups(galaxyGroups);
        wrapper.setPermissions(accessControlManager.getGrantedPermissions(wrapper.getUser()));

        return wrapper;
    }

    public void setRequiredAuthority(String requiredAuthority) {
        this.requiredAuthority = requiredAuthority;
    }
    
}
