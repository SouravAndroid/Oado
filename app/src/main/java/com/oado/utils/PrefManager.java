package com.oado.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    private Context context;
    private SharedPreferences pref1;
    SharedPreferences.Editor editor1;

    private static final String MyPREFERENCES = "myPref1";

    private static final String key_is_login = "key_is_login";
    private static final String key_id = "key_id";
    private static final String key_name = "key_name";
    private static final String key_email = "key_email";
    private static final String key_phone = "key_phone";
    private static final String key_image = "key_image";
    private static final String key_address = "key_address";
    private static final String key_city = "key_city";
    private static final String key_state = "key_state";
    private static final String key_country = "key_country";
    private static final String key_pincode = "key_pincode";
    private static final String key_login_type = "key_login_type";
    private static final String key_institute_type = "key_institute_type";
    private static final String key_institute_id = "key_institute_id";
    private static final String key_student_id = "key_student_id";
    private static final String key_guardian_id = "key_guardian_id";
    private static final String key_class_id = "key_class_id";
    private static final String key_section_id = "key_section_id";

    public PrefManager(Context context) {
        this.context = context;
        pref1 = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor1 = pref1.edit();

    }


    public void logOut(){
        editor1.clear();
        editor1.commit();
    }


    public void setLogin(boolean boo){
        editor1.putBoolean(key_is_login, boo);
        editor1.commit();
    }

    public boolean isLogin(){
        return pref1.getBoolean(key_is_login, false);
    }


    public void setUserData(String id, String name, String email, String phone, String image,
                            String address, String city, String state, String country, String pincode,
                            String login_type){

        editor1.putString(key_id, id);
        editor1.putString(key_name, name);
        editor1.putString(key_email, email);
        editor1.putString(key_phone, phone);
        editor1.putString(key_image, image);
        editor1.putString(key_address, address);
        editor1.putString(key_city, city);
        editor1.putString(key_state, state);
        editor1.putString(key_country, country);
        editor1.putString(key_pincode, pincode);
        editor1.putString(key_login_type, login_type);


        editor1.commit();

    }



    public void setInstitute_id(String sid) {
        editor1.putString(key_institute_id, sid);
        editor1.commit();
    }

    public String getInstitute_id() {
        return pref1.getString(key_institute_id, "");
       // return "37";
    }


    public String getId() {
        return pref1.getString(key_id, "");
    }


    public String getName() {
        return pref1.getString(key_name, "");
    }


    public String getPhone() {
        return pref1.getString(key_phone, "");
    }


    public String getImage() {
        return pref1.getString(key_image, "");
    }


    public String getEmail() {
        return pref1.getString(key_email, "");
    }

    public String getLoginType() {
        return pref1.getString(key_login_type, "");
    }


    public void setInstituteType(String type){
        editor1.putString(key_institute_type, type);
        editor1.commit();
    }

    public String getInstituteType() {
        return pref1.getString(key_institute_type, "");
        //return "school";
       // return "coaching_center";
    }


    public void setGuardian_id(String sid) {
        editor1.putString(key_guardian_id, sid);
        editor1.commit();
    }

    public String getGuardian_id() {
        return pref1.getString(key_guardian_id, "");
    }

    public void setStudent_id(String sid) {
        editor1.putString(key_student_id, sid);
        editor1.commit();
    }

    public String getStudent_id() {
        return pref1.getString(key_student_id, "");
    }


    public void setClass_id(String sid) {
        editor1.putString(key_class_id, sid);
        editor1.commit();
    }

    public String getClass_id() {
        return pref1.getString(key_class_id, "");
    }


    public void setSection_id(String sid) {
        editor1.putString(key_section_id, sid);
        editor1.commit();
    }

    public String getSection_id() {
        return pref1.getString(key_section_id, "");
    }


    ///////////////////////// for notification counter ...
    private static final String key1 = "key1";
    private static final String key2 = "key2";
    private static final String key3 = "key3";
    private static final String key4 = "key4";
    private static final String key5 = "key5";
    private static final String key6 = "key6";

    public void setKeyCounter1(int c){
        editor1.putInt(key1, c);
        editor1.commit();
    }

    public int getKey1() {
        return pref1.getInt(key1, 0);
    }

    public void setKeyCounter2(int c){
        editor1.putInt(key2, c);
        editor1.commit();
    }

    public int getKey2() {
        return pref1.getInt(key2, 0);
    }

    public void setKeyCounter3(int c){
        editor1.putInt(key3, c);
        editor1.commit();
    }

    public int getKey3() {
        return pref1.getInt(key3, 0);
    }

    public void setKeyCounter4(int c){
        editor1.putInt(key4, c);
        editor1.commit();
    }

    public int getKey4() {
        return pref1.getInt(key4, 0);
    }

    public void setKeyCounter5(int c){
        editor1.putInt(key5, c);
        editor1.commit();
    }

    public int getKey5() {
        return pref1.getInt(key5, 0);
    }

    public void setKeyCounter6(int c){
        editor1.putInt(key6, c);
        editor1.commit();
    }

    public int getKey6() {
        return pref1.getInt(key6, 0);
    }
}

