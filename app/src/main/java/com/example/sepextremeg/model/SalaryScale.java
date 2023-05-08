package com.example.sepextremeg.model;

public class SalaryScale {

    String salaryCode;
    String basicSalary;
    String researchAllowancePercentage;
    String allowances;
    String deductionRate;
    String taxRate;

    public SalaryScale() {
    }

    public SalaryScale(String salaryCode, String basicSalary, String researchAllowancePercentage,
                       String allowances, String deductionRate, String taxRate) {
        this.salaryCode = salaryCode;
        this.basicSalary = basicSalary;
        this.researchAllowancePercentage = researchAllowancePercentage;
        this.allowances = allowances;
        this.deductionRate = deductionRate;
        this.taxRate = taxRate;
    }

    public String getSalaryCode() {
        return salaryCode;
    }

    public void setSalaryCode(String salaryCode) {
        this.salaryCode = salaryCode;
    }

    public String getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(String basicSalary) {
        this.basicSalary = basicSalary;
    }

    public String getResearchAllowancePercentage() {
        return researchAllowancePercentage;
    }

    public void setResearchAllowancePercentage(String researchAllowancePercentage) {
        this.researchAllowancePercentage = researchAllowancePercentage;
    }

    public String getAllowances() {
        return allowances;
    }

    public void setAllowances(String allowances) {
        this.allowances = allowances;
    }

    public String getDeductionRate() {
        return deductionRate;
    }

    public void setDeductionRate(String deductionRate) {
        this.deductionRate = deductionRate;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }
}
