package org.mule.galaxy.config.jackrabbit;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.util.IOUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.io.InputStream;

public class DatabaseInitializer implements InitializingBean {

    protected final Log logger = LogFactory.getLog(getClass());

    private DataSource dataSource;

    private Resource ddlScript;

    public void afterPropertiesSet() throws Exception {
        if (getDataSource() == null) {
            throw new IllegalStateException("No DataSource configured, please set the 'dataSource' property of the bean.");
        }

        if (ddlScript == null) {
            throw new IllegalStateException("No ddlScript configured, please set the 'ddlScript' property of the bean.");
        }

        final InputStream inputStream = ddlScript.getInputStream();
        if (inputStream == null) {
            throw new IllegalArgumentException("Resource unreachable: " + ddlScript.getURI().toURL());
        }

        Connection conn = dataSource.getConnection();
        try {
            conn.setAutoCommit(false);

            Statement stmt = conn.createStatement();

            // check if any tables need to be created
            try {
                stmt.executeQuery("select * from qrtz_locks");
                // to play nicely with derby
                conn.commit();

                if (logger.isDebugEnabled()) {
                    logger.debug("Database tables for quartz already exist.");
                }

                // no failure, no need to create tables
                return;
            } catch (SQLException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("SQL select statement failed, quartz tables might not exist in the database " +
                            "(this is not an error)", e);

                    // chained SQL exceptions
                    SQLException next;
                    while ((next = e.getNextException()) != null) {
                        logger.debug(next);
                    }
                }

                // for now assume the table doesn't exist and run ddl. not ideal, but...
            }

            if (logger.isInfoEnabled()) {
                logger.info("Initializing database tables (first run) from " + ddlScript);
            }

            String ddlScript = IOUtils.toString(inputStream);

            // jdbc can't execute scripts through non-proprietary apis, split by statement
            String[] statements = ddlScript.split(";");

            for (String ddl : statements) {
                ddl = ddl.trim();
                // skip empty ddls
                if (ddl.length() == 0) {
                    continue;
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("Executing DDL: " + ddl);
                }
                stmt.execute(ddl);
            }
            conn.commit();
        } catch (SQLException ex) {
            conn.rollback();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Resource getDdlScript() {
        return ddlScript;
    }

    public void setDdlScript(Resource ddlScript) {
        this.ddlScript = ddlScript;
    }
}
