package org.mule.galaxy.config.jackrabbit;

import org.apache.jackrabbit.core.fs.db.JNDIDatabaseFileSystem;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * An extension of the Jackrabbit's {@link JNDIDatabaseFileSystem} to allow for
 * more fine-grained control over the JNDI environment.
 */
public class JndiDatabaseFileSystem extends JNDIDatabaseFileSystem {

    private Hashtable<String, String> jndiEnvironment = new Hashtable<String, String>();

    public String getInitialContextFactory() {
        return jndiEnvironment.get(Context.INITIAL_CONTEXT_FACTORY);
    }

    public void setInitialContextFactory(String initialFactory) {
        jndiEnvironment.put(Context.INITIAL_CONTEXT_FACTORY, initialFactory);
    }

    public Hashtable getJndiEnvironment() {
        return jndiEnvironment;
    }

    public void setJndiEnvironment(Hashtable<String, String> jndiEnvironment) {
        this.jndiEnvironment = jndiEnvironment;
    }

    protected Connection getConnection() throws NamingException, SQLException {
        InitialContext ic = new InitialContext(getJndiEnvironment());
        DataSource dataSource = (DataSource) ic.lookup(getDataSourceLocation());
        return dataSource.getConnection();
    }

}