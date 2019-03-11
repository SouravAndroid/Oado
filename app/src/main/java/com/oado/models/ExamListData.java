package com.oado.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ExamListData implements Serializable {

    private String id;
    private String name;
    private String student_name;
    private String student_email;
    private String student_phone;
    private String student_roll;
    private String image;
    private String class_name;
    private String section_name;

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

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getStudent_email() {
        return student_email;
    }

    public void setStudent_email(String student_email) {
        this.student_email = student_email;
    }

    public String getStudent_phone() {
        return student_phone;
    }

    public void setStudent_phone(String student_phone) {
        this.student_phone = student_phone;
    }

    public String getStudent_roll() {
        return student_roll;
    }

    public void setStudent_roll(String student_roll) {
        this.student_roll = student_roll;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getSection_name() {
        return section_name;
    }

    public void setSection_name(String section_name) {
        this.section_name = section_name;
    }

    public ArrayList<ExamResultData> getList_ExamResult() {
        return list_ExamResult;
    }

    public void setList_ExamResult(ArrayList<ExamResultData> list_ExamResult) {
        this.list_ExamResult = list_ExamResult;
    }
}
