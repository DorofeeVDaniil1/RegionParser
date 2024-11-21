package org.example.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/disp";
    private static String USER;
    private static String PASSWORD;

    public static String getUSER() {
        return USER;
    }

    public static void setUSER(String USER) {
        DatabaseConnection.USER = USER;
    }

    public static String getPASSWORD() {
        return PASSWORD;
    }

    public static void setPASSWORD(String PASSWORD) {
        DatabaseConnection.PASSWORD = PASSWORD;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
