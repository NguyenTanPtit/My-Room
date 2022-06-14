package model;

import androidx.annotation.NonNull;

public class User {
    private String email,name,password,profileImg,sex,phone,address,yearOfBirth, role;


    public User() {
    }

    public User(String email, String name, String password, String sex, String phone, String address, String role) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.sex = sex;
        this.phone = phone;
        this.address = address;
        this.role = role;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public String getSex() {
        return sex;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(String yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public String getRole() {
        return role;
    }

    @NonNull
    @Override
    public String toString() {
        return name+" "+email+" "+password;
    }
}
