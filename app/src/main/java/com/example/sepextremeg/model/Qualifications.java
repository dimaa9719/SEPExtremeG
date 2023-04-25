package com.example.sepextremeg.model;

public class Qualifications {

    String qualificationId;
    String qualificationTitle;
    String qualificationType;
    String qualificationFile;
    int selectedITemPosition;

    public Qualifications(String qualificationTitle, String qualificationType, String qualificationFile) {
        this.qualificationTitle = qualificationTitle;
        this.qualificationType = qualificationType;
        this.qualificationFile = qualificationFile;
    }

    public Qualifications() {

    }

    public String getQualificationId() {
        return qualificationId;
    }

    public void setQualificationId(String qualificationId) {
        this.qualificationId = qualificationId;
    }

    public String getQualificationTitle() {
        return qualificationTitle;
    }

    public void setQualificationTitle(String qualificationTitle) {
        this.qualificationTitle = qualificationTitle;
    }

    public String getQualificationType() {
        return qualificationType;
    }

    public void setQualificationType(String qualificationType) {
        this.qualificationType = qualificationType;
    }

    public String getQualificationFile() {
        return qualificationFile;
    }

    public void setQualificationFile(String qualificationFile) {
        this.qualificationFile = qualificationFile;
    }

    public int getSelectedITemPosition() {
        return selectedITemPosition;
    }

    public void setSelectedITemPosition(int selectedITemPosition) {
        this.selectedITemPosition = selectedITemPosition;
    }
}
