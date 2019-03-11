package com.oado.models;

import java.io.Serializable;

public class FeesData implements Serializable {

    private String id;
    private String institute_id;
    private String class_id;
    private String subject_id;
    private String fees_amount;
    private String type_of_center;
    private String class_name;
    private String subject_name;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstitute_id() {
        return institute_id;
    }

    public void setInstitute_id(String institute_id) {
        this.institute_id = institute_id;
    }

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(String subject_id) {
        this.subject_id = subject_id;
    }

    public String getFees_amount() {
        return fees_amount;
    }

    public void setFees_amount(String fees_amount) {
        this.fees_amount = fees_amount;
    }

    public String getType_of_center() {
        return type_of_center;
    }

    public void setType_of_center(String type_of_center) {
        this.type_of_center = type_of_center;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }
}
