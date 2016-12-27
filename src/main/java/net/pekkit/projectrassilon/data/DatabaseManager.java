/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Doctor Squawk <Squawkers13@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.pekkit.projectrassilon.data;

import net.pekkit.projectrassilon.ProjectRassilon;
import net.pekkit.projectrassilon.locale.MessageSender;
import net.pekkit.projectrassilon.util.RegenTask;

import java.io.File;
import java.sql.*;

import static net.pekkit.projectrassilon.util.RassilonUtils.ConfigurationFile.REGEN;

/**
 * Database Manager- Manages the database used to store Timelord DNA.
 *
 * @author Squawkers13
 */
public class DatabaseManager {

    private ProjectRassilon plugin;

    private String dbPath;

    private Connection connection;
    private Statement statement;
    private ResultSet results;

    DatabaseManager(ProjectRassilon pl) {
        plugin = pl;
    }

    /**
     * Initializes the database.
     *
     */
    protected void initDB() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            MessageSender.logStackTrace(e);
        }

        try {
            dbPath = "jdbc:sqlite:" + plugin.getDataFolder() + File.separator + "ProjectRassilon-PREVIEW-2.db";
            connection = DriverManager.getConnection(dbPath);
        } catch (SQLException ex) {
            MessageSender.logStackTrace(ex);
        }

    }

    /**
     * Creates the default tables.
     */
    protected void createTables() {
        update("CREATE TABLE IF NOT EXISTS Regen (UUID TEXT UNIQUE NOT NULL PRIMARY KEY, energy INTEGER DEFAULT " + plugin.getConfig(REGEN).getInt("regen.costs.startingEnergy") + ", block INTEGER DEFAULT 0, incarnation INTEGER DEFAULT 1);");

        StringBuilder taskQuery = new StringBuilder("CREATE TABLE IF NOT EXISTS Tasks (UUID TEXT UNIQUE NOT NULL PRIMARY KEY");
        for (RegenTask t : RegenTask.values()) {
            taskQuery.append(", ").append(t.getColumnName()).append(" INTEGER DEFAULT 0");
        }
        taskQuery.append(");");
        update(taskQuery.toString());

    }

    /**
     * Sends a query to the database and returns the results.
     *
     * @param query
     * @param row
     * @return results of query
     */
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
        } catch (SQLException ex) {
            MessageSender.logStackTrace(ex);
            return -1;
        }
    }

    /**
     * Sends a query to the database. Must be a INSERT, UPDATE, or DELETE query!
     *
     * @param query
     */
    protected void update(String query) {
        try {
            statement = connection.createStatement();

            statement.executeUpdate(query);

            statement.close();
        } catch (SQLException ex) {
            MessageSender.logStackTrace(ex);
        }
    }

    /**
     *
     * @param player
     * @param table
     * @return whether the query was null or not
     */
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
        } catch (SQLException ex) {
            MessageSender.log("Exception: " + ex.getLocalizedMessage());
            //ms.logStackTrace(ex);
            return true;
        }
    }

}
