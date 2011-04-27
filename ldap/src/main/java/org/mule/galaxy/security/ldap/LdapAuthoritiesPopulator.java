package org.mule.galaxy.security.ldap;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.security.AccessControlManager;
import org.mule.galaxy.security.Group;
import org.springframework.ldap.core.ContextSource;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;

/**
 * Custom authorities populator for producing an intersection of
 * Galaxy and Ldap granted authorities.
 *  
 * @author: Brian Lichtenwalter 
 */
public class LdapAuthoritiesPopulator extends DefaultLdapAuthoritiesPopulator { 

    private final Log log = LogFactory.getLog(getClass());
    private AccessControlManager accessControlManager;

    public LdapAuthoritiesPopulator(ContextSource initialDirContextFactory, String groupSearchBase) {
        super(initialDirContextFactory, groupSearchBase);
    }

    public void setAccessControlManager(AccessControlManager accessControlManager) {
        this.accessControlManager = accessControlManager;
    }

    public Set getGroupMembershipRoles(String userDn, String username) {

        Object[] ldapAuthorities = super.getGroupMembershipRoles(userDn, username).toArray();

        Set commonGroups = new HashSet();
        for (Group galaxyGroup : accessControlManager.getGroups()) {
            for (Object grantedAuthority : ldapAuthorities) {
                if (galaxyGroup.getName().equals(grantedAuthority.toString())) {
                    commonGroups.add(grantedAuthority);
                    break;
                }
            }
        }
        log.debug("Group intersection " + commonGroups);

        return commonGroups;
    }
}
