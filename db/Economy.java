package org.example.listeners.db;

public class Economy {
    private String user_id;
    private double user_bal;

    public Economy() {
        // Default constructor
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public double getUser_bal() {
        return user_bal;
    }

    public void setUser_bal(double user_bal) {
        this.user_bal = user_bal;
    }
}

