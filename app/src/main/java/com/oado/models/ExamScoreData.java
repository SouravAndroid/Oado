package com.oado.models;

import java.io.Serializable;

public class ExamScoreData implements Serializable {

    private String id;
    private String subject_name;
    private String marks;
    private String full_marks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getFull_marks() {
        return full_marks;
    }

    public void setFull_marks(String full_marks) {
        this.full_marks = full_marks;
    }
}
