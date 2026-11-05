package service;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.LinkedList;
import model.User;


public class UserService {

    private static final String COLUMNS = "id, username, password, name, role";

    private User readUser(ResultSet rs) throws SQLException {
        return new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
    }

    public User[] getAll() throws SQLException {
        LinkedList<User> list = new LinkedList<User>();
        String sql = "SELECT " + COLUMNS + " FROM users ORDER BY id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(readUser(rs));
            }
        }
        return list.toArray(new User[0]);
    }

    public User findById(int id) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM users WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? readUser(rs) : null;
            }
        }
    }

   
    public User login(String username, String password) throws SQLException {
        if (username == null || username.trim().isEmpty() || password == null) {
            return null;
        }
        String sql = "SELECT " + COLUMNS + " FROM users WHERE username = ? AND password = SHA2(?, 256)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? readUser(rs) : null;
            }
        }
    }

    public void addUser(User u, String plainPassword) throws SQLException {
        validate(u);
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password is required for a new user.");
        }
        String sql = "INSERT INTO users (username, password, name, role) VALUES (?, SHA2(?, 256), ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getUsername().trim());
            ps.setString(2, plainPassword);
            ps.setString(3, u.getName().trim());
            ps.setString(4, u.getRole());
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("That username is already taken.");
        }
    }


    public void updateUser(User u, String newPlainPassword) throws SQLException {
        validate(u);
        boolean changePassword = newPlainPassword != null && !newPlainPassword.isEmpty();
        String sql = changePassword
                ? "UPDATE users SET username = ?, name = ?, role = ?, password = SHA2(?, 256) WHERE id = ?"
                : "UPDATE users SET username = ?, name = ?, role = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getUsername().trim());
            ps.setString(2, u.getName().trim());
            ps.setString(3, u.getRole());
            if (changePassword) {
                ps.setString(4, newPlainPassword);
                ps.setInt(5, u.getId());
            } else {
                ps.setInt(4, u.getId());
            }
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("User not found.");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("That username is already taken.");
        }
    }

   
    public void deleteUser(int id) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM users WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private void validate(User u) {
        if (u.getUsername() == null || u.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (u.getName() == null || u.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (!"ADMIN".equals(u.getRole()) && !"MEMBER".equals(u.getRole())) {
            throw new IllegalArgumentException("Role must be ADMIN or MEMBER.");
        }
    }
}
