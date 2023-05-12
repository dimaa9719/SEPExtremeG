package com.example.sepextremeg.model;

public class StaffModel {

    String id;
    String name;
    String email;
    String proUrl;
    String password;
    String role;
    String serviceNo;

    public StaffModel() {

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServiceNo() {
        return serviceNo;
    }

    public void setServiceNo(String serviceNo) {
        this.serviceNo = serviceNo;
    }
}
