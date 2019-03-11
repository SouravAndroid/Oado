package com.oado.utils;

import android.content.Context;

public class UserPermissionCheck {

    private Context context;
    private PrefManager prefManager;


    public UserPermissionCheck(Context context) {
        this.context = context;
        prefManager = new PrefManager(context);

    }


    public boolean isAdmin(){
        if (prefManager.getLoginType().equals(ApiClient.admin)){
            return true;
        }else {
            return false;
        }
    }

    public boolean isInstitute(){
        if (prefManager.getLoginType().equals(ApiClient.institute)){
            return true;
        }else {
            return false;
        }
    }

    public boolean isTeacher(){
        if (prefManager.getLoginType().equals(ApiClient.teacher)){
            return true;
        }else {
            return false;
        }
    }

    public boolean isStaff(){
        if (prefManager.getLoginType().equals(ApiClient.other_staff)){
            return true;
        }else {
            return false;
        }
    }

    public boolean isStudent(){
        if (prefManager.getLoginType().equals(ApiClient.student)){
            return true;
        }else {
            return false;
        }
    }

    public boolean isGuardian(){
        if (prefManager.getLoginType().equals(ApiClient.guardian)){
            return true;
        }else {
            return false;
        }
    }



}
