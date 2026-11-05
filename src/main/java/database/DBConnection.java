package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {


    private static final String URL =
        "jdbc:mysql://localhost:3306/fitness_tracker?createDatabaseIfNotExist=true";

    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static boolean tablesReady = false;

    public static Connection getConnection() throws SQLException {
        Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
        if (!tablesReady) {
            createTables(con);
            tablesReady = true;
        }
        return con;
    }


    private static void createTables(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {

            st.executeUpdate("CREATE TABLE IF NOT EXISTS users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "username VARCHAR(50) NOT NULL UNIQUE, "
                + "password CHAR(64) NOT NULL, "
                + "name VARCHAR(100) NOT NULL, "
                + "role VARCHAR(10) NOT NULL DEFAULT 'MEMBER')");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS workouts ("
                + "workout_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "user_id INT NOT NULL, "
                + "name VARCHAR(100) NOT NULL, "
                + "duration INT NOT NULL, "
                + "calories_burned INT NOT NULL, "
                + "workout_date DATE NOT NULL, "
                + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS meals ("
                + "meal_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "user_id INT NOT NULL, "
                + "name VARCHAR(100) NOT NULL, "
                + "calories INT NOT NULL, "
                + "protein INT NOT NULL DEFAULT 0, "
                + "carbs INT NOT NULL DEFAULT 0, "
                + "fat INT NOT NULL DEFAULT 0, "
                + "nutritional_info VARCHAR(255), "
                + "meal_date DATE NOT NULL, "
                + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS goals ("
                + "goal_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "user_id INT NOT NULL, "
                + "title VARCHAR(100) NOT NULL, "
                + "goal_type VARCHAR(30) NOT NULL, "
                + "target_value INT NOT NULL, "
                + "start_date DATE NOT NULL, "
                + "deadline DATE NOT NULL, "
                + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)");

            // Default accounts, only added if they don't exist yet
            st.executeUpdate("INSERT IGNORE INTO users (id, username, password, name, role) VALUES "
                + "(1, 'admin', SHA2('admin123', 256), 'Administrator', 'ADMIN')");
            st.executeUpdate("INSERT IGNORE INTO users (username, password, name, role) VALUES "
                + "('guest', SHA2('guest', 256), 'Guest', 'MEMBER')");
        }
    }
}