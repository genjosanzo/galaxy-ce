package org.mule.galaxy.atom.client;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Session;

import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Item;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.type.TypeManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springmodules.jcr.SessionFactoryUtils;

public abstract class AbstractAtomTest extends org.mule.galaxy.test.AbstractAtomTest {

    Session session = null;
    boolean participate = false;

    protected Item attatchTestWorkspace() throws DuplicateItemException, RegistryException,
        AccessException, PropertyException, NotFoundException, PolicyException {
        Map<String,String> config = new HashMap<String, String>();
        config.put("url", "http://localhost:9002/api/registry/Test");
        config.put("username", "admin");
        config.put("password", "admin");
        
        login("admin", "admin");
        
        registry.newItem("Test", typeManager.getTypeByName(TypeManager.WORKSPACE));
        
        Item parent = registry.newItem("parent", typeManager.getTypeByName(TypeManager.WORKSPACE)).getItem();
        return registry.attachItem(parent, "atom", AtomWorkspaceManagerFactory.ID, config);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        newSession();
    }

    private void newSession() {
        if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
            // Do not modify the Session: just set the participate
            // flag.
            participate = true;
            session = SessionFactoryUtils.getSession(sessionFactory, false);
        } else {
            session = SessionFactoryUtils.getSession(sessionFactory, true);
            TransactionSynchronizationManager.bindResource(sessionFactory, 
                                                           sessionFactory.getSessionHolder(session));
        }
    }

    @Override
    protected void tearDown() throws Exception {
        releaseSession();
        super.tearDown();
    }

    private void releaseSession() {
        if (!participate) {
            TransactionSynchronizationManager.unbindResource(sessionFactory);
            SessionFactoryUtils.releaseSession(session, sessionFactory);
        }
    }
}
