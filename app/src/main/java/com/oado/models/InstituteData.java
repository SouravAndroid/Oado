package com.oado.models;

import java.io.Serializable;

public class InstituteData implements Serializable {

    private String id;
    private String name;
    private String code;
    private String board_name;
    private String mobile;
    private String email;
    private String street;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private String institute_type;
    private String sms_subcription;
    private String image;
    private int lock_status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBoard_name() {
        return board_name;
    }

    public void setBoard_name(String board_name) {
        this.board_name = board_name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getInstitute_type() {
        return institute_type;
    }

    public void setInstitute_type(String institute_type) {
        this.institute_type = institute_type;
    }

    public String getSms_subcription() {
        return sms_subcription;
    }

    public void setSms_subcription(String sms_subcription) {
        this.sms_subcription = sms_subcription;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public int getLock_status() {
        return lock_status;
    }

    public void setLock_status(int lock_status) {
        this.lock_status = lock_status;
    }
}
