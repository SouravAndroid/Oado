package com.oado.models;

import java.io.Serializable;
import java.util.ArrayList;

public class StudentData implements Serializable {

    private String id;
    private String name;
    private String class_id;
    private String section_id;
    private String subject_ids;
    private ArrayList<String> list_subject;
    private String class_name;
    private String section_name;
    private String institute_id;
    private String roll_no;
    private String gender;
    private String dob;
    private String mobile;
    private String email;
    private String street;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private String barcode;
    private String image;
    private int attendance_status;
    private ArrayList<ExamResultData> list_ExamResult;

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

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }


    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getSection_id() {
        return section_id;
    }

    public void setSection_id(String section_id) {
        this.section_id = section_id;
    }


    public String getSubject_ids() {
        return subject_ids;
    }

    public void setSubject_ids(String subject_ids) {
        this.subject_ids = subject_ids;
    }

    public ArrayList<String> getList_subject() {
        return list_subject;
    }

    public void setList_subject(ArrayList<String> list_subject) {
        this.list_subject = list_subject;
    }

    public String getSection_name() {
        return section_name;
    }

    public void setSection_name(String section_name) {
        this.section_name = section_name;
    }

    public String getInstitute_id() {
        return institute_id;
    }

    public void setInstitute_id(String institute_id) {
        this.institute_id = institute_id;
    }

    public String getRoll_no() {
        return roll_no;
    }

    public void setRoll_no(String roll_no) {
        this.roll_no = roll_no;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getAttendance_status() {
        return attendance_status;
    }

    public void setAttendance_status(int attendance_status) {
        this.attendance_status = attendance_status;
    }

    public ArrayList<ExamResultData> getList_ExamResult() {
        return list_ExamResult;
    }

    public void setList_ExamResult(ArrayList<ExamResultData> list_ExamResult) {
        this.list_ExamResult = list_ExamResult;
    }
}
