package com.learning.dynamic.repo;

import com.learning.dynamic.config.DBConfig;
import com.learning.dynamic.entity.User;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository("userRepo")
@RequiredArgsConstructor
public class UserRepo extends CommonDao {

    private final DBConfig dbConfig;

    private static final Logger LOGGER = LogManager.getLogger(UserRepo.class);

    public List<User> getUsers() throws SQLException {
        List<User> userList = new ArrayList<>();
        LOGGER.info("getUsers() invoked");

        Connection connection = dbConfig.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            String sqlQuery = "select * from `user`";

            LOGGER.info("SQL query: " + sqlQuery);

            preparedStatement = connection.prepareStatement(sqlQuery);
            //preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                User user = new User();
                user.setFirstName(resultSet.getString("first_name"));
                user.setLastName(resultSet.getString("last_name"));
                user.setEmail(resultSet.getString("email"));
                user.setCompany(resultSet.getString("company"));
                userList.add(user);
            }
        }
        catch (SQLException e) {
            LOGGER.error("SQLException occured in getUsers()");
            LOGGER.error(e.fillInStackTrace());

            e.printStackTrace();
            throw e;
        }
        finally {
            releasingJDBCResources(null, preparedStatement, resultSet);
        }
        return userList;
    }
}
