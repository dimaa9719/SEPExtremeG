package com.example.sepextremeg.model;

public class StaffModel {

    String id;
    String name;
    String email;
    String proUrl;
    String password;
    String role;

    public StaffModel(String id, String name, String email, String proUrl, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.proUrl = proUrl;
        this.password = password;
        this.role = role;
    }

    public StaffModel(String id, String role, String name, String proUrl) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.proUrl = proUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProUrl() {
        return proUrl;
    }

    public void setProUrl(String proUrl) {
        this.proUrl = proUrl;
    }


}
