package com.oado.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ClassData implements Serializable {

    private String id;
    private String name;
    private String institute_id;
    private ArrayList<SectionData> list_Section;

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

    public ArrayList<SectionData> getList_Section() {
        return list_Section;
    }

    public void setList_Section(ArrayList<SectionData> list_Section) {
        this.list_Section = list_Section;
    }
}
