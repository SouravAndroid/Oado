package com.oado.models;

import java.io.Serializable;

public class AttendanceReportData implements Serializable {

    private int stu_id;
    private String name;
    private String email;
    private String roll_no;
    private String image;
    private String attendance_data;
    private String p_count;
    private String l_count;
    private String a_count;

    public int getStu_id() {
        return stu_id;
    }

    public void setStu_id(int stu_id) {
        this.stu_id = stu_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoll_no() {
        return roll_no;
    }

    public void setRoll_no(String roll_no) {
        this.roll_no = roll_no;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAttendance_data() {
        return attendance_data;
    }

    public void setAttendance_data(String attendance_data) {
        this.attendance_data = attendance_data;
    }

    public String getP_count() {
        return p_count;
    }

    public void setP_count(String p_count) {
        this.p_count = p_count;
    }

    public String getL_count() {
        return l_count;
    }

    public void setL_count(String l_count) {
        this.l_count = l_count;
    }

    public String getA_count() {
        return a_count;
    }

    public void setA_count(String a_count) {
        this.a_count = a_count;
    }
}
