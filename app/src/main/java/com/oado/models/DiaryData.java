package com.oado.models;

import java.io.Serializable;
import java.util.ArrayList;

public class DiaryData implements Serializable {

    private String id;
    private String name;
    private String institute_id;
    private String created_by;
    private String user_ids;
    private ArrayList<DiaryMembersData> list_Members;

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

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getUser_ids() {
        return user_ids;
    }

    public void setUser_ids(String user_ids) {
        this.user_ids = user_ids;
    }

    public ArrayList<DiaryMembersData> getList_Members() {
        return list_Members;
    }

    public void setList_Members(ArrayList<DiaryMembersData> list_Members) {
        this.list_Members = list_Members;
    }
}
