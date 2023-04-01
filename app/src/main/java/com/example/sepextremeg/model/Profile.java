package com.example.sepextremeg.model;

public class Profile {
    private String fullName;
    private String dateOfBirth;
    private String nic;
    private String phone;
    private String address;
    private String qualification;
    private String workExp;
    private String facultyName;
    private String jobTitle;
    private String publication;
    private String datePublished;

    public Profile() {

    }

    public Profile(String fullName, String dateOfBirth, String nic, String phone, String address,
                   String qualification, String workExp,  String facultyName, String jobTitle, String publication, String datePublished) {
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.nic = nic;
        this.phone = phone;
        this.address = address;
        this.qualification = qualification;
        this.workExp = workExp;
        this.facultyName = facultyName;
        this.jobTitle = jobTitle;
        this.publication = publication;
        this.datePublished = datePublished;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getWorkExp() {
        return workExp;
    }

    public void setWorkExp(String workExp) {
        this.workExp = workExp;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getPublication() {
        return publication;
    }

    public void setPublication(String publication) {
        this.publication = publication;
    }

    public String getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(String datePublished) {
        this.datePublished = datePublished;
    }
}
