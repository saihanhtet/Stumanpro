package com.hanhtet.stumanpro.entity;
import org.apache.commons.codec.digest.DigestUtils;
public class Login {
    private String email;

    public Login(String email, String password) {
        this.email = email;
        setPassword(password);
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
    private String hashPassword(String plainTextPassword) {
        return DigestUtils.sha256Hex(plainTextPassword);
    }

    private String password;
}
