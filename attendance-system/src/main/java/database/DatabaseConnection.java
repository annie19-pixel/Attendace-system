package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {


    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE = "attendance_db";
    private static final String USER = "root";          // change to your MySQL username
    private static final String PASSWORD = "0718659347annie#";          // change to your MySQL password

    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE +
                    "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" +
                    "&useUnicode=true&characterEncoding=utf8mb4";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}