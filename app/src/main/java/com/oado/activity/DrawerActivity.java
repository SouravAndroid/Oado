package com.oado.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.javiersantos.appupdater.AppUpdater;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.barcode.FullScannerActivity;
import com.oado.database.DatabaseHelper;
import com.oado.fragments.Class_Frag;
import com.oado.fragments.Diary;
import com.oado.fragments.ExamResultCreate;
import com.oado.fragments.Fees;
import com.oado.fragments.Guardian;
import com.oado.fragments.Home;
import com.oado.fragments.InstitutesList;
import com.oado.fragments.Notifications;
import com.oado.fragments.OthersStaff;
import com.oado.fragments.PostOnDairy;
import com.oado.fragments.SMS_Report;
import com.oado.fragments.Section;
import com.oado.fragments.SelectInstituteForAdmin;
import com.oado.fragments.Student;
import com.oado.fragments.Subject;
import com.oado.fragments.Teacher;
import com.oado.fragments.TimeSlots;
import com.oado.models.ClassData;
import com.oado.models.SectionData;
import com.oado.models.SubjectData;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.Constants;
import com.oado.utils.GlobalClass;
import com.oado.utils.PrefManager;
import com.oado.utils.StaticText;
import com.oado.utils.UserPermissionCheck;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;


    TextView tv_name;
    TextView tv_email;
    CircleImageView profile_image;
    RelativeLayout relative_main;
    LinearLayout linear_logout;




    Fragment fragment = null;

    private PrefManager prefManager;
    private ProgressDialog progressDialog;
    private GlobalClass globalClass;
    private UserPermissionCheck userPermissionCheck;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        userPermissionCheck = new UserPermissionCheck(DrawerActivity.this);

        initViews();


        showHideNavigationItems();

        clearGlobalData();
    }

    private void clearGlobalData(){

        globalClass.getDiaryMessageArrayList().clear();
        globalClass.getListClass().clear();
        globalClass.getListSection().clear();
        globalClass.getListSubject().clear();

    }

    public void initViews(){

        AppUpdater appUpdater = new AppUpdater(this);
        appUpdater.start();

        prefManager = new PrefManager(this);
        globalClass = (GlobalClass) getApplicationContext();

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.activity_main_drawer);
        navigationView.setNavigationItemSelectedListener(this);


        requestPermission();


        setFirstFragment();

        setHeaderItem();

    }

    public void setHeaderItem(){

        /// for header view.........
        View header = navigationView.getHeaderView(0);
        relative_main = header.findViewById(R.id.relative_main);
        profile_image = header.findViewById(R.id.profile_image);
        tv_name = header.findViewById(R.id.tv_name);
        tv_email = header.findViewById(R.id.tv_email);
        linear_logout = header.findViewById(R.id.linear_logout);

        tv_name.setText(prefManager.getName() + " - (" + prefManager.getLoginType()+")");
        tv_email.setText(prefManager.getEmail());

        if (!prefManager.getImage().isEmpty()){

            Picasso.with(this).load(prefManager.getImage()).placeholder(R.mipmap.no_image)
                    .into(profile_image, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                    R.mipmap.no_image);
                            profile_image.setImageBitmap(icon);
                        }
                    });
        }else {
            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.no_image);
            profile_image.setImageBitmap(icon);
        }



        relative_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent2 = new Intent(DrawerActivity.this, ProfileView.class);
                startActivity(intent2);
                drawer.closeDrawer(GravityCompat.START);
            }
        });


        linear_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogLogout();
            }
        });

    }


    public void setFirstFragment(){

        if (userPermissionCheck.isAdmin()){

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragment = new SelectInstituteForAdmin();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
            toolbar.setTitle("Select Institute/School");


        }else {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragment = new Home();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
            toolbar.setTitle("Home");

        }


    }


    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
        }


        this.doubleBackToExitPressedOnce = true;
        Toasty.info(this,
                "Please click BACK again to exit",
                Toast.LENGTH_SHORT, true).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        getClassRelatedDetails();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        // Handle navigation view item clicks here.

        if (userPermissionCheck.isAdmin()){
            if (item.getItemId() == R.id.id_institutes){


            }else if (prefManager.getInstitute_id().isEmpty()){

                Toasty.error(getApplicationContext(),
                        "Select institute to show related information",
                        Toast.LENGTH_LONG, true).show();
                return false;
            }
        }



        fragment = null;
        Intent intent = null;

        switch (item.getItemId()){

            case R.id.id_institute_school_for_admin:

                fragment = new SelectInstituteForAdmin();

                break;

            case R.id.id_home:

                fragment = new Home();

                break;

            case R.id.id_create_post:

                fragment = new PostOnDairy();

                break;

            case R.id.id_dairy:

                fragment = new Diary();

                break;

            case R.id.id_institutes:

                fragment = new InstitutesList();

                break;

            case R.id.id_class:

                fragment = new Class_Frag();

                break;

            case R.id.id_section:

                fragment = new Section();

                break;

            case R.id.id_teacher:

                fragment = new Teacher();

                break;

            case R.id.id_student:

                fragment = new Student();

                break;

            case R.id.id_guardian:

                fragment = new Guardian();

                break;

            case R.id.id_other_staff:

                fragment = new OthersStaff();

                break;

            case R.id.id_subject:

                fragment = new Subject();

                break;

            case R.id.id_time_slots:

                fragment = new TimeSlots();

                break;

            case R.id.id_attendance_scan:

                intent = new Intent(DrawerActivity.this,
                        FullScannerActivity.class);
                setMyIntent(intent);

                break;

            case R.id.id_attendance_in:

                intent = new Intent(DrawerActivity.this,
                        InAttendanceByTeacher.class);
                setMyIntent(intent);

                break;

            case R.id.id_attendance_out:

                intent = new Intent(DrawerActivity.this,
                        OutAttendanceByTeacher.class);
                setMyIntent(intent);

                break;

            case R.id.id_attendance_report:

                intent = new Intent(DrawerActivity.this,
                        AttendanceReport.class);
                setMyIntent(intent);

                break;

            case R.id.id_create_exam_result:

                fragment = new ExamResultCreate();

                break;

            case R.id.id_exam_result:

                intent = new Intent(DrawerActivity.this,
                        ExamResult.class);
                setMyIntent(intent);

                break;

            case R.id.id_fees_create:

                fragment = new Fees();

                break;

            case R.id.id_fees_submit_report:

                intent = new Intent(DrawerActivity.this,
                        FeesSubmitAndReport.class);

                setMyIntent(intent);

                break;

            case R.id.id_messages:

                intent = new Intent(DrawerActivity.this,
                        MessagesScreen.class);

                setMyIntent(intent);

                break;

            case R.id.id_notifications:

                fragment = new Notifications();

                break;

            case R.id.id_sms_report:

                fragment = new SMS_Report();

                break;



                // more
            case R.id.facebook_page:

                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = getFacebookPageURL(this);
                facebookIntent.setData(Uri.parse(facebookUrl));
                startActivity(facebookIntent);

                //getFacebookPageURL(DrawerActivity.this);

                break;

            case R.id.about_us:

                intent = new Intent(DrawerActivity.this,
                        WebViewScreen.class);
                intent.putExtra("url", ApiClient.ABOUT_US_URL);
                setMyIntent(intent);

                break;

            case R.id.contact_us:

                intent = new Intent(DrawerActivity.this,
                        WebViewScreen.class);
                intent.putExtra("url", ApiClient.FAQ_URL);
                setMyIntent(intent);

                break;

            case R.id.faqs:

                intent = new Intent(DrawerActivity.this,
                        WebViewScreen.class);
                intent.putExtra("url", ApiClient.FAQ_URL);
                setMyIntent(intent);

                break;

            case R.id.privacy_policy:

                intent = new Intent(DrawerActivity.this,
                        WebViewScreen.class);
                intent.putExtra("url", ApiClient.PRIVACY_POLICY_URL);
                setMyIntent(intent);

                break;



        }


        drawer.closeDrawer(GravityCompat.START);
        item.setChecked(true);



        Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {

                if (fragment != null) {

                    try {

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    toolbar.setTitle(item.getTitle());

                }

            }
        };
        handler.postDelayed(runnable, 500);

        return true;
    }

    private void setMyIntent(final Intent intent){

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {

                if (intent != null) {

                    startActivity(intent);

                }

            }
        };
        handler.postDelayed(runnable, 500);
    }





    private void dialogLogout(){

        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.alert)
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        prefManager.logOut();

                        DatabaseHelper databaseHelper = new DatabaseHelper(DrawerActivity.this);
                        databaseHelper.deleteTableData();

                        Intent intent = new Intent(DrawerActivity.this, Login.class);
                        startActivity(intent);
                        finish();

                    }

                })
                .setNegativeButton("No", null)
                .show();

    }


    private void getClassRelatedDetails(){

        progressDialog.show();

        String url = ApiClient.get_class_details;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.institute_id, prefManager.getInstitute_id());


        Log.d(Constants.TAG , "get_class_details - " + url);
        Log.d(Constants.TAG , "get_class_details - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "get_class_details- " + response.toString());

                if (response != null) {
                    try {

                        ClassData classData;
                        ArrayList<ClassData> classDataArrayList = new ArrayList<>();

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            globalClass.getListClass().clear();

                            JSONArray info = response.getJSONArray("info");

                            for (int i = 0; i < info.length(); i++){
                                JSONObject object = info.getJSONObject(i);

                                classData = new ClassData();

                                classData.setId(object.optString("class_id"));
                                classData.setName(object.optString("class_name"));
                                classData.setInstitute_id(object.optString("institute_id"));

                                JSONArray array_section = object.getJSONArray("section");

                                ArrayList<SectionData> list_section = new ArrayList<>();
                                for (int j = 0; j < array_section.length(); j++){
                                    JSONObject object1 = array_section.getJSONObject(j);

                                    SectionData sectionData = new SectionData();

                                    sectionData.setId(object1.optString("section_id"));
                                    sectionData.setName(object1.optString("section_name"));

                                    JSONArray array_subject = object1.getJSONArray("subject");

                                    ArrayList<SubjectData> list_subject = new ArrayList<>();
                                    for (int k = 0; k < array_subject.length(); k++){
                                        JSONObject object2 = array_subject.getJSONObject(k);

                                        SubjectData subjectData = new SubjectData();

                                        subjectData.setId(object2.optString("subject_id"));
                                        subjectData.setSubject_name(object2.optString("subject_name"));

                                        list_subject.add(subjectData);

                                    }
                                    sectionData.setList_Subject(list_subject);

                                    list_section.add(sectionData);
                                }

                                classData.setList_Section(list_section);

                                classDataArrayList.add(classData);

                            }

                            globalClass.setListClass(classDataArrayList);

                        }else {

                            /*Toasty.info(getApplicationContext(),
                                    "No data found",
                                    Toast.LENGTH_SHORT, true).show();*/

                        }

                        getAllSubject();

                       progressDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(Commons.TAG, "get_class_details- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(DrawerActivity.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });



    }

    private void getAllSubject(){

        // progressDialog.show();

        String url = ApiClient.get_all_subjects;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.institute_id, prefManager.getInstitute_id());


        Log.d(Constants.TAG , "get_all_subjects - " + url);
        Log.d(Constants.TAG , "get_all_subjects - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "get_all_subjects- " + response.toString());

                if (response != null) {
                    try {

                        SubjectData subjectData;
                        ArrayList<SubjectData> subjectDataArrayList = new ArrayList<>();

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            JSONArray info = response.getJSONArray("info");

                            if (info.length() == 0){
                                /*Toasty.info(getApplicationContext(),
                                        "No data found",
                                        Toast.LENGTH_SHORT, true).show();*/

                              //  progressDialog.dismiss();

                            }


                            for (int i = 0; i < info.length(); i++){
                                JSONObject object = info.getJSONObject(i);

                                subjectData = new SubjectData();

                                subjectData.setId(object.optString("id"));
                                subjectData.setSubject_name(object.optString("subject_name"));

                                subjectDataArrayList.add(subjectData);

                            }

                            globalClass.setListSubject(subjectDataArrayList);

                        }else {

                            /*Toasty.info(getApplicationContext(),
                                    "No data found",
                                    Toast.LENGTH_SHORT, true).show();*/

                        }

                        getAllSection();

                      // progressDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(Commons.TAG, "get_all_subjects- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(DrawerActivity.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });

    }

    private void getAllSection(){

        // progressDialog.show();

        String url = ApiClient.get_all_sections;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.institute_id, prefManager.getInstitute_id());


        Log.d(Constants.TAG , "get_all_sections - " + url);
        Log.d(Constants.TAG , "get_all_sections - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "get_all_sections- " + response.toString());

                if (response != null) {
                    try {

                        SectionData sectionData;
                        ArrayList<SectionData> sectionDataArrayList = new ArrayList<>();

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            JSONArray info = response.getJSONArray("info");

                            if (info.length() == 0){
                                /*Toasty.info(getApplicationContext(),
                                        "No data found",
                                        Toast.LENGTH_SHORT, true).show();*/

                                progressDialog.dismiss();

                                return;
                            }


                            for (int i = 0; i < info.length(); i++){
                                JSONObject object = info.getJSONObject(i);

                                sectionData = new SectionData();

                                sectionData.setId(object.optString("id"));
                                sectionData.setName(object.optString("section_name"));

                                sectionDataArrayList.add(sectionData);

                            }

                            globalClass.setListSection(sectionDataArrayList);

                        }else {

                            /*Toasty.info(getApplicationContext(),
                                    "No data found",
                                    Toast.LENGTH_SHORT, true).show();*/

                        }


                        progressDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(Commons.TAG, "get_all_sections- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(DrawerActivity.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });


        updateFCM();

    }


    private void updateFCM(){

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        String url = ApiClient.fcm_token_update;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put(ApiClient.user_id, prefManager.getId());
        params.put(ApiClient.fcm_reg_token, refreshedToken);


        Log.d(Constants.TAG , "fcm_token_update - " + url);
        Log.d(Constants.TAG , "fcm_token_update - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "fcm_token_update- " + response.toString());

                if (response != null) {
                    try {


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(Commons.TAG, "fcm_token_update- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(DrawerActivity.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });

    }



    /////////////////////////////////////////


    public static String FACEBOOK_URL = "https://www.facebook.com/oado.edu";
    public static String FACEBOOK_PAGE_ID = "oado.edu";
    public String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }

    /////////////////////


    @Override
    protected void onStart() {
        super.onStart();
    }

    private static final int PERMISSION_REQUEST_CODE = 1;
    private void requestPermission(){

        ActivityCompat.requestPermissions((Activity)DrawerActivity.this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(StaticText.TAG, "PERMISSION_GRANTED >>>>");


                } else {
                    //code for deny

                    requestPermission();
                }
                break;
        }
    }


    /////////////////////////
    // show hide navigation item .... 22 nav item

    private void showHideNavigationItems(){
        navigationView = findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();

        if (userPermissionCheck.isAdmin()){


        }else if (userPermissionCheck.isInstitute()){

            nav_Menu.findItem(R.id.id_institute_school_for_admin).setVisible(false);

        }else if (userPermissionCheck.isTeacher()){

            nav_Menu.findItem(R.id.id_institute_school_for_admin).setVisible(false);
            nav_Menu.findItem(R.id.id_other_staff).setVisible(false);
            nav_Menu.findItem(R.id.id_attendance_scan).setVisible(false);
            nav_Menu.findItem(R.id.id_fees_create).setVisible(false);
            nav_Menu.findItem(R.id.id_fees_submit_report).setVisible(false);
            nav_Menu.findItem(R.id.id_sms_report).setVisible(false);


        }else if (userPermissionCheck.isStaff()){

            nav_Menu.findItem(R.id.id_institute_school_for_admin).setVisible(false);
            nav_Menu.findItem(R.id.id_create_post).setVisible(false);
            nav_Menu.findItem(R.id.id_dairy).setVisible(false);
            nav_Menu.findItem(R.id.id_class).setVisible(false);
            nav_Menu.findItem(R.id.id_section).setVisible(false);
            nav_Menu.findItem(R.id.id_teacher).setVisible(false);
            nav_Menu.findItem(R.id.id_time_slots).setVisible(false);
            nav_Menu.findItem(R.id.id_attendance_in).setVisible(false);
            nav_Menu.findItem(R.id.id_attendance_out).setVisible(false);
            nav_Menu.findItem(R.id.id_attendance_report).setVisible(false);
            nav_Menu.findItem(R.id.id_create_exam_result).setVisible(false);
            nav_Menu.findItem(R.id.id_exam_result).setVisible(false);
            nav_Menu.findItem(R.id.id_fees_create).setVisible(false);
            nav_Menu.findItem(R.id.id_fees_submit_report).setVisible(false);
            nav_Menu.findItem(R.id.id_sms_report).setVisible(false);

        }else if (userPermissionCheck.isStudent()){

            nav_Menu.findItem(R.id.id_institute_school_for_admin).setVisible(false);
            nav_Menu.findItem(R.id.id_create_post).setVisible(false);
            nav_Menu.findItem(R.id.id_dairy).setVisible(false);
            nav_Menu.findItem(R.id.id_subject).setVisible(false);
            nav_Menu.findItem(R.id.id_teacher).setVisible(false);
            nav_Menu.findItem(R.id.id_other_staff).setVisible(false);
            nav_Menu.findItem(R.id.id_attendance_scan).setVisible(false);
            nav_Menu.findItem(R.id.id_attendance_in).setVisible(false);
            nav_Menu.findItem(R.id.id_attendance_out).setVisible(false);
            nav_Menu.findItem(R.id.id_create_exam_result).setVisible(false);
            nav_Menu.findItem(R.id.id_fees_create).setVisible(false);
            nav_Menu.findItem(R.id.id_sms_report).setVisible(false);

        }else if (userPermissionCheck.isGuardian()){

            nav_Menu.findItem(R.id.id_institute_school_for_admin).setVisible(false);
            nav_Menu.findItem(R.id.id_create_post).setVisible(false);
            nav_Menu.findItem(R.id.id_dairy).setVisible(false);
            nav_Menu.findItem(R.id.id_subject).setVisible(false);
            nav_Menu.findItem(R.id.id_teacher).setVisible(false);
            nav_Menu.findItem(R.id.id_other_staff).setVisible(false);
            nav_Menu.findItem(R.id.id_attendance_scan).setVisible(false);
            nav_Menu.findItem(R.id.id_attendance_in).setVisible(false);
            nav_Menu.findItem(R.id.id_attendance_out).setVisible(false);
            nav_Menu.findItem(R.id.id_create_exam_result).setVisible(false);
            nav_Menu.findItem(R.id.id_fees_create).setVisible(false);
            nav_Menu.findItem(R.id.id_fees_submit_report).setVisible(false);
            nav_Menu.findItem(R.id.id_sms_report).setVisible(false);


        }


    }



}
