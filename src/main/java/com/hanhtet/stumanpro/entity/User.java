package com.hanhtet.stumanpro.entity;
import org.apache.commons.codec.digest.DigestUtils;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String Id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String phone_no;
    private String picture;
    private String address;
    private String role;
    public User(String firstname,String lastname, String email, String password, String phone_no, String picture, String address, String role) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        setPassword(password);
        this.phone_no = phone_no;
        this.picture = picture;
        this.address = address;
        this.role = role;
    }
    public void setId(String id){
        this.Id = id;
    }
    public String getId(){
        return this.Id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
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
        this.password = hashPassword(password);
    }
    public void setPasswordWithoutHash(String password){
        this.password = password;
    }

    private String hashPassword(String plainTextPassword) {
        return DigestUtils.sha256Hex(plainTextPassword);
    }

    public boolean checkPassword(String plainTextPassword) {
        return hashPassword(plainTextPassword).equals(this.password);
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName(){
        return this.firstname + " " + this.lastname;
    }
    
    public List<Object> getAllDataAsList() {
        List<Object> userData = new ArrayList<>();
        userData.add(firstname);
        userData.add(lastname);
        userData.add(email);
        userData.add(password);
        userData.add(phone_no);
        userData.add(picture);
        userData.add(address);
        userData.add(role);
        return userData;
    }

    public void setName(String newValue) {
        this.firstname = newValue;
        this.lastname = "";
    }
}
