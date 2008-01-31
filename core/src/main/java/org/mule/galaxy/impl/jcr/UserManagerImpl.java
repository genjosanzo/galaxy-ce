package org.mule.galaxy.impl.jcr;

import org.mule.galaxy.api.security.User;
import org.mule.galaxy.api.security.UserExistsException;
import org.mule.galaxy.api.security.UserManager;
import static org.mule.galaxy.impl.jcr.JcrUtil.getStringOrNull;
import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springmodules.jcr.JcrCallback;

public class UserManagerImpl extends AbstractReflectionDao<User>
    implements UserManager, UserDetailsService {
    
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String USER = "user";
    private static final String NAME = "name";
    private static final String CREATED = "created";
    private static final String EMAIL = "email";
    private static final String ENABLED = "enabled";
    private static final String ROLES = "roles";

    public UserManagerImpl() throws Exception {
        super(User.class, "users", true);
    }

    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException, DataAccessException {
        return (UserDetails) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node userNode = findUser(username, session);
                if (userNode == null) {
                    throw new UsernameNotFoundException("Username was not found: " + username);
                }
                try {
                    return new UserDetailsWrapper(build(userNode, session), 
                                                  getStringOrNull(userNode, PASSWORD));
                } catch (Exception e) {
                    if (e instanceof RepositoryException) {
                        throw (RepositoryException) e;
                    }
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public boolean setPassword(final String username, final String oldPassword, final String newPassword) {
        return (Boolean) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node node = findUser(username, session);
                if (node == null) {
                    return null;
                }
                
                String pass = JcrUtil.getStringOrNull(node, PASSWORD);
                
                if (oldPassword != null && oldPassword.equals(pass)) {
                    node.setProperty(PASSWORD, newPassword);
                } else {
                    return false;
                }
                
                return true;
            }
        });
    }

    public void setPassword(final User user, final String password) {
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node node = findUser(user.getUsername(), session);
                if (node == null) {
                    return null;
                }
                
                node.setProperty(PASSWORD, password);
                return null;
            }
        });
    }

    public User authenticate(final String username, final String password) {
        return (User) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node node = findUser(username, session);
                if (node == null) {
                    return null;
                }
               
                String pass = getStringOrNull(node, PASSWORD);
                if (password != null && password.equals(pass)) {
                    try {
                        return build(node, session);
                    } catch (Exception e) {
                        if (e instanceof RepositoryException) {
                            throw (RepositoryException) e;
                        }
                        throw new RuntimeException(e);
                    }
                }
                
                return null;
                
            }
        });
    }

    protected Node findUser(String username, Session session) throws RepositoryException {
        QueryManager qm = getQueryManager(session);
        Query q = qm.createQuery("/*/users/user[@enabled='true' and @username='" + username + "']", Query.XPATH);
        QueryResult qr = q.execute();
        
        NodeIterator nodes = qr.getNodes();
        if (!nodes.hasNext()) {
            return null;
        }
        
        return nodes.nextNode();
    }

    public void create(final User user, final String password) throws UserExistsException
    {
        execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node users = getObjectsNode(session);
                
                Node node = users.addNode(USER);
                node.addMixin("mix:referenceable");
                node.setProperty(PASSWORD, password);
                node.setProperty(ENABLED, true);
                
                user.setId(node.getUUID());
                user.getRoles().add(UserManager.ROLE_USER);
                
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                user.setCreated(cal);
                
                try {
                    persist(user, node, session);
                } catch (Exception e) {
                    if (e instanceof RepositoryException) {
                        throw (RepositoryException) e;
                    }
                    throw new RuntimeException(e);
                }
                session.save();
                return null;
            }
        });
    }

    @Override
    protected void doDelete(String id, Session session) throws RepositoryException {
        Node userNode = findNode(id, session);
        
        if (userNode != null) {
            userNode.setProperty(ENABLED, false);
        }
        session.save();
    }

    
    @SuppressWarnings("unchecked")
    @Override
    protected List<User> doListAll(Session session) throws RepositoryException {
        return (List<User>) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                QueryManager qm = getQueryManager(session);
                Query q = qm.createQuery("/*/users/user[@enabled='true']", Query.XPATH);
                QueryResult qr = q.execute();
                
                ArrayList<User> users = new ArrayList<User>();
                for (NodeIterator nodes = qr.getNodes(); nodes.hasNext();) {
                    Node node = nodes.nextNode();
                    
                    try {
                        users.add(build(node, session));
                    } catch (Exception e) {
                        if (e instanceof RepositoryException) {
                            throw (RepositoryException) e;
                        } else if (e instanceof RuntimeException) {
                            throw (RuntimeException) e;
                        }
                        throw new RuntimeException(e);
                    }
                }

                return users;
            }
        });
    }

    protected void doCreateInitialNodes(Session session, Node objects) throws RepositoryException {
        if (objects.getNodes().getSize() == 0) {
            Node node = objects.addNode(USER);
            node.addMixin("mix:referenceable");
            node.setProperty(PASSWORD, "admin");
            node.setProperty(ENABLED, true);
            
            JcrUtil.setProperty(USERNAME, "admin", node);
            JcrUtil.setProperty(NAME, "Administrator", node);
            JcrUtil.setProperty(EMAIL, "", node);
            HashSet<String> roles = new HashSet<String>();
            roles.add(UserManager.ROLE_ADMINISTRATOR);
            roles.add(UserManager.ROLE_USER);
            JcrUtil.setProperty(ROLES, roles, node);
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            JcrUtil.setProperty(CREATED, cal, node);
        }
    }
}
