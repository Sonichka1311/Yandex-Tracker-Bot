package ru.hse.cs.java2020.task03;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3301/tracker";
    private static final String USER = "root";
    private static final String PASSWORD = "guest";

    private static final String USERS_TABLE = "users";
    private static final String TASKS_TABLE = "tasks";

    // for lint only
    private static final int FIRST_ARG = 1;
    private static final int SECOND_ARG = 2;
    private static final int THIRD_ARG = 3;

    private static Connection connection;

    Database() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException ex) {
            System.err.printf("Connection to database is not mounted: %s\n", ex.toString());
        }
    }

    private void setString(String what, Long userId, String key, String value) {
        String update = "UPDATE " + what + " SET " + key + " = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(update)) {
            statement.setString(FIRST_ARG, value);
            statement.setLong(SECOND_ARG, userId);
            statement.execute();
        } catch (SQLException ex) {
            System.err.printf("Database exception while set %s: %s\n", key, ex.toString());
        }
    }

    private void setInteger(String what, Long userId, String key, Integer value) {
        String update = "UPDATE " + what + " SET " + key + " = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(update)) {
            statement.setInt(FIRST_ARG, value);
            statement.setLong(SECOND_ARG, userId);
            statement.execute();
        } catch (SQLException ex) {
            System.err.printf("Database exception while set %s: %s\n", key, ex.toString());
        }
    }

    private String getString(String where, Long userId, String key) {
        String select = "SELECT " + key + " FROM " + where + " WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(select)) {
            statement.setLong(FIRST_ARG, userId);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                return res.getString(key);
            }
        } catch (SQLException ex) {
            System.err.printf("Database exception while select %s: %s\n", key, ex.toString());
        }
        return null;
    }

    private Integer getInteger(String where, Long userId, String key) {
        String select = "SELECT " + key + " FROM " + where + " WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(select)) {
            statement.setLong(FIRST_ARG, userId);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                return res.getInt(key);
            }
        } catch (SQLException ex) {
            System.err.printf("Database exception while select %s: %s\n", key, ex.toString());
        }
        return null;
    }

    public String getUserState(Long userId) {
        String state = getString(USERS_TABLE, userId, "state");
        return (state != null) ? state : "main";
    }

    public void updateUserState(Long userId, String state) {
        long id = 0;
        String select = "SELECT id FROM " + USERS_TABLE + " WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(select)) {
            statement.setLong(FIRST_ARG, userId);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                id = res.getLong("id");
            }
        } catch (SQLException ex) {
            System.err.printf("Database exception while select id: %s\n", ex.toString());
            return;
        }
        if (id == 0) {
            String insert = "INSERT INTO " + USERS_TABLE + " (id, state) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insert)) {
                statement.setLong(FIRST_ARG, userId);
                statement.setString(SECOND_ARG, state);
                statement.execute();
            } catch (SQLException ex) {
                System.err.printf("Database exception while set state: %s\n", ex.toString());
            }

            insert = "INSERT INTO " + TASKS_TABLE + " (id) VALUES (?)";
            try (PreparedStatement statement = connection.prepareStatement(insert)) {
                statement.setLong(FIRST_ARG, userId);
                statement.execute();
            } catch (SQLException ex) {
                System.err.printf("Database exception while set id into tasks: %s\n", ex.toString());
            }
        } else {
            setString(USERS_TABLE, userId, "state", state);
        }
    }

    public boolean isAuthorized(Long userId) {
        String select = "SELECT auth FROM " + USERS_TABLE + " WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(select)) {
            statement.setLong(FIRST_ARG, userId);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                return res.getBoolean("auth");
            }
        } catch (SQLException ex) {
            System.err.printf("Database exception while select auth: %s\n", ex.toString());
        }
        return false;
    }

    public void setSummary(Long userId, String summary) {
        setString(TASKS_TABLE, userId, "summary", summary);
    }

    public void setQueue(Long userId, String queue) {
        setString(TASKS_TABLE, userId, "queue", queue);
    }

    public void setDescription(Long userId, String description) {
        setString(TASKS_TABLE, userId, "description", description);
    }

    public String getSummary(Long userId) {
        return getString(TASKS_TABLE, userId, "summary");
    }

    public String getQueue(Long userId) {
        return getString(TASKS_TABLE, userId, "queue");
    }

    public String getDescription(Long userId) {
        return getString(TASKS_TABLE, userId, "description");
    }

    public void setToken(Long userId, String token) {
        String update = "UPDATE " + USERS_TABLE + " SET token = ?, auth = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(update)) {
            statement.setString(FIRST_ARG, token);
            statement.setBoolean(SECOND_ARG, false);
            statement.setLong(THIRD_ARG, userId);
            statement.execute();
        } catch (SQLException ex) {
            System.err.printf("Database exception while update token: %s\n", ex.toString());
        }
    }

    public void setCompanyId(Long userId, String companyId) {
        setString(USERS_TABLE, userId, "company", companyId);
    }

    public String getToken(Long userId) {
        String select = "SELECT token FROM " + USERS_TABLE + " WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(select)) {
            statement.setLong(FIRST_ARG, userId);
            ResultSet res = statement.executeQuery();
            res.next();
            return res.getString("token");
        } catch (SQLException ex) {
            System.err.printf("Database exception while get token: %s\n", ex.toString());
        }
        return null;
    }

    public String getCompanyId(Long userId) {
        String select = "SELECT company FROM " + USERS_TABLE + " WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(select)) {
            statement.setLong(FIRST_ARG, userId);
            ResultSet res = statement.executeQuery();
            res.next();
            return res.getString("company");
        } catch (SQLException ex) {
            System.err.printf("Database exception while get company id: %s\n", ex.toString());
        }
        return null;
    }

    public Integer getPage(Long userId) {
        return getInteger(TASKS_TABLE, userId, "page");
    }

    public void updatePage(Long userId, int page) {
        setInteger(TASKS_TABLE, userId, "page", page);
    }

    public void updateCount(Long userId, Integer count) {
        setInteger(TASKS_TABLE, userId, "count", count);
    }

    public Integer getCount(Long userId) {
        return getInteger(TASKS_TABLE, userId, "count");
    }

    public void authorizeUser(Long userId) {
        String update = "UPDATE " + USERS_TABLE + " SET auth = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(update)) {
            statement.setBoolean(FIRST_ARG, true);
            statement.setLong(SECOND_ARG, userId);
            statement.execute();
        } catch (SQLException ex) {
            System.err.printf("Database exception while update auth: %s\n", ex.toString());
        }
    }

    public void setTask(Long userId, String task) {
        setString(TASKS_TABLE, userId, "task", task);
    }

    public String getTask(Long userId) {
        return getString(TASKS_TABLE, userId, "task");
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.printf("Can't close connection to DB: %s\n", e.toString());
        }
    }
}
