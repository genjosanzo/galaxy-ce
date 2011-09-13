package org.mule.galaxy.security.ldap;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.impl.jcr.UserDetailsWrapper;
import org.mule.galaxy.security.AccessControlManager;
import org.mule.galaxy.security.Group;
import org.mule.galaxy.security.User;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.authentication.LdapAuthenticator;

/**
 * Custom ldap authentication provider for populating the Galaxy
 * User with granted authorities and permissions. 
 * 
 * @author: Brian Lichtenwalter 
 */
public class GalaxyAuthenticationProvider extends LdapAuthenticationProvider {

    private final Log log = LogFactory.getLog(getClass());
    private AccessControlManager accessControlManager;
    private ContextMapper userMapper;
    private List<String> requiredAuthorities;
    
    public GalaxyAuthenticationProvider(LdapAuthenticator authenticator, LdapAuthoritiesPopulator authoritiesPopulator) {
        super(authenticator, authoritiesPopulator);
    }

    public void setAccessControlManager(AccessControlManager accessControlManager) {
        this.accessControlManager = accessControlManager;
    }

    public void setUserMapper(ContextMapper userMapper) {
        this.userMapper = userMapper;
    }

    protected void additionalAuthenticationChecks(final UserDetails userDetails,
                                                  final UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (this.requiredAuthorities != null && !this.requiredAuthorities.isEmpty()) {
	    	final Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
	        boolean found = false;
	        if (authorities != null) {
	            for (final GrantedAuthority auth : authorities) {
	                for (final String requiredAuthority : requiredAuthorities) {
	                    if (auth.getAuthority().equals(requiredAuthority)) {
	                        found = true;  
	                        break;
	                    }
	                }
	            }
	        }
	        if (!found) {
	            throw new AuthenticationCredentialsNotFoundException("User does not have sufficient authority.");
	        }
        }
    }

    @Override
    protected Authentication createSuccessfulAuthentication(final UsernamePasswordAuthenticationToken authentication, final UserDetails userDetails, final DirContextOperations userData) {
    	final UsernamePasswordAuthenticationToken successfulAuthentication = (UsernamePasswordAuthenticationToken) super.createSuccessfulAuthentication(authentication, userDetails, userData);

        final User user = (User) userMapper.mapFromContext(userData);
        
        final UserDetailsWrapper userDetailsWrapper = new UserDetailsWrapper(user, null, successfulAuthentication.getCredentials().toString());
        userDetailsWrapper.setAuthorities(successfulAuthentication.getAuthorities().toArray(new GrantedAuthority[successfulAuthentication.getAuthorities().size()]));

        final Set<Group> galaxyGroups = new HashSet<Group>();
        for (final GrantedAuthority authority : successfulAuthentication.getAuthorities()) {
            try {
                galaxyGroups.add(accessControlManager.getGroupByName(authority.toString()));
            } catch (NotFoundException ex) {
                log.warn("Galaxy group not found " + authority.toString());
            }
        }
        userDetailsWrapper.getUser().setGroups(galaxyGroups);
        userDetailsWrapper.setPermissions(accessControlManager.getGrantedPermissions(user));

        additionalAuthenticationChecks(userDetailsWrapper, successfulAuthentication);

        return new AuthenticationWrapper(successfulAuthentication, userDetailsWrapper);
    }

    public void setRequiredAuthorities(List<String> requiredAuthorities) {
        this.requiredAuthorities = requiredAuthorities;
    }

}
