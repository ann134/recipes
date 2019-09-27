package ru.sigmadigital.recipes.model;

import com.google.gson.Gson;

import java.util.Date;

public class User {

    private String email;
    private String password;
    private String name;

    private int gender;
    private Date birthday;
    private String city;

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /*public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }*/


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static User fromJson(String json){
        return new Gson().fromJson(json, User.class);
    }
}
