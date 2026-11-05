package model;

import java.util.ArrayList;

/** A person who can log in. role is "ADMIN" or "MEMBER". */
public class User {

    private int id;
    private String username;
    private String password;      // SHA-256 hash, never the plain text
    private String name;
    private String role;
    private ArrayList<Workout> workouts = new ArrayList<Workout>();
    private ArrayList<Meal> meals = new ArrayList<Meal>();

    public User() {
    }

    public User(int id, String username, String password, String name, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public ArrayList<Workout> getWorkouts() { return workouts; }
    public void setWorkouts(ArrayList<Workout> workouts) { this.workouts = workouts; }

    public ArrayList<Meal> getMeals() { return meals; }
    public void setMeals(ArrayList<Meal> meals) { this.meals = meals; }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    @Override
    public String toString() {
        return "User[" + id + ", " + username + ", " + name + ", " + role + "]";
    }
}
