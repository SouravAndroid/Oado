package com.oado.activity;

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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class ComposeMessage extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tv_send_to) TextView tv_send_to;
    @BindView(R.id.spinner_category) Spinner spinner_category;

    @BindView(R.id.card_view_announcement) CardView card_view_announcement;
    @BindView(R.id.card_view_image) CardView card_view_image;
    @BindView(R.id.card_view_link) CardView card_view_link;
    @BindView(R.id.edt_announcement) EditText edt_announcement;

    @BindView(R.id.edt_image_title) EditText edt_image_title;

    @BindView(R.id.btn_select_image) Button btn_select_image;
    @BindView(R.id.iv_image) ImageView iv_image;

    @BindView(R.id.edt_link_title) EditText edt_link_title;
    @BindView(R.id.edt_link) EditText edt_link;


    @BindView(R.id.rel_send_message)
    RelativeLayout rel_send_message;


    int msg_type;
    String message, to_user_name, to_user_id, to_user_type;

    File p_image;
    private int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 1888;
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1;

    ProgressDialog progressDialog;
    PrefManager prefManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_compose);
        ButterKnife.bind(this);

        initViews();

    }


    public void initViews(){

        prefManager = new PrefManager(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);


        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        rel_send_message.setOnClickListener(this);

        setItems();

        btn_select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage();
            }
        });


        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            to_user_id = bundle.getString("id");
            to_user_name = bundle.getString("name");
            to_user_type = bundle.getString("type");


            tv_send_to.setText("To: "+to_user_name
                    + " ("+to_user_type.toUpperCase()+")");

        }



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
        hashMap.put("name", "Photo");
        arrayList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "Youtube Link / Others Link");
        arrayList.add(hashMap);

        MySpinnerAdapter adapter = new MySpinnerAdapter(ComposeMessage.this, arrayList);
        spinner_category.setAdapter(adapter);


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                finish();
                break;

            default:
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.rel_send_message:

                checkValidation();

                break;

            default:
                break;


        }

    }

    private void hideCardViews(){

        edt_announcement.setText("");
        edt_image_title.setText("");
        edt_link_title.setText("");

        edt_link.setText("");
        iv_image.setImageBitmap(null);
        p_image = null;

        card_view_announcement.setVisibility(View.GONE);
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

                card_view_image.setVisibility(View.VISIBLE);

                break;

            case 3:

                card_view_link.setVisibility(View.VISIBLE);

                break;

            default:
                break;
        }

    }


    private void checkValidation(){

        if (msg_type == 0){

            Toasty.info(getApplicationContext(),
                    "Select message type",
                    Toast.LENGTH_SHORT, true).show();

            return;

        }

        if (msg_type == 1){

            if (edt_announcement.getText().toString().trim().length() == 0){

                Toasty.info(getApplicationContext(),
                        "Enter message",
                        Toast.LENGTH_SHORT, true).show();

                return;
            }


        }

        if (msg_type == 2){

            if (edt_image_title.getText().toString().trim().length() == 0){

                Toasty.info(getApplicationContext(),
                        "Enter message",
                        Toast.LENGTH_SHORT, true).show();

                return;
            }

            if (p_image == null){

                Toasty.info(getApplicationContext(),
                        "Select image",
                        Toast.LENGTH_SHORT, true).show();
            }

        }

        if (msg_type == 3){

            if (edt_link_title.getText().toString().trim().length() == 0){

                Toasty.info(getApplicationContext(),
                        "Enter message",
                        Toast.LENGTH_SHORT, true).show();

                return;
            }


            if (edt_link.getText().toString().trim().length() == 0){

                Toasty.info(getApplicationContext(),
                        "Enter a web link or youtube link",
                        Toast.LENGTH_SHORT, true).show();

                return;
            }

            if (!URLUtil.isValidUrl(edt_link.getText().toString().trim())){

                Toasty.info(getApplicationContext(),
                        "Enter valid link",
                        Toast.LENGTH_SHORT, true).show();

                return;
            }


        }


        if (ConnectivityReceiver.isConnected()){
            sendMessage();
        }else {
            Toasty.error(getApplicationContext(),
                    "Please connect to internet",
                    Toast.LENGTH_SHORT, true).show();
        }



    }



    private void sendMessage(){

        progressDialog.show();

        String url = ApiClient.send_message;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.institute_id, prefManager.getInstitute_id());
        params.put(ApiClient.from_id, prefManager.getId());
        params.put(ApiClient.to_id, to_user_id);



        if (msg_type == 1){

            params.put(ApiClient.message_type, msg_type);
            params.put(ApiClient.message, edt_announcement.getText().toString());

        }

        if (msg_type == 2){

            params.put(ApiClient.message_type, msg_type);
            params.put(ApiClient.message, edt_image_title.getText().toString());

            try {

                params.put(ApiClient.image, p_image);

            }catch (FileNotFoundException e){
                e.printStackTrace();
            }

        }

        if (msg_type == 3){

            if (Commons.isYoutubeLink(edt_link.getText().toString())){

                params.put(ApiClient.message_type, 4);
                params.put(ApiClient.message, edt_link_title.getText().toString());
                params.put(ApiClient.youtube_link, edt_link.getText().toString().trim());

            }else {

                params.put(ApiClient.message_type, msg_type);
                params.put(ApiClient.message, edt_link_title.getText().toString());
                params.put(ApiClient.link, edt_link.getText().toString().trim());

            }

        }

        Log.d(Constants.TAG , "send_message - " + url);
        Log.d(Constants.TAG , "send_message - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "send_message- " + response.toString());

                if (response != null) {
                    try {

                        HashMap<String, String> hashMap = new HashMap<>();

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            Toasty.success(getApplicationContext(),
                                    message,
                                    Toast.LENGTH_SHORT, true).show();


                            progressDialog.dismiss();

                            initViews();

                        }else {

                            Toasty.info(getApplicationContext(),
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
                Log.d(Commons.TAG, "send_message- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(getApplicationContext()).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });



    }


    /////////////////////////

    public void selectImage() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ComposeMessage.this);
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
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
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
            Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
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

        if (ContextCompat.checkSelfPermission(ComposeMessage.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(ComposeMessage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(ComposeMessage.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.CAMERA);
        }

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions((Activity) ComposeMessage.this, permissionsList.toArray(new String[permissionsList.size()]),
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
