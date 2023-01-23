package com.learning.dynamic.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
@RequiredArgsConstructor
public class DBConfig {

    private final static Logger LOGGER = LogManager.getLogger(DBConfig.class);

    private final DBProperty dbProperty;

    private volatile boolean isPrimary = true;

    private HikariDataSource connectionPool = null;

    private final long leakDetectionThreshold = 30000;

    private final long poolMaxLifeTime = 60000;

    @PostConstruct
    public void initializeDBProperties() {
        LOGGER.info("initializeDBProperties()");
        loadConnectionPool(true);
    }

    private void loadConnectionPool(boolean isRetry) {
        LOGGER.debug("loadConnectionPool()");

        try {
            HikariConfig config = new HikariConfig ();

            if(isPrimary) {
                LOGGER.info("Loading primary Database");
                Class.forName(dbProperty.getProperty("app.datasource.primary.driver-class-name"));
                config.setJdbcUrl(dbProperty.getProperty("app.datasource.primary.jdbc-url"));
                config.setUsername(dbProperty.getProperty("app.datasource.primary.username"));
                config.setPassword(dbProperty.getProperty("app.datasource.primary.password"));
            }
            else {
                LOGGER.info("Loading secondary Database");
                Class.forName(dbProperty.getProperty("app.datasource.secondary.driver-class-name"));
                config.setJdbcUrl(dbProperty.getProperty("app.datasource.secondary.jdbc-url"));
                config.setUsername(dbProperty.getProperty("app.datasource.secondary.username"));
                config.setPassword(dbProperty.getProperty("app.datasource.secondary.password"));
            }

            config.setLeakDetectionThreshold(leakDetectionThreshold);
            config.setMaxLifetime(poolMaxLifeTime);

            if (!(dbProperty.getProperty("Connection_PoolSize")).equalsIgnoreCase("")) {
                config.setMaximumPoolSize(Integer.parseInt(dbProperty.getProperty("Connection_PoolSize")));
            }
            if (!(dbProperty.getProperty("Connection_MinIdle")).equalsIgnoreCase("")) {
                config.setMinimumIdle(Integer.parseInt(dbProperty.getProperty("Connection_MinIdle")));
            }
            if (!(dbProperty.getProperty("Connection_TestQuery")).equalsIgnoreCase("")) {
                config.setConnectionTestQuery(dbProperty.getProperty("Connection_TestQuery"));
            }
            if (!(dbProperty.getProperty("Connection_MaxWait")).equalsIgnoreCase("")) {
                config.setConnectionTimeout(Integer.parseInt(dbProperty.getProperty("Connection_MaxWait")));
            }
            if (!(dbProperty.getProperty("ValidationInterval")).equalsIgnoreCase("")) {
                config.setValidationTimeout(Integer.parseInt(dbProperty.getProperty("ValidationInterval")));
            }
            if (!(dbProperty.getProperty("DBAutoCommit")).equalsIgnoreCase("")) {
                config.setAutoCommit(false);
            }

            connectionPool = new HikariDataSource(config);
            LOGGER.info((isPrimary? "Primary" : "Secondary") + " Database: Loaded Success");
        } catch (Exception e) {
            LOGGER.error(e.getMessage() + " occured at loadConnectionPool()");
            LOGGER.error("Failed to load " + (isPrimary ? "Primary" : "Secondary") + " DB, LOADING alternate DatabaseURL : "
                    + (isPrimary ? dbProperty.getProperty("app.datasource.secondary.jdbc-url")
                    : dbProperty.getProperty("app.datasource.primary.jdbc-url")));
            try {
                if(connectionPool != null)
                    connectionPool.close();
                connectionPool = null;
            }catch(Exception m) {
            }
            isPrimary = !(isPrimary);
            if(isRetry)
                loadConnectionPool(!isRetry);
        }
    }

    public Connection getConnection() throws SQLException {
        try {
            return getRetryConnection(true);
        } catch (SQLException e) {
            throw e;
        }
    }

    private Connection getRetryConnection(boolean isRetry) throws SQLException {
        try {
            return connectionPool.getConnection();
        } catch(Exception e) {
            LOGGER.error(" **** DATABASE ERROR ****** "+e);
            try {
                connectionPool.close();
            }catch(Exception m) {
            }

            if(!isRetry) {
                throw new SQLException();
            }
            isPrimary = (!isPrimary);
            LOGGER.info(" ***** Changing DATABASE Connectivity to "+(isPrimary? "Primary":"Secondary")+" *****");
            loadConnectionPool(true);
            return getRetryConnection(false);
        }
    }
}
