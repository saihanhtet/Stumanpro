package com.hanhtet.stumanpro.utils;

public class UserSession {
    private String email;
    private String id;
    private String password;
    private String role;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    private static UserSession instance;
    private UserSession() {
        // Private constructor to enforce the Singleton pattern
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }
    public void logoutUser(){
        this.email = "";
        this.id = "";
        this.password = "";
        this.role = "";
        this.name = "";
        this.phone_no = "";
        this.picture = "";
        this.address = "";
    }
    public void loginUser(String id, String name, String email, String password, String phone_no, String picture, String address, String role) {
        this.email = email;
        this.id = id;
        this.password = password;
        this.role = role;
        this.name = name;
        this.phone_no = phone_no;
        this.picture = picture;
        this.address = address;
    }

    private String name;
    private String phone_no;
    private String picture;
    private String address;


    public boolean isAdmin(){
        return this.role.equals("admin");
    }

    public boolean isTeacher(){
        return this.role.equals("teacher");
    }
    public boolean isStudent(){
        return this.role.equals("student");
    }
}
