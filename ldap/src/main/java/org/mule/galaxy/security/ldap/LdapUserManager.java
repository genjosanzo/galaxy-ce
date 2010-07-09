package org.mule.galaxy.security.ldap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;

import org.acegisecurity.ldap.InitialDirContextFactory;
import org.acegisecurity.ldap.LdapCallback;
import org.acegisecurity.ldap.LdapEntryMapper;
import org.acegisecurity.ldap.LdapTemplate;
import org.acegisecurity.ldap.LdapUserSearch;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.acegisecurity.userdetails.ldap.LdapUserDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.Dao;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.Results;
import org.mule.galaxy.impl.jcr.onm.DaoPersister;
import org.mule.galaxy.impl.jcr.onm.PersisterManager;
import org.mule.galaxy.security.User;
import org.mule.galaxy.security.UserExistsException;
import org.mule.galaxy.security.UserManager;
import org.mule.galaxy.util.SecurityUtils;
import org.springframework.dao.DataAccessException;

public class LdapUserManager 
    implements UserManager, UserDetailsService {

    private final Log log = LogFactory.getLog(getClass());

    private Map<String, String> userSearchAttributes;
    
    private String userSearchBase;
    private LdapUserSearch userSearch;
    private LdapEntryMapper userMapper;
    
    private LdapTemplate ldapTemplate;
    private InitialDirContextFactory initialDirContextFactory;
    private PersisterManager persisterManager;
    
    private Dao<LdapUserMetadata> ldapUserMetadataDao;
    
    public void initialize() throws Exception {
        persisterManager.getPersisters().put(User.class.getName(), new DaoPersister(this));
    }
    
    public void setPersisterManager(PersisterManager persisterManager) {
        this.persisterManager = persisterManager;
    }

    public void setUserSearch(LdapUserSearch userSearch) {
        this.userSearch = userSearch;
    }

    public void delete(String id) {
        throw new UnsupportedOperationException();
    }

    public long count(Map<String, Object> criteria) {
        return 0;
    }

    public List<User> find(String property, String value) {
        throw new UnsupportedOperationException();
    }

    public Results<User> find(Map<String, Object> criteria, int start, int count) {
        throw new UnsupportedOperationException();
    }

    public Results<User> find(Map<String, Object> criteria, String sortByField, boolean asc, int start, int count) {
        throw new UnsupportedOperationException();    
    }
    
    public User authenticate(String username, String password) {
        throw new UnsupportedOperationException();
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException,
        DataAccessException {
        // TODO Auto-generated method stub
        return null;
    }

    public User get(String id) throws NotFoundException {
        if (id.equals(SecurityUtils.SYSTEM_USER.getUsername())) {
            return SecurityUtils.SYSTEM_USER;
        }
        try {
            LdapUserDetails d = userSearch.searchForUser(id);

            User user = (User)userMapper.mapAttributes(d.getDn(), d.getAttributes());
            user.setProperties(getUserProperties(id));
            return user;
        } catch (UsernameNotFoundException e) {
            throw new NotFoundException(id);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> getUserProperties(String id) {
        try {
            LdapUserMetadata metadata = ldapUserMetadataDao.get(id);
            return metadata.getProperties();
        } catch (NotFoundException e) {
            return null;
        }
    }

    public List<User> listAll() {
        return (List<User>) getLdapTemplate().execute(new LdapCallback() {

            public Object doInDirContext(DirContext dirContext) throws NamingException {
                List<User> users = new ArrayList<User>();
                
                BasicAttributes atts = new BasicAttributes();
                for (Map.Entry<String, String> e : userSearchAttributes.entrySet()) {
                    atts.put(e.getKey(), e.getValue());
                }
                
                NamingEnumeration<SearchResult> results = dirContext.search(userSearchBase, atts);
                while (results.hasMore()) {
                    SearchResult result = results.next();
                    
                    users.add((User) userMapper.mapAttributes(null, result.getAttributes()));
                }

                dirContext.close();
                return users;
            }
            
        });
    }
    
    public List<User> getUsersForGroup(String groupId) {
        return (List<User>) getLdapTemplate().execute(new LdapCallback() {

            public Object doInDirContext(DirContext dirContext) throws NamingException {
                List<User> users = new ArrayList<User>();
                
                BasicAttributes atts = new BasicAttributes();
                for (Map.Entry<String, String> e : userSearchAttributes.entrySet()) {
                    atts.put(e.getKey(), e.getValue());
                }
                
                NamingEnumeration<SearchResult> results = dirContext.search(userSearchBase, atts);
                while (results.hasMore()) {
                    SearchResult result = results.next();
                    
                    users.add((User) userMapper.mapAttributes(null, result.getAttributes()));
                }

                dirContext.close();
                return users;
            }
            
        });
    }

    public void save(User user) throws DuplicateItemException, NotFoundException {
        if (SecurityUtils.SYSTEM_USER.getId().equals(user.getId())) {
            return;
        }
        
        
        LdapUserMetadata metadata = null;
        try {
            metadata = ldapUserMetadataDao.get(user.getId());
        } catch (NotFoundException e) {
            metadata = new LdapUserMetadata();
            metadata.setId(user.getId());
        }
        metadata.setProperties(user.getProperties());
        ldapUserMetadataDao.save(metadata);
        
    }

    public List<User> find(Map<String, Object> criteria) {
        throw new UnsupportedOperationException();
    }

    public Class<User> getTypeClass() {
        return User.class;
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
    
    public void setInitialDirContextFactory(InitialDirContextFactory initialDirContextFactory) {
        this.initialDirContextFactory = initialDirContextFactory;
    }

    public void setUserSearchBase(String userSearchBase) {
        this.userSearchBase = userSearchBase;
    }

    public Map<String, String> getUserSearchAttributes() {
        return userSearchAttributes;
    }

    public boolean isManagementSupported() {
        return false;
    }

    public void setUserSearchAttributes(Map<String, String> userSearchAttributes) {
        this.userSearchAttributes = userSearchAttributes;
    }

    public void setUserMapper(LdapEntryMapper userMapper) {
        this.userMapper = userMapper;
    }

    public void setLdapUserMetadataDao(Dao<LdapUserMetadata> ldapUserMetadataDao) {
        this.ldapUserMetadataDao = ldapUserMetadataDao;
    }

}
