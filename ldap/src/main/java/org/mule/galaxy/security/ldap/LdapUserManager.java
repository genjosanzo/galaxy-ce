package org.mule.galaxy.security.ldap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.acegisecurity.ldap.InitialDirContextFactory;
import org.acegisecurity.ldap.LdapTemplate;
import org.acegisecurity.ldap.LdapUserSearch;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.acegisecurity.userdetails.ldap.LdapUserDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.impl.jcr.UserDetailsWrapper;
import org.mule.galaxy.security.AccessControlManager;
import org.mule.galaxy.security.Group;
import org.mule.galaxy.security.Permission;
import org.mule.galaxy.security.User;
import org.mule.galaxy.security.UserExistsException;
import org.mule.galaxy.security.UserManager;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.DataAccessException;

public class LdapUserManager 
    implements UserManager, UserDetailsService, ApplicationContextAware {

    private final Log log = LogFactory.getLog(getClass());

    private String emailAttribute = "email";
    private String groupSearchBase;
    private String groupSearchFilter = "(uniqueMember={0})";
    
    /**
     * The ID of the attribute which contains the role name for a group
     */
    private String groupRoleAttribute = "cn";
    
    private LdapUserSearch userSearch;
    private LdapTemplate ldapTemplate;
    private InitialDirContextFactory initialDirContextFactory;
    private AccessControlManager accessControlManager;

    private ApplicationContext applicationContext;
    
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException,
        DataAccessException {
        try {
            return getUserDetails(username, true);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(username);
        }
    }

    public void setUserSearch(LdapUserSearch userSearch) {
        this.userSearch = userSearch;
    }

    public void delete(String id) {
        throw new UnsupportedOperationException();
    }

    public List<User> find(String property, String value) {
        throw new UnsupportedOperationException();
    }

    public User get(String id) throws NotFoundException {
        return getUserDetails(id, false).getUser();
    }
    
    public UserDetailsWrapper getUserDetails(String username, boolean fillInGroups) throws NotFoundException {
        LdapUserDetails d = userSearch.searchForUser(username);

        if (d == null) {
            throw new NotFoundException(username);
        }
        
        User user = new User();
        if (fillInGroups) {
            Set groupNames = 
                getLdapTemplate().searchForSingleAttributeValues(groupSearchBase, 
                                                                 groupSearchFilter,
                                                                 new String[] {d.getDn(), username}, 
                                                                 groupRoleAttribute);
    
            Set<Group> groups = new HashSet<Group>();
            for (Object o : groupNames) {
                try {
                    groups.add(getAccessControlManager().getGroupByName(o.toString()));
                } catch (NotFoundException e) {
                    log.info("Could not find group " + o.toString() + " for user " + username);
                }
            }
            
            user.setGroups(groups);
        }
        
        user.setId(username);
        user.setUsername(username);
         
        Attributes atts = d.getAttributes();
        if (atts != null)
        {
            Attribute att = atts.get(emailAttribute);
            if (att != null)
            {
                user.setEmail(att.toString());
            }
            
        }
        
        Set<Permission> grantedPermissions;
        if (fillInGroups) {
            grantedPermissions = getAccessControlManager().getGrantedPermissions(user);
        } else {
            grantedPermissions = new HashSet<Permission>();
        }
        
        return new UserDetailsWrapper(user, 
                                      grantedPermissions, 
                                      d.getPassword());
    }

    public List<User> listAll() {
        return new ArrayList<User>();
    }

    public void save(User t) {
        // TODO Auto-generated method stub
        
    }

    public Class<User> getTypeClass() {
        return User.class;
    }

    public User authenticate(String username, String password) {
        try {
            return get(username);
        } catch (NotFoundException e) {
            return null;
        }
    }

    public void create(User user, String password) throws UserExistsException {
        throw new UnsupportedOperationException();
    }

    public User getByUsername(String string) throws NotFoundException {
        return get(string);
    }

    public boolean setPassword(String username, String oldPassword, String newPassword) {
        throw new UnsupportedOperationException();
    }

    public void setPassword(User user, String password) {
        throw new UnsupportedOperationException();
    }

    public synchronized LdapTemplate getLdapTemplate() {
        if (ldapTemplate == null) {
            ldapTemplate = new LdapTemplate(initialDirContextFactory);
        }
        return ldapTemplate;
    }

    public void setAccessControlManager(AccessControlManager accessControlManager) {
        this.accessControlManager = accessControlManager;
    }

    private AccessControlManager getAccessControlManager() {
        if (accessControlManager == null) {
            accessControlManager = (AccessControlManager) applicationContext.getBean("accessControlManager");
        }
        return accessControlManager;
    }
    
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setInitialDirContextFactory(InitialDirContextFactory initialDirContextFactory) {
        this.initialDirContextFactory = initialDirContextFactory;
    }

    public void setEmailAttribute(String emailAttribute) {
        this.emailAttribute = emailAttribute;
    }

    public void setGroupSearchBase(String groupSearchBase) {
        this.groupSearchBase = groupSearchBase;
    }

    public void setGroupSearchFilter(String groupSearchFilter) {
        this.groupSearchFilter = groupSearchFilter;
    }

    public void setGroupRoleAttribute(String groupRoleAttribute) {
        this.groupRoleAttribute = groupRoleAttribute;
    }

}
