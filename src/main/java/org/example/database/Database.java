package org.example.database;

public class Database {
    private  final String DB_URL = "jdbc:postgresql://host:port/db_name";
    private  final String DB_USER = "owner";
    private  final String DB_PASSWORD = "password";

    public String getDB_URL() {
        return DB_URL;
    }

    public  String getDB_USER() {
        return DB_USER;
    }

    public String getDB_PASSWORD() {
        return DB_PASSWORD;
    }
}
