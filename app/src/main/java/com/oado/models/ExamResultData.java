package com.oado.models;

import java.io.Serializable;

public class ExamResultData implements Serializable {

    private String id;
    private String subject_id;
    private String subject_name;
    private String total_marks;
    private String scored_marks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(String subject_id) {
        this.subject_id = subject_id;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getTotal_marks() {
        return total_marks;
    }

    public void setTotal_marks(String total_marks) {
        this.total_marks = total_marks;
    }

    public String getScored_marks() {
        return scored_marks;
    }

    public void setScored_marks(String scored_marks) {
        this.scored_marks = scored_marks;
    }
}
