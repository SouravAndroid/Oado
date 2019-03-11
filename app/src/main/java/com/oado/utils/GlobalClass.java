package com.oado.utils;

import android.app.Application;

import com.oado.models.ClassData;
import com.oado.models.DiaryMessage;
import com.oado.models.SectionData;
import com.oado.models.SubjectData;

import java.util.ArrayList;

public class GlobalClass extends Application {

    private static GlobalClass mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized GlobalClass getInstance() {
        return mInstance;
    }


    private ArrayList<SubjectData> listSubject = new ArrayList<>();
    private ArrayList<ClassData> listClass = new ArrayList<>();
    private ArrayList<SectionData> listSection = new ArrayList<>();

    public ArrayList<SubjectData> getListSubject() {
        return listSubject;
    }

    public void setListSubject(ArrayList<SubjectData> listSubject) {
        this.listSubject = listSubject;
    }

    public ArrayList<ClassData> getListClass() {
        return listClass;
    }

    public void setListClass(ArrayList<ClassData> listClass) {
        this.listClass = listClass;
    }

    public ArrayList<SectionData> getListSection() {
        return listSection;
    }

    public void setListSection(ArrayList<SectionData> listSection) {
        this.listSection = listSection;
    }


    private ArrayList<DiaryMessage> diaryMessageArrayList = new ArrayList<>();

    public ArrayList<DiaryMessage> getDiaryMessageArrayList() {
        return diaryMessageArrayList;
    }

    public void setDiaryMessageArrayList(ArrayList<DiaryMessage> diaryMessageArrayList) {
        this.diaryMessageArrayList = diaryMessageArrayList;
    }
}
