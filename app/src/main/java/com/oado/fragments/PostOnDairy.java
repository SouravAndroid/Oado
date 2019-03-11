package com.oado.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.adapters.MySpinnerAdapter;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.ConnectivityReceiver;
import com.oado.utils.Constants;
import com.oado.utils.PrefManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;

public class PostOnDairy extends Fragment {

    public PostOnDairy() {
    }

    Unbinder unbinder;

    @BindView(R.id.spinner_dairy) Spinner spinner_dairy;
    @BindView(R.id.spinner_category) Spinner spinner_category;

    @BindView(R.id.card_view_announcement) CardView card_view_announcement;
    @BindView(R.id.card_view_event) CardView card_view_event;
    @BindView(R.id.card_view_image) CardView card_view_image;
    @BindView(R.id.card_view_link) CardView card_view_link;

    @BindView(R.id.edt_announcement) EditText edt_announcement;

    @BindView(R.id.edt_event_name) EditText edt_event_name;
    @BindView(R.id.btn_start_date_time) Button btn_start_date_time;
    @BindView(R.id.tv_start_date_time) TextView tv_start_date_time;
    @BindView(R.id.btn_end_date_time) Button btn_end_date_time;
    @BindView(R.id.tv_end_date_time) TextView tv_end_date_time;

    @BindView(R.id.edt_image_title) EditText edt_image_title;
    @BindView(R.id.btn_select_image) Button btn_select_image;
    @BindView(R.id.iv_image) ImageView iv_image;

    @BindView(R.id.edt_link_title) EditText edt_link_title;
    @BindView(R.id.edt_link) EditText edt_link;

    @BindView(R.id.rel_post_content) RelativeLayout rel_post_content;


    int msg_type;
    String message, diary_id;
    File p_image;

    private int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 1888;
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1;

    ProgressDialog progressDialog;
    PrefManager prefManager;
    ArrayList<HashMap<String, String>> list_Diary;
    MySpinnerAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_create_post, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        unbinder = ButterKnife.bind(this, view);

        initViews();



        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // unbind the view to free some memory
        unbinder.unbind();
    }

    private void initViews(){

        prefManager = new PrefManager(getActivity());

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);




        setItems();

        hideCardViews();

        getMyDiary();


        btn_start_date_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initDateTimePicker("start");
            }
        });

        btn_end_date_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initDateTimePicker("end");

            }
        });

        btn_select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage();
            }
        });


        rel_post_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityReceiver.isConnected()){
                    checkValidation();
                }else {
                    Toasty.error(getActivity(),
                            "No internet connection available",
                            Toast.LENGTH_SHORT, true).show();
                }

            }
        });


    }


    public void setItems(){

        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("name", "Select Type");
        arrayList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "Announcement");
        arrayList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "Event");
        arrayList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "Photo");
        arrayList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "Youtube Link / Others Link");
        arrayList.add(hashMap);

        adapter = new MySpinnerAdapter(getActivity(), arrayList);
        spinner_category.setAdapter(adapter);


        spinner_dairy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.app_blue));

                if (position > 0){
                    diary_id = list_Diary.get(position).get("id");
                }else {
                    diary_id = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.app_blue));

                showHideCardView(position);

                msg_type = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    SwitchDateTimeDialogFragment dateTimeDialogFragment;
    public void initDateTimePicker(final String what){

        dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                "Select Date Time",
                "OK",
                "Cancel"
        );

        try {
            dateTimeDialogFragment.setSimpleDateMonthAndDayFormat(
                    new SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e(Commons.TAG, e.getMessage());
        }


        // Set listener
        dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                // Date is get on positive button click
                // Do something
                DateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault());
                String reportDate = df.format(date);

                Log.d(Commons.TAG, "reportDate = "+reportDate);
                if (what.equals("start")) {

                    tv_start_date_time.setText(reportDate);

                }else if (what.equals("end")) {

                    tv_end_date_time.setText(reportDate);
                }
            }

            @Override
            public void onNegativeButtonClick(Date date) {
                // Date is get on negative button click
            }
        });

        dateTimeDialogFragment.show(getChildFragmentManager(), "Select Date & Time");
    }

    private void hideCardViews(){


        edt_announcement.setText("");
        edt_event_name.setText("");
        edt_image_title.setText("");
        edt_link_title.setText("");

        tv_end_date_time.setText("");
        tv_start_date_time.setText("");
        edt_link.setText("");
        iv_image.setImageBitmap(null);
        p_image = null;


        card_view_announcement.setVisibility(View.GONE);
        card_view_event.setVisibility(View.GONE);
        card_view_image.setVisibility(View.GONE);
        card_view_link.setVisibility(View.GONE);
    }

    private void showHideCardView(int position){

        hideCardViews();

        switch (position){

            case 1:

                card_view_announcement.setVisibility(View.VISIBLE);

                break;

            case 2:

                card_view_event.setVisibility(View.VISIBLE);

                break;

            case 3:

                card_view_image.setVisibility(View.VISIBLE);

                break;

            case 4:

                card_view_link.setVisibility(View.VISIBLE);

                break;

            default:
                break;
        }

    }

    private void getMyDiary(){

        list_Diary = new ArrayList<>();

        progressDialog.show();

        String url = ApiClient.get_my_diary;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.user_id, prefManager.getId());


        Log.d(Constants.TAG , "get_my_diary - " + url);
        Log.d(Constants.TAG , "get_my_diary - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "get_my_diary- " + response.toString());

                if (response != null) {
                    try {

                        HashMap<String, String> hashMap = new HashMap<>();

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            JSONArray info = response.getJSONArray("info");

                            if (info.length() == 0){
                                Toasty.info(getActivity(),
                                        "No data found",
                                        Toast.LENGTH_SHORT, true).show();

                                hashMap.put("name", "No Diary Found");
                                list_Diary.add(hashMap);

                                adapter = new MySpinnerAdapter(getActivity(), list_Diary);
                                spinner_dairy.setAdapter(adapter);

                                progressDialog.dismiss();

                                return;
                            }


                            hashMap.put("name", "Select Diary");
                            list_Diary.add(hashMap);


                            for (int i = 0; i < info.length(); i++){
                                JSONObject object = info.getJSONObject(i);

                                hashMap = new HashMap<>();

                                hashMap.put("id", object.optString("id"));
                                hashMap.put("name", object.optString("diary_name"));

                                list_Diary.add(hashMap);

                            }

                            adapter = new MySpinnerAdapter(getActivity(), list_Diary);
                            spinner_dairy.setAdapter(adapter);

                        }else {

                            Toasty.info(getActivity(),
                                    "No data found",
                                    Toast.LENGTH_SHORT, true).show();

                            hashMap.put("name", "No Diary Found");
                            list_Diary.add(hashMap);

                            adapter = new MySpinnerAdapter(getActivity(), list_Diary);
                            spinner_dairy.setAdapter(adapter);
                        }


                        progressDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(Commons.TAG, "get_my_diary- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(getActivity()).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });



    }



    private void checkValidation(){

        if (diary_id == null){

            Toasty.info(getActivity(),
                    "Select diary",
                    Toast.LENGTH_SHORT, true).show();

            return;

        }

        if (msg_type == 0){

            Toasty.info(getActivity(),
                    "Select message type",
                    Toast.LENGTH_SHORT, true).show();

            return;

        }

        if (msg_type == 1){

            if (edt_announcement.getText().toString().trim().length() == 0){

                Toasty.info(getActivity(),
                        "Enter message",
                        Toast.LENGTH_SHORT, true).show();

                return;
            }


        }

        if (msg_type == 2){

            if (edt_event_name.getText().toString().trim().length() == 0){

                Toasty.info(getActivity(),
                        "Enter message",
                        Toast.LENGTH_SHORT, true).show();

                return;
            }


            if (tv_start_date_time.getText().toString().trim().length() == 0){

                Toasty.info(getActivity(),
                        "Select event start date time",
                        Toast.LENGTH_SHORT, true).show();

                return;
            }


            if (tv_end_date_time.getText().toString().trim().length() == 0){

                Toasty.info(getActivity(),
                        "Select event end date time",
                        Toast.LENGTH_SHORT, true).show();

                return;
            }

        }

        if (msg_type == 3){

            if (edt_image_title.getText().toString().trim().length() == 0){

                Toasty.info(getActivity(),
                        "Enter message",
                        Toast.LENGTH_SHORT, true).show();

                return;
            }

            if (p_image == null){

                Toasty.info(getActivity(),
                        "Select image",
                        Toast.LENGTH_SHORT, true).show();
            }

        }

        if (msg_type == 4){

            if (edt_link_title.getText().toString().trim().length() == 0){

                Toasty.info(getActivity(),
                        "Enter message",
                        Toast.LENGTH_SHORT, true).show();

                return;
            }


            if (edt_link.getText().toString().trim().length() == 0){

                Toasty.info(getActivity(),
                        "Enter a web link or youtube link",
                        Toast.LENGTH_SHORT, true).show();

                return;
            }

            if (!URLUtil.isValidUrl(edt_link.getText().toString().trim())){

                Toasty.info(getActivity(),
                        "Enter valid link",
                        Toast.LENGTH_SHORT, true).show();

                return;
            }


        }


        if (ConnectivityReceiver.isConnected()){
            postOnDairy();
        }else {
            Toasty.error(getActivity(),
                    "Please connect to internet",
                    Toast.LENGTH_SHORT, true).show();
        }


    }



    private void postOnDairy(){

        progressDialog.show();

        String url = ApiClient.diary_message;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.created_by, prefManager.getId());
        //params.put(ApiClient.created_by, prefManager.getInstitute_id());
        params.put(ApiClient.diary_id, diary_id);

        if (msg_type == 1){

            params.put(ApiClient.message_type, msg_type);
            params.put(ApiClient.message, edt_announcement.getText().toString());

        }

        if (msg_type == 2){

            params.put(ApiClient.message_type, msg_type);
            params.put(ApiClient.message, edt_event_name.getText().toString());
            params.put(ApiClient.event_start_date, tv_start_date_time.getText().toString());
            params.put(ApiClient.event_end_date, tv_end_date_time.getText().toString());

        }

        if (msg_type == 3){

            params.put(ApiClient.message_type, msg_type);
            params.put(ApiClient.message, edt_image_title.getText().toString());

            try {

                params.put(ApiClient.image, p_image);

            }catch (FileNotFoundException e){
                e.printStackTrace();
            }

        }

        if (msg_type == 4){

            if (Commons.isYoutubeLink(edt_link.getText().toString())){

                params.put(ApiClient.message_type, 5);
                params.put(ApiClient.message, edt_link_title.getText().toString());
                params.put(ApiClient.youtube_link, edt_link.getText().toString().trim());

            }else {

                params.put(ApiClient.message_type, msg_type);
                params.put(ApiClient.message, edt_link_title.getText().toString());
                params.put(ApiClient.link, edt_link.getText().toString().trim());

            }

        }

        Log.d(Constants.TAG , "diary_message - " + url);
        Log.d(Constants.TAG , "diary_message - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "diary_message- " + response.toString());

                if (response != null) {
                    try {

                        HashMap<String, String> hashMap = new HashMap<>();

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            Toasty.success(getActivity(),
                                    message,
                                    Toast.LENGTH_SHORT, true).show();


                            progressDialog.dismiss();

                            initViews();

                        }else {

                            Toasty.info(getActivity(),
                                    message,
                                    Toast.LENGTH_SHORT, true).show();


                            progressDialog.dismiss();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(Commons.TAG, "diary_message- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(getActivity()).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });



    }


    ///////////////////

    public void selectImage() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_picture_select, null);
        dialogBuilder.setView(dialogView);

        ImageView iv_gallery = (ImageView) dialogView.findViewById(R.id.iv_gallery);
        ImageView iv_camera = (ImageView) dialogView.findViewById(R.id.iv_camera);

        final AlertDialog alertDialog = dialogBuilder.create();

        iv_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityForResult(new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                        PICK_IMAGE_REQUEST);

                alertDialog.dismiss();

            }
        });


        iv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);

                alertDialog.dismiss();

            }
        });


        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            Uri uri = data.getData();
            //p_image = new File(getRealPathFromURI(uri));

            Bitmap bitmap = null;
           // Bitmap newBitmap = null;

            Log.d(Constants.TAG , "PICK_IMAGE_REQUEST - "+uri);
            Log.d(Constants.TAG , "p_image - "+p_image);



            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

                //newBitmap = Bitmap.createScaledBitmap(bitmap, 600, 300, false);

                iv_image.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }




            File f = new File(Environment.getExternalStorageDirectory().toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }

            try {

                String path = Environment.getExternalStorageDirectory()+File.separator;

                f.delete();
                OutputStream outFile = null;
                File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                try {

                    p_image = file;

                    outFile = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outFile);
                    outFile.flush();
                    outFile.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            //Bitmap newBitmap = null;


            Log.d(Constants.TAG , "PICK_IMAGE_REQUEST - "+data.getExtras().get("data"));

            File f = new File(Environment.getExternalStorageDirectory().toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }

            try {

               // newBitmap = Bitmap.createScaledBitmap(photo, 600, 300, false);

                iv_image.setImageBitmap(photo);

                String path = Environment.getExternalStorageDirectory()+File.separator;

                f.delete();
                OutputStream outFile = null;
                File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                try {

                    p_image = file;

                    outFile = new FileOutputStream(file);
                    photo.compress(Bitmap.CompressFormat.JPEG, 80, outFile);
                    outFile.flush();
                    outFile.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    private String getRealPathFromURI(Uri contentURI) {
        String result = "";
        try {
            Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentURI.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx); // Exception raised HERE
                cursor.close(); }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean checkPermission() {

        List<String> permissionsList = new ArrayList<String>();

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.CAMERA);
        }

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions((Activity) getActivity(), permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return false;
        } else {

            selectImage();

        }


        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                        (permissions.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                                grantResults[1] == PackageManager.PERMISSION_GRANTED)) {

                    //list is still empty

                    selectImage();

                } else {

                    checkPermission();
                    // Permission Denied

                }
                break;
        }
    }


}
