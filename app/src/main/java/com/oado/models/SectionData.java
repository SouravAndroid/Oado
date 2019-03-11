package com.oado.models;

import java.io.Serializable;
import java.util.ArrayList;

public class SectionData implements Serializable {

    private String id;
    private String name;
    private String institute_id;
    private ArrayList<SubjectData> list_Subject;

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

    public String getInstitute_id() {
        return institute_id;
    }

    public void setInstitute_id(String institute_id) {
        this.institute_id = institute_id;
    }

    public ArrayList<SubjectData> getList_Subject() {
        return list_Subject;
    }

    public void setList_Subject(ArrayList<SubjectData> list_Subject) {
        this.list_Subject = list_Subject;
    }
}
