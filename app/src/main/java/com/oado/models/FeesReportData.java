package com.oado.models;

import java.io.Serializable;
import java.util.ArrayList;

public class FeesReportData implements Serializable {

    private String id;
    private String institute_id;
    private String student_id;
    private String class_id;
    private String total_amount;
    private String pay_amount;
    private String month_name;
    private String pay_date;
    private String type_of_center;
    private ArrayList<FeesAmounts> listAmounts;


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

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getPay_amount() {
        return pay_amount;
    }

    public void setPay_amount(String pay_amount) {
        this.pay_amount = pay_amount;
    }

    public String getMonth_name() {
        return month_name;
    }

    public void setMonth_name(String month_name) {
        this.month_name = month_name;
    }

    public String getPay_date() {
        return pay_date;
    }

    public void setPay_date(String pay_date) {
        this.pay_date = pay_date;
    }

    public String getType_of_center() {
        return type_of_center;
    }

    public void setType_of_center(String type_of_center) {
        this.type_of_center = type_of_center;
    }

    public ArrayList<FeesAmounts> getListAmounts() {
        return listAmounts;
    }

    public void setListAmounts(ArrayList<FeesAmounts> listAmounts) {
        this.listAmounts = listAmounts;
    }
}
