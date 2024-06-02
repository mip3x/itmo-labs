package io.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBManager {
    private static DBManager instance = null;
    private final Logger logger = LogManager.getLogger();
    private Connection connection;

    public static DBManager getInstance() {
        if (instance == null) instance = new DBManager();
        return instance;
    }

    public boolean establishConnection(String url, String user, String password) {
        try {
            connection = DriverManager.getConnection(url, user, password);

            if (connection == null) {
                logger.error("Connection with database failed!");
                return false;
            }
            logger.info("Connection with database established");
            return true;

        } catch (Exception exception) {
            logger.error("Connection with database failed: " + exception);
            return false;
        }
    }

    public void loadCollection() {
    }
}
