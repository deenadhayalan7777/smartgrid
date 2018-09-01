package com.example.balasakthi.smartgrid.Admin;

public class AdminUser {

    private static String name;
    private static String pincode;
    private static String email;
    private static String empid;

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        AdminUser.name = name;
    }

    public static String getPincode() {
        return pincode;
    }

    public static void setPincode(String pincode) {
        AdminUser.pincode = pincode;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        AdminUser.email = email;
    }

    public static String getEmpid() {
        return empid;
    }

    public static void setEmpid(String empid) {
        AdminUser.empid = empid;
    }
}
