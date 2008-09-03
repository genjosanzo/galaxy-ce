package org.mule.galaxy.config.jackrabbit;

import org.apache.jackrabbit.core.persistence.db.JNDIDatabasePersistenceManager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.Hashtable;

/**
 * A more customizable JNDI database persistence manager.
 */
public class JndiDatabasePersistenceManager extends JNDIDatabasePersistenceManager {

    private Hashtable<String, String> jndiEnvironment = new Hashtable<String, String>();

    private boolean derbyShutdown;

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

    public boolean isDerbyShutdown() {
        return derbyShutdown;
    }

    public void setDerbyShutdown(boolean derbyShutdown) {
        this.derbyShutdown = derbyShutdown;
    }

    protected Connection getConnection() throws NamingException, SQLException {
        InitialContext ic = new InitialContext(getJndiEnvironment());
        DataSource dataSource = (DataSource) ic.lookup(getDataSourceLocation());
        return dataSource.getConnection();
    }

    /**
     * Closes the given connection and shuts down the embedded Derby
     * database if <code>shutdownOnClose</code> is set to true.
     *
     * @param connection database connection
     * @throws SQLException if an error occurs
     * @see org.apache.jackrabbit.core.persistence.db.DatabasePersistenceManager#closeConnection(Connection)
     */
    protected void closeConnection(Connection connection) throws SQLException {

        // normal shutdown
        if (!derbyShutdown) {
            connection.close();
            return;
        }

        // prepare connection url for issuing derby shutdown command
        String url = connection.getMetaData().getURL();
        int pos = url.lastIndexOf(';');
        if (pos != -1) {
            // strip any attributes from connection url
            url = url.substring(0, pos);
        }
        url += ";shutdown=true";

        // we have to reset the connection to 'autoCommit=true' before closing it;
        // otherwise Derby would mysteriously complain about some pending uncommitted
        // changes which can't possibly be true.
        // @todo further investigate
        connection.setAutoCommit(true);
        connection.close();
    }
    
}