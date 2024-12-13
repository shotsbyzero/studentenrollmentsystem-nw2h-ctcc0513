package javaapplication31;

public class Course {
    private String code;
    private String name;
    private int units;
    private double fee;
  
    public Course(String code, String name, int units, double fee) {
      this.code = code;
      this.name = name;
      this.units = units;
      this.fee = fee;
    }
  
    public String getCode() {
      return code;
    }
  
    public String getName() {
      return name;
    }
  
    public int getUnits() {
      return units;
    }
  
    public double getFee() {
      return fee;
    }
  
    @Override
    public String toString() {
      return "Course Code: " + code + ", Name: " + name + ", Units: " + units + ", Fee: " + fee;
    }
  }