package org.mule.galaxy.impl.jcr;

import static org.mule.galaxy.impl.jcr.JcrUtil.getStringOrNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;
import org.mule.galaxy.security.User;
import org.mule.galaxy.security.UserExistsException;
import org.mule.galaxy.security.UserManager;
import org.springframework.dao.DataAccessException;
import org.springmodules.jcr.JcrCallback;

public class UserManagerImpl extends AbstractReflectionDao<User> 
    implements UserManager, UserDetailsService {
    
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String USER = "user";
    private static final String NAME = "name";
    private static final String CREATED = "created";
    protected static final String ENABLED = "enabled";

    public UserManagerImpl() throws Exception {
        super(User.class, "users", true);
    }

    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException, DataAccessException {
        return (UserDetails) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node userNode = findUser(username, session);
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

    public boolean setPassword(String username, String oldPassword, String newPassword) {
        // TODO Auto-generated method stub
        return false;
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
        Query q = qm.createQuery("/*/users/*[@enabled='true']/username[@" +
        		JcrUtil.VALUE + "='" + username + "']", Query.XPATH);
        QueryResult qr = q.execute();
        
        NodeIterator nodes = qr.getNodes();
        if (!nodes.hasNext()) {
            return null;
        }
        
        return nodes.nextNode().getParent();
    }

    public User create(final String username, final String password, final String name) throws UserExistsException {
        return (User) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                Node users = getObjectsNode();
                
                Node node = users.addNode(USER);
                node.addMixin("mix:referenceable");
                node.setProperty(PASSWORD, password);
                node.setProperty(ENABLED, true);
                
                User user = new User();
                user.setUsername(username);
                user.setName(name);
                user.setId(node.getUUID());
                
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
                return user;
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
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            JcrUtil.setProperty(CREATED, cal, node);
        }
    }
}
