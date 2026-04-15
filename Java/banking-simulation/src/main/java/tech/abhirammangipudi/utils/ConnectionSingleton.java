package tech.abhirammangipudi.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import tech.abhirammangipudi.errors.ConnectionError;

public class ConnectionSingleton {
    private static Connection connection;
    private static final String HOST = System.getenv("MYSQL_HOST");
    private static final int PORT = Integer.parseInt(System.getenv("MYSQL_PORT"));
    private static final String DATABASE = System.getenv("MYSQL_DATABASE");
    private static final String USER = System.getenv("MYSQL_USER");
    private static final String PASSWORD = System.getenv("MYSQL_PASSWORD");

    private ConnectionSingleton() {
    }

    public static synchronized Connection getConnection() throws ConnectionError {
        try {
            if (HOST == null || DATABASE == null || USER == null || PASSWORD == null) {
                throw new ConnectionError("MySQL information not set in the environment", HOST);
            }

            String url = String.format(
                    "jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC",
                    HOST, PORT, DATABASE);
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, USER, PASSWORD);
            }
        } catch (SQLException err) {
            err.printStackTrace();
            throw new ConnectionError(err.getMessage(), HOST);
        }

        return connection;
    }
}
