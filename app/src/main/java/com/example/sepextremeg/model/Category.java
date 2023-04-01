package com.example.sepextremeg.model;

import java.util.ArrayList;

public class Category {

    String id;
    String userRole;
    ArrayList<StaffModel> staffModels;

    public Category( String id, String userRole, ArrayList<StaffModel> staffModels) {
        this.id = id;
        this.userRole = userRole;
        this.staffModels = staffModels;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public ArrayList<StaffModel> getStaffModels() {
        return staffModels;
    }

    public void setStaffModels(ArrayList<StaffModel> staffModels) {
        this.staffModels = staffModels;
    }
}
