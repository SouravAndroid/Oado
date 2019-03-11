package com.oado.utils;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.oado.R;

import es.dmoral.toasty.Toasty;

/**
 * Created by Developer on 1/16/18.
 */

public class ValidationClass {

    private Context context;
    private String android_id;
    private String fcm_token;

    public ValidationClass(Context context) {
        this.context = context;

        android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);


    }

    public String getAndroid_id() {
        return android_id;
    }

    //////////////////////////////////////////////

    public boolean validateName(EditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            editText.requestFocus();
            Toasty.info(context,
                    context.getResources().getString(R.string.msg_enter_name),
                    Toast.LENGTH_SHORT, true).show();
            return false;
        }

        return true;
    }


    public boolean validateEmail(EditText editText) {
        String email = editText.getText().toString().trim();

        if (email.isEmpty()) {
            editText.requestFocus();
            Toasty.info(context,
                    context.getResources().getString(R.string.msg_enter_email),
                    Toast.LENGTH_SHORT, true).show();
            return false;
        } else {

            if (!isValidEmail(email)){
                Toasty.info(context,
                        context.getResources().getString(R.string.msg_enter_valid_email),
                        Toast.LENGTH_SHORT, true).show();
                editText.requestFocus();
                return false;

            }

        }

        return true;
    }

    public boolean validateMobileNo(EditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            Toasty.info(context,
                    context.getResources().getString(R.string.msg_enter_mobile),
                    Toast.LENGTH_SHORT, true).show();
            editText.requestFocus();
            return false;
        } else
        if (editText.getText().toString().trim().length() < 10){
            Toasty.info(context,
                    context.getResources().getString(R.string.msg_enter_10digitmobile),
                    Toast.LENGTH_SHORT, true).show();
            editText.requestFocus();
            return false;
        }

        return true;
    }

    public boolean validatePassword1(EditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            Toasty.info(context,
                    context.getResources().getString(R.string.msg_enter_password),
                    Toast.LENGTH_SHORT, true).show();
            editText.requestFocus();
            return false;
        } else
        if (editText.getText().toString().trim().length() < 8){

            Toasty.info(context,
                    context.getResources().getString(R.string.msg_enter_6character_password),
                    Toast.LENGTH_SHORT, true).show();
            editText.requestFocus();
            return false;
        }

        return true;
    }

    public boolean validatePassword2(EditText editText1, EditText editText2) {
        if (editText2.getText().toString().trim().isEmpty()) {
            editText2.setError(context.getResources().getString(R.string.msg_enter_confirm_password));
            editText2.requestFocus();
            return false;
        } else {

            if (validatePassword1(editText1)){
                return CheckEqualPassword(editText1, editText2);
            }
        }

        return true;
    }

    public boolean CheckEqualPassword(EditText editText1, EditText editText2) {
        String p1 = editText1.getText().toString().trim();
        String p2 = editText2.getText().toString().trim();

        if (!p1.equals(p2)) {
            editText2.setError(context.getResources().getString(R.string.msg_password_not_same));
            editText2.requestFocus();
            return false;
        }

        return true;
    }



    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public boolean validateOtp(EditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            editText.requestFocus();
            editText.setError(context.getResources().getString(R.string.msg_otp));
            return false;
        }

        return true;
    }



    public boolean validateIsEmpty(EditText editText, String msg) {
        if (editText.getText().toString().trim().isEmpty()) {
            editText.requestFocus();
            Toasty.info(context,
                    msg, Toast.LENGTH_SHORT, true).show();
            return false;
        }

        return true;
    }



}
