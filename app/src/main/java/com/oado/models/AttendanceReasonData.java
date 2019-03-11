package com.oado.models;

import java.io.Serializable;

public class AttendanceReasonData implements Serializable {

    private int stu_id;
    private String atten_type;
    private String atten_date;
    private String atten_time;
    private String late_time;
    private String reason;

    public int getStu_id() {
        return stu_id;
    }

    public void setStu_id(int stu_id) {
        this.stu_id = stu_id;
    }

    public String getAtten_type() {
        return atten_type;
    }

    public void setAtten_type(String atten_type) {
        this.atten_type = atten_type;
    }

    public String getAtten_date() {
        return atten_date;
    }

    public void setAtten_date(String atten_date) {
        this.atten_date = atten_date;
    }

    public String getAtten_time() {
        return atten_time;
    }

    public void setAtten_time(String atten_time) {
        this.atten_time = atten_time;
    }

    public String getLate_time() {
        return late_time;
    }

    public void setLate_time(String late_time) {
        this.late_time = late_time;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
