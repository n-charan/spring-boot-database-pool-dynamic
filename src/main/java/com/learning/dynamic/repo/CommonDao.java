package com.learning.dynamic.repo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CommonDao {

    private static final Logger LOGGER = LogManager.getLogger(CommonDao.class);

    protected void commit(Connection connection) {
        try {
            if(connection != null)
                connection.commit();
        }
        catch (SQLException e) {
            LOGGER.error("SQLException occured in commit()");
            LOGGER.error(e.fillInStackTrace());

            e.printStackTrace();
        }
    }

    protected void rollBack(Connection connection) {
        try {
            if(connection != null)
                connection.rollback();
        }
        catch (SQLException e) {
            LOGGER.error("SQLException occured in rollBack()");
            LOGGER.error(e.fillInStackTrace());

            e.printStackTrace();
        }
    }

    protected void releasingJDBCResources(Connection connection,
                                          PreparedStatement preparedStatement, ResultSet resultSet) {
        try {
            if (resultSet != null){
                resultSet.close();
                resultSet =  null;
            }
        }
        catch(Exception e) {
            LOGGER.error("Exception occurred releasingJDBCResources() at closing ResultSet");
            LOGGER.error(e.fillInStackTrace());

            e.printStackTrace();
        }
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
                preparedStatement =  null;
            }
        }
        catch(Exception e) {
            LOGGER.error("Exception occurred releasingJDBCResources() at closing PreparedStatement");
            LOGGER.error(e.fillInStackTrace());

            e.printStackTrace();
        }
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }
        catch(Exception e) {
            LOGGER.error("Exception occurred releasingJDBCResources() at closing Connection");
            LOGGER.error(e.fillInStackTrace());

            e.printStackTrace();
        }
    }

    protected String getTotalRowCount(Connection connection, String... sqlQueryArguments) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String totalRows = "0";

        try {
            preparedStatement = connection.prepareStatement(sqlQueryArguments[0]);

            for(int i = 1; i < sqlQueryArguments.length; i ++) {
                preparedStatement.setString(i, sqlQueryArguments[i]);
            }

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                totalRows = resultSet.getString(1);

                LOGGER.debug("Total row(s): " + totalRows);
            }
        }
        catch (SQLException e) {
            LOGGER.error("SQLException occured in getTotalRowCount()");
            LOGGER.error(e.fillInStackTrace());

            e.printStackTrace();
        }
        finally {
            releasingJDBCResources(null, preparedStatement, resultSet);
        }

        return totalRows;
    }
}
