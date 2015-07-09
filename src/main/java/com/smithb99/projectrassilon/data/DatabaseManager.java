package com.smithb99.projectrassilon.data;

import com.smithb99.projectrassilon.ProjectRassilon;

import java.io.File;
import java.sql.*;

public class DatabaseManager {
    private ProjectRassilon plugin;

    private String dbPath;

    private Connection connection;
    private Statement statement;
    private ResultSet results;

    DatabaseManager(ProjectRassilon pl) {
        plugin = pl;
    }

    protected void initDB() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            MessageSender.logStackTrace(e);
        }

        try {
            dbPath = "jdbc:sqlite:" + plugin.getDataFolder() + File.separator + "ProjectRassilon.db";
            connection = DriverManager.getConnection(dbPath);
        } catch (SQLException e) {
            MessageSender.logStackTrace(e);
        }
    }

    protected void createTables() {
        update("CREATE TABLE IF NOT EXISTS Regen (UUID TEXT UNIQUE NOT NULL PRIMARY KEY, count INTEGER DEFAULT " + plugin.getConfig().getInt("settings.regen.count") + ", block INTEGER DEFAULT 0, incarnation INTEGER DEFAULT 1");

        StringBuilder taskQuery = new StringBuilder("CREATE TABLE IF NOT EXISTS Tasks (UUID TEXT UNIQUE NOT NULL PRIMARY KEY");

        for (RegenTask t : RegenTask.values()) {
            taskQuery.append(");");
            update(taskQuery.toString());
        }
    }

    protected int query(String query, String row) {
        try {
            statement = connection.createStatement();

            results = statement.executeQuery(query);

            int var = -1;

            while (results.next()) {
                var = results.getInt(row);
            }

            statement.close();
            results.close();

            return var;
        } catch (SQLException e) {
            MessageSender.logStackTrace(e);
            return -1;
        }
    }

    protected void update(String query) {
        try {
            statement = connection.createStatement();

            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException e) {
            MessageSender.logStackTrace(e);
        }
    }

    protected boolean checkNull(String player, String table) {
        try {
            statement = connection.createStatement();

            results = statement.executeQuery("SELECT * FROM " + table + " WHERE UUID='" + player + "';");

            String UUID = null;

            while (results.next()) {
                UUID = results.getString("UUID");
            }

            statement.close();
            results.close();

            return UUID == null;
        } catch (SQLException e) {
            MessageSender.log("Exception:  " + e.getLocalizedMessage());
            return true;
        }
    }
}
