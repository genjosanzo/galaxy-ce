package org.mule.galaxy.security.ldap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.acegisecurity.ldap.LdapTemplate;
import org.acegisecurity.ldap.LdapUserSearch;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.acegisecurity.userdetails.ldap.LdapUserDetails;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.impl.jcr.UserDetailsWrapper;
import org.mule.galaxy.security.AccessControlManager;
import org.mule.galaxy.security.Group;
import org.mule.galaxy.security.User;
import org.mule.galaxy.security.UserExistsException;
import org.mule.galaxy.security.UserManager;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.DataAccessException;

public class LdapUserManager 
    implements UserManager, UserDetailsService, ApplicationContextAware {

    private String emailAttribute = "email";
    private String groupSearchBase;
    private String groupSearchFilter = "(uniqueMemeber={0})";
    
    /**
     * The ID of the attribute which contains the role name for a group
     */
    private String groupRoleAttribute = "cn";
    
    private LdapUserSearch userSearch;
    private LdapTemplate ldapTemplate;

    private AccessControlManager accessControlManager;
    private ApplicationContext applicationContext;
    
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException,
        DataAccessException {
        try {
            User user = get(username);
            
            return new UserDetailsWrapper(user, 
                                          getAccessControlManager().getGrantedPermissions(user), 
                                          "secret");
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
        LdapUserDetails d = userSearch.searchForUser(id);
        
        Set groupNames = 
            ldapTemplate.searchForSingleAttributeValues(groupSearchBase, 
                                                        groupSearchFilter,
                                                        new String[] {d.getDn(), 
                                                                       id}, 
                                                        groupRoleAttribute);

        Set<Group> groups = new HashSet<Group>();
        for (Object o : groupNames) {
            try {
                groups.add(getAccessControlManager().getGroupByName(o.toString()));
            } catch (NotFoundException e) {
                System.out.println("Didn't find group " + o.toString());
            }
        }
        
        if (d == null) {
            throw new NotFoundException(id);
        }
        
        User user = new User();
        user.setId(id);
        user.setUsername(id);
        user.setGroups(groups);
        
        Attributes atts = d.getAttributes();
        if (atts != null)
        {
            Attribute att = atts.get(emailAttribute);
            if (att != null)
            {
                user.setEmail(att.toString());
            }
            
        }
        return user;
    }

    public List<User> listAll() {
        return new ArrayList<User>();
    }

    public void save(User t) {
        // TODO Auto-generated method stub
        
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

    private AccessControlManager getAccessControlManager() {
        if (accessControlManager == null) {
            accessControlManager =(AccessControlManager) applicationContext.getBean("accessControlManager");
        }
        return accessControlManager;
    }
    
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.applicationContext = ctx;
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

    public void setLdapTemplate(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

}
