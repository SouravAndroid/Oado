package com.oado.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.oado.R;
import com.oado.adapters.MySpinnerAdapter;
import com.oado.adapters.SubjectSpinnerAdapter;
import com.oado.models.SectionData;
import com.oado.models.StudentData;
import com.oado.models.SubjectData;
import com.oado.utils.ApiClient;
import com.oado.utils.Commons;
import com.oado.utils.Constants;
import com.oado.utils.GlobalClass;
import com.oado.utils.PrefManager;
import com.oado.utils.UserPermissionCheck;
import com.oado.utils.ValidationClass;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class StudentAdd extends AppCompatActivity implements View.OnClickListener {

    // student
    @BindView(R.id.item_image) CircleImageView item_image;
    @BindView(R.id.student_name) EditText student_name;
    @BindView(R.id.roll_no) EditText roll_no;
    @BindView(R.id.phone_no) EditText phone_no;
    @BindView(R.id.email) EditText email;
    @BindView(R.id.street_name) EditText street_name;
    @BindView(R.id.city_name) EditText city_name;
    @BindView(R.id.state_name) EditText state_name;
    @BindView(R.id.country_name) EditText country_name;
    @BindView(R.id.pincode) EditText pincode;
    @BindView(R.id.barcode) EditText barcode;
    @BindView(R.id.edt_dob) EditText edt_dob;
    @BindView(R.id.spinner_class) Spinner spinner_class;
    @BindView(R.id.spinner_section) Spinner spinner_section;
    @BindView(R.id.spinner_subject) Spinner spinner_subject;
    @BindView(R.id.iv_calender) ImageView iv_calender;
    @BindView(R.id.radioGroup) RadioGroup radioGroup;
    @BindView(R.id.radio_male) RadioButton radio_male;
    @BindView(R.id.radio_female) RadioButton radio_female;
    @BindView(R.id.rel_create_student) RelativeLayout rel_create_student;
    @BindView(R.id.tv_gender_title) TextView tv_gender_title;
    @BindView(R.id.tv_button_title) TextView tv_button_title;
    @BindView(R.id.tv_selected_subject) TextView tv_selected_subject;
    @BindView(R.id.linear_spinner) LinearLayout linear_spinner;
    @BindView(R.id.linear_textviews) LinearLayout linear_textviews;

    @BindView(R.id.class_name) EditText class_name;
    @BindView(R.id.section_name) EditText section_name;
    @BindView(R.id.subject_name) EditText subject_name;

    // guardian
    @BindView(R.id.item_image_guardian) CircleImageView item_image_guardian;
    @BindView(R.id.guardian_name) EditText guardian_name;
    @BindView(R.id.spinner_relation_guardian) Spinner spinner_relation_guardian;
    @BindView(R.id.phone_no_guardian) EditText phone_no_guardian;
    @BindView(R.id.email_guardian) EditText email_guardian;
    @BindView(R.id.cb_same_as_student_address) CheckBox cb_same_as_student_address;
    @BindView(R.id.street_name_guardian) EditText street_name_guardian;
    @BindView(R.id.city_name_guardian) EditText city_name_guardian;
    @BindView(R.id.state_name_guardian) EditText state_name_guardian;
    @BindView(R.id.country_name_guardian) EditText country_name_guardian;
    @BindView(R.id.pincode_guardian) EditText pincode_guardian;
    @BindView(R.id.card_view_guardian) CardView card_view_guardian;
    @BindView(R.id.cb_sms_subscription) CheckBox cb_sms_subscription;


    private int PICK_IMAGE_REQUEST_STUDENT = 11;
    private int PICK_IMAGE_REQUEST_GUARDIAN = 22;
    private static final int CAMERA_REQUEST_STUDENT = 33;
    private static final int CAMERA_REQUEST_GUARDIAN = 44;
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 55;
    File p_image_student = null;
    File p_image_guardian = null;
    ValidationClass validationClass;
    ProgressDialog progressDialog;
    PrefManager prefManager;
    GlobalClass globalClass;
    StudentData studentData;
    UserPermissionCheck userPermissionCheck;

    String type, gender, class_id, section_id, guardian_relation;
    ArrayList<HashMap<String, String>> list_Class;
    ArrayList<HashMap<String, String>> list_Section;
    ArrayList<HashMap<String, String>> list_Subject;
    ArrayList<HashMap<String, String>> list_Relation;
    ArrayList<SectionData> sectionDataArrayList;
    ArrayList<SubjectData> subjectDataArrayList;

    MySpinnerAdapter spinnerAdapter;
    SubjectSpinnerAdapter subjectSpinnerAdapter;
    String click_on_image, sms_subsription;


    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_add);
        ButterKnife.bind(this);
        initViews();



    }

    public void initViews(){

        validationClass = new ValidationClass(this);
        prefManager = new PrefManager(this);
        globalClass = (GlobalClass) getApplicationContext();
        userPermissionCheck = new UserPermissionCheck(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);



        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }



        item_image.setOnClickListener(this);
        item_image_guardian.setOnClickListener(this);
        rel_create_student.setOnClickListener(this);

        type = "add";

        setActionOnViews();

        setGuardianRelations();


        if (type.matches("add")){
            setClassData();
            setSectionData("");

        }else if (type.matches("edit")){
            setClassData();
        }

        clickOnViews();

    }

    public void clickOnViews(){

        spinner_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String item = (String) parent.getItemAtPosition(position);
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.app_blue));

                if (position > 0){
                    class_id = list_Class.get(position).get("id");

                    setSectionData(class_id);

                }else {

                    class_id = null;
                    section_id = null;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_section.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String item = (String) parent.getItemAtPosition(position);
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.app_blue));

                if (position > 0){
                    section_id = list_Section.get(position).get("id");

                    setSubjectData(section_id);

                }else {

                    section_id = null;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinner_relation_guardian.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String item = (String) parent.getItemAtPosition(position);
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.app_blue));

                if (position > 0){
                    guardian_relation = list_Relation.get(position).get("name");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        iv_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(StudentAdd.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });


        cb_same_as_student_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){

                    street_name_guardian.setText(street_name.getText().toString());
                    city_name_guardian.setText(city_name.getText().toString());
                    state_name_guardian.setText(state_name.getText().toString());
                    country_name_guardian.setText(country_name.getText().toString());
                    pincode_guardian.setText(pincode.getText().toString());

                }else {

                    street_name_guardian.setText("");
                    city_name_guardian.setText("");
                    state_name_guardian.setText("");
                    country_name_guardian.setText("");
                    pincode_guardian.setText("");
                }


            }
        });

    }

    private void setActionOnViews(){
        type = "add";
        tv_button_title.setText(getResources().getString(R.string.add_student));
        getSupportActionBar().setTitle(getResources().getString(R.string.add_student));
        phone_no.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });
        linear_textviews.setVisibility(View.GONE);
        barcode.setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            type = bundle.getString("type");

            if (type.matches("edit")){

                tv_button_title.setText(getResources().getString(R.string.edit_student));

                getSupportActionBar().setTitle(getResources().getString(R.string.edit_student));

                card_view_guardian.setVisibility(View.GONE);

                studentData = (StudentData) bundle.getSerializable("user_data");

                Picasso.with(this).load(studentData.getImage()).placeholder(R.mipmap.no_image)
                        .into(item_image, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                        R.mipmap.no_image);

                                item_image.setImageBitmap(icon);
                            }
                        });


                student_name.setText(studentData.getName());
                student_name.setSelection(student_name.length());

                roll_no.setText(studentData.getRoll_no());
                roll_no.setSelection(roll_no.length());

                phone_no.setText(studentData.getMobile());
                phone_no.setSelection(phone_no.length());

                email.setText(studentData.getEmail());
                email.setSelection(email.length());

                street_name.setText(studentData.getStreet());
                street_name.setSelection(street_name.length());

                city_name.setText(studentData.getCity());
                city_name.setSelection(city_name.length());

                state_name.setText(studentData.getState());
                state_name.setSelection(state_name.length());

                country_name.setText(studentData.getCountry());
                country_name.setSelection(country_name.length());

                pincode.setText(studentData.getPincode());
                pincode.setSelection(pincode.length());

                edt_dob.setText(studentData.getDob());


                if (studentData.getGender().toLowerCase()
                        .matches(radio_male.getText().toString().toLowerCase())){

                    radio_male.setChecked(true);

                }else if (studentData.getGender().toLowerCase()
                        .matches(radio_female.getText().toString().toLowerCase())){

                    radio_female.setChecked(true);
                }


                class_id = studentData.getClass_id();
                section_id = studentData.getSection_id();


                if (userPermissionCheck.isAdmin()){
                    barcode.setVisibility(View.VISIBLE);
                    barcode.setText("Barcode: "+studentData.getBarcode());
                    barcode.setEnabled(false);
                }else {
                    barcode.setVisibility(View.GONE);
                    barcode.setText("Barcode: "+studentData.getBarcode());
                    barcode.setEnabled(false);
                }




            }else if (type.matches("view")){

                tv_button_title.setText(getResources().getString(R.string.view_student));
                rel_create_student.setVisibility(View.GONE);
                getSupportActionBar().setTitle(getResources().getString(R.string.view_student));
                linear_spinner.setVisibility(View.GONE);
                linear_textviews.setVisibility(View.VISIBLE);

                card_view_guardian.setVisibility(View.GONE);


                phone_no.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });

                studentData = (StudentData) bundle.getSerializable("user_data");

                Picasso.with(this).load(studentData.getImage()).placeholder(R.mipmap.no_image)
                        .into(item_image, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                        R.mipmap.no_image);

                                item_image.setImageBitmap(icon);
                            }
                        });

                item_image.setOnClickListener(null);

                class_name.setText("Class: "+studentData.getClass_name());
                class_name.setEnabled(false);

                section_name.setText("Section: "+studentData.getSection_name());
                section_name.setEnabled(false);

                String ss = "";
                for (int i = 0; i < studentData.getList_subject().size(); i++){
                    ss = ss + studentData.getList_subject().get(i) + ",";
                }
                if (ss.endsWith(",")){
                    subject_name.setText("Subject: "+Commons.removeLastChar(ss));
                    subject_name.setEnabled(false);
                }



                student_name.setText("Name: "+studentData.getName());
                student_name.setEnabled(false);

                roll_no.setText("Roll No: "+studentData.getRoll_no());
                roll_no.setEnabled(false);

                phone_no.setText("Mobile: "+studentData.getMobile());
                phone_no.setEnabled(false);

                email.setText("Email: "+studentData.getEmail());
                email.setEnabled(false);

                street_name.setText("Street: "+studentData.getStreet());
                street_name.setEnabled(false);

                city_name.setText("City: "+studentData.getCity());
                city_name.setEnabled(false);

                state_name.setText("State: "+studentData.getState());
                state_name.setEnabled(false);

                country_name.setText("Country: "+studentData.getCountry());
                country_name.setEnabled(false);

                pincode.setText("Pincode: "+studentData.getPincode());
                pincode.setEnabled(false);


                if (userPermissionCheck.isAdmin()){
                    barcode.setVisibility(View.VISIBLE);
                    barcode.setText("Barcode: "+studentData.getBarcode());
                    barcode.setEnabled(false);
                }else {
                    barcode.setVisibility(View.GONE);
                    barcode.setText("Barcode: "+studentData.getBarcode());
                    barcode.setEnabled(false);
                }




                edt_dob.setText("DOB: "+studentData.getDob());
                iv_calender.setVisibility(View.INVISIBLE);


                radioGroup.setVisibility(View.INVISIBLE);
                if (studentData.getGender().toLowerCase()
                        .matches(radio_male.getText().toString().toLowerCase())){

                    radio_male.setChecked(true);

                    tv_gender_title.setText("Gender: Male");

                }else if (studentData.getGender().toLowerCase()
                        .matches(radio_female.getText().toString().toLowerCase())){

                    radio_female.setChecked(true);

                    tv_gender_title.setText("Gender: Female");
                }



            }else if (type.matches("add")) {

                tv_button_title.setText(getResources().getString(R.string.add_student));
                getSupportActionBar().setTitle(getResources().getString(R.string.add_student));
                barcode.setVisibility(View.GONE);

            }



        }


    }


    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate();
        }

    };


    private void updateDate() {
        //String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(Commons.dateFormat, Locale.US);

        edt_dob.setText(sdf.format(myCalendar.getTime()));
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

            case R.id.item_image:

                click_on_image = "student";

                selectImage();

                break;

            case R.id.item_image_guardian:

                click_on_image = "guardian";

                selectImage();

                break;

            case R.id.rel_create_student:

                checkValidation();

                break;

        }

    }

    private void setGuardianRelations(){


        list_Relation = new ArrayList<>();
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("name", "Select Relation");
        list_Relation.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "Parents");
        list_Relation.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "Uncle/Aunty");
        list_Relation.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "Grand Father/Mother");
        list_Relation.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "Others");
        list_Relation.add(hashMap);

        MySpinnerAdapter adapter = new MySpinnerAdapter(this, list_Relation);
        spinner_relation_guardian.setAdapter(adapter);



    }

    private void setClassData(){
        // class set
        list_Class = new ArrayList<>();

        HashMap<String, String> hashMap = new HashMap<>();
        int set_position = 0;

        if (globalClass.getListClass().size() == 0){

            hashMap.put("name", "No Class here");
            list_Class.add(hashMap);


            spinnerAdapter = new MySpinnerAdapter(StudentAdd.this,
                    list_Class);
            spinner_class.setAdapter(spinnerAdapter);

        }else {

            hashMap.put("name", "Select Class");
            list_Class.add(hashMap);

            for (int i = 0; i < globalClass.getListClass().size(); i++){

                hashMap = new HashMap<>();

                hashMap.put("id", globalClass.getListClass().get(i).getId());
                hashMap.put("name", globalClass.getListClass().get(i).getName());


                if (type.matches("edit") &&
                        studentData.getClass_id()
                                .matches(globalClass.getListClass().get(i).getId())){
                    set_position = i+1;
                }

                list_Class.add(hashMap);
            }

            spinnerAdapter = new MySpinnerAdapter(StudentAdd.this,
                    list_Class);
            spinner_class.setAdapter(spinnerAdapter);

            if (type.matches("edit")){
                spinner_class.setSelection(set_position);
            }


        }

    }

    private void setSectionData(String class_id_){
        list_Section = new ArrayList<>();
        HashMap<String, String> hashMap = new HashMap<>();
        int set_position = 0;
        sectionDataArrayList = new ArrayList<>();

        for (int i = 0; i < globalClass.getListClass().size(); i++){

            if (class_id_.equals(globalClass.getListClass().get(i).getId())){

                sectionDataArrayList = globalClass.getListClass().get(i).getList_Section();

            }

        }

        if (sectionDataArrayList.size() == 0){

            hashMap.put("name", "No Section here");
            list_Section.add(hashMap);


            spinnerAdapter = new MySpinnerAdapter(StudentAdd.this,
                    list_Section);
            spinner_section.setAdapter(spinnerAdapter);

        }else {
            hashMap.put("name", "Select Section");
            list_Section.add(hashMap);

            for (int i = 0; i < sectionDataArrayList.size(); i++){

                hashMap = new HashMap<>();

                hashMap.put("id", sectionDataArrayList.get(i).getId());
                hashMap.put("name", sectionDataArrayList.get(i).getName());


                if (type.matches("edit") &&
                        studentData.getSection_id()
                                .matches(sectionDataArrayList.get(i).getId())){
                    set_position = i+1;
                }

                list_Section.add(hashMap);

            }


            spinnerAdapter = new MySpinnerAdapter(StudentAdd.this,
                    list_Section);
            spinner_section.setAdapter(spinnerAdapter);


            if (type.matches("edit")){
                spinner_section.setSelection(set_position);
            }

        }

    }

    private void setSubjectData(String section_id_){
        list_Subject = new ArrayList<>();
        HashMap<String, String> hashMap = new HashMap<>();
        subjectDataArrayList = new ArrayList<>();

        for (int i = 0; i < sectionDataArrayList.size(); i++){

            if (section_id_.equals(sectionDataArrayList.get(i).getId())){

                subjectDataArrayList = sectionDataArrayList.get(i).getList_Subject();

            }

        }

        if (subjectDataArrayList.size() == 0){

            hashMap.put("name", "No Subject here");
            list_Subject.add(hashMap);

            subjectSpinnerAdapter = new SubjectSpinnerAdapter(StudentAdd.this,
                    list_Subject, tv_selected_subject);
            spinner_subject.setAdapter(spinnerAdapter);

        }else {

            hashMap.put("name", "Select Subject");
            list_Subject.add(hashMap);

            for (int i = 0; i < subjectDataArrayList.size(); i++){

                hashMap = new HashMap<>();

                hashMap.put("id", subjectDataArrayList.get(i).getId());
                hashMap.put("name", subjectDataArrayList.get(i).getSubject_name());

                list_Subject.add(hashMap);

            }

            if (type.matches("edit")){

                subjectSpinnerAdapter = new SubjectSpinnerAdapter(StudentAdd.this,
                        list_Subject, studentData.getList_subject(),
                        tv_selected_subject);
                spinner_subject.setAdapter(subjectSpinnerAdapter);

            }else {

                subjectSpinnerAdapter = new SubjectSpinnerAdapter(StudentAdd.this,
                        list_Subject, tv_selected_subject);
                spinner_subject.setAdapter(subjectSpinnerAdapter);

            }


        }

    }



    public void selectImage() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(StudentAdd.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_picture_select, null);
        dialogBuilder.setView(dialogView);

        ImageView iv_gallery = (ImageView) dialogView.findViewById(R.id.iv_gallery);
        ImageView iv_camera = (ImageView) dialogView.findViewById(R.id.iv_camera);

        final AlertDialog alertDialog = dialogBuilder.create();

        iv_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (click_on_image.equals("student")){

                    startActivityForResult(new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                            PICK_IMAGE_REQUEST_STUDENT);

                }else {

                    startActivityForResult(new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                            PICK_IMAGE_REQUEST_GUARDIAN);

                }

                alertDialog.dismiss();

            }
        });


        iv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (click_on_image.equals("student")){

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_STUDENT);

                }else {

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_GUARDIAN);
                }

                alertDialog.dismiss();

            }
        });


        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_STUDENT && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            Uri uri = data.getData();
            //p_image = new File(getRealPathFromURI(uri));

            Bitmap bitmap = null;

            Log.d(Constants.TAG , "PICK_IMAGE_REQUEST - "+uri);
            Log.d(Constants.TAG , "p_image - "+p_image_student);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                item_image.setImageBitmap(bitmap);

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

                    p_image_student = file;

                    outFile = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outFile);
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

        }else if (requestCode == CAMERA_REQUEST_STUDENT && resultCode == Activity.RESULT_OK) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");

            Log.d(Constants.TAG , "PICK_IMAGE_REQUEST - "+data.getExtras().get("data"));

            File f = new File(Environment.getExternalStorageDirectory().toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }

            try {

                item_image.setImageBitmap(photo);

                String path = Environment.getExternalStorageDirectory()+File.separator;

                f.delete();
                OutputStream outFile = null;
                File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                try {

                    p_image_student = file;

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

        }else if (requestCode == PICK_IMAGE_REQUEST_GUARDIAN && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            Uri uri = data.getData();
            //p_image = new File(getRealPathFromURI(uri));

            Bitmap bitmap = null;

            Log.d(Constants.TAG , "PICK_IMAGE_REQUEST - "+uri);
            Log.d(Constants.TAG , "p_image - "+p_image_guardian);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                item_image_guardian.setImageBitmap(bitmap);

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

                    p_image_guardian = file;

                    outFile = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outFile);
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

        }else if (requestCode == CAMERA_REQUEST_GUARDIAN && resultCode == Activity.RESULT_OK) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");

            Log.d(Constants.TAG , "PICK_IMAGE_REQUEST - "+data.getExtras().get("data"));

            File f = new File(Environment.getExternalStorageDirectory().toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }

            try {

                item_image_guardian.setImageBitmap(photo);

                String path = Environment.getExternalStorageDirectory()+File.separator;

                f.delete();
                OutputStream outFile = null;
                File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                try {

                    p_image_guardian = file;

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
            Cursor cursor = getApplicationContext().getContentResolver().query(contentURI, null, null, null, null);
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

        if (ContextCompat.checkSelfPermission(StudentAdd.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(StudentAdd.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(StudentAdd.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.CAMERA);
        }

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions((Activity) StudentAdd.this, permissionsList.toArray(new String[permissionsList.size()]),
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





    /////////////////

    private void checkValidation(){

        if (!validationClass.validateIsEmpty(student_name, "Enter student name")){
            return;
        }

        if (class_id == null){
            Toasty.info(getApplicationContext(),
                    "Select class",
                    Toast.LENGTH_SHORT, true).show();
            return;
        }

        if (section_id == null){
            Toasty.info(getApplicationContext(),
                    "Select section",
                    Toast.LENGTH_SHORT, true).show();
            return;
        }

        if (subjectSpinnerAdapter.getIdsLength() == 0){
            Toasty.info(getApplicationContext(),
                    "Select subject",
                    Toast.LENGTH_SHORT, true).show();
            return;
        }


        if (!validationClass.validateIsEmpty(roll_no, "Enter Roll no")){
            return;
        }

        if (!validationClass.validateIsEmpty(edt_dob, "Set date of birth")){
            return;
        }

        if (!validationClass.validateMobileNo(phone_no)){
            return;
        }

        if (!validationClass.validateEmail(email)){
            return;
        }

        if (!validationClass.validateIsEmpty(street_name, "Enter street name")){
            return;
        }

        if (!validationClass.validateIsEmpty(city_name, "Enter city name")){
            return;
        }

        if (!validationClass.validateIsEmpty(state_name, "Enter state name")){
            return;
        }

        if (!validationClass.validateIsEmpty(country_name, "Enter country name")){
            return;
        }

        if (!validationClass.validateIsEmpty(pincode, "Enter pincode")){
            return;
        }


        if (radio_male.isChecked()){
            gender = radio_male.getText().toString().toLowerCase();
        }

        if (radio_female.isChecked()){
            gender = radio_female.getText().toString().toLowerCase();
        }




        if (type.matches("edit")){

            updateStudent();

            return;

        }


        /// guardian ...



        if (!validationClass.validateIsEmpty(guardian_name, "Enter guardian name")){
            return;
        }

        if (guardian_relation == null){
            Toasty.info(getApplicationContext(),
                    "Select relation",
                    Toast.LENGTH_SHORT, true).show();
            return;
        }

        if (!validationClass.validateMobileNo(phone_no_guardian)){
            return;
        }

        if (!validationClass.validateEmail(email_guardian)){
            return;
        }


        if (cb_sms_subscription.isChecked()){
            sms_subsription = "Y";
        }else {
            sms_subsription = "N";
        }


        if (type.matches("add")){

            createStudent();

        }else if (type.matches("edit")){

            updateStudent();

        }

    }

    public void createStudent(){

        progressDialog.show();

        String url = ApiClient.add_student;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        params.put(ApiClient.student_name, student_name.getText().toString());
        params.put(ApiClient.institute_id, prefManager.getInstitute_id());
        params.put(ApiClient.class_id, class_id);
        params.put(ApiClient.section_id, section_id);
        params.put(ApiClient.subject_id, subjectSpinnerAdapter.getSubjectIds());
        params.put(ApiClient.gender, gender);
        params.put(ApiClient.dob, edt_dob.getText().toString());
        params.put(ApiClient.mobile_no, phone_no.getText().toString().trim());
        params.put(ApiClient.email_id, email.getText().toString().trim());
        params.put(ApiClient.address, street_name.getText().toString());
        params.put(ApiClient.city, city_name.getText().toString());
        params.put(ApiClient.state, state_name.getText().toString());
        params.put(ApiClient.country, country_name.getText().toString());
        params.put(ApiClient.pincode, pincode.getText().toString());
        params.put(ApiClient.roll_no, roll_no.getText().toString());
        params.put(ApiClient.barcode, barcode.getText().toString());
        params.put(ApiClient.login_type, ApiClient.student);

        try{

            params.put(ApiClient.image, p_image_student);

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.d(Constants.TAG , "add_student - " + url);
        Log.d(Constants.TAG , "add_student - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "add_student- " + response.toString());

                if (response != null) {
                    try {

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            JSONObject student_details = response.getJSONObject("student_details");
                            String id = student_details.optString("id");

                            progressDialog.dismiss();

                            createGuardian(id);

                        }else {

                            Toasty.error(getApplicationContext(),
                                    "Some error occurred. Try Again",
                                    Toast.LENGTH_SHORT, true).show();

                        }


                        progressDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(Commons.TAG, "add_student- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(StudentAdd.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });


    }

    public void createGuardian(String student_id){

        progressDialog.show();

        String url = ApiClient.add_guardian;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        params.put(ApiClient.guardian_name, guardian_name.getText().toString());
        params.put(ApiClient.relation, guardian_relation);
        params.put(ApiClient.institute_id, prefManager.getInstitute_id());
        params.put(ApiClient.student_id, student_id);
        params.put(ApiClient.mobile_no, phone_no_guardian.getText().toString().trim());
        params.put(ApiClient.email_id, email_guardian.getText().toString().trim());
        params.put(ApiClient.address, street_name_guardian.getText().toString());
        params.put(ApiClient.city, city_name_guardian.getText().toString());
        params.put(ApiClient.state, state_name_guardian.getText().toString());
        params.put(ApiClient.country, country_name_guardian.getText().toString());
        params.put(ApiClient.pincode, pincode_guardian.getText().toString());
        params.put(ApiClient.login_type, ApiClient.guardian);
        params.put(ApiClient.sms_subsription, sms_subsription);

        try{

            params.put(ApiClient.image, p_image_guardian);

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.d(Constants.TAG , "add_guardian - " + url);
        Log.d(Constants.TAG , "add_guardian - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "add_guardian- " + response.toString());

                if (response != null) {
                    try {

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            Toasty.success(getApplicationContext(),
                                    "Student & Guardian successfully created",
                                    Toast.LENGTH_LONG, true).show();

                           // Commons.restartActivity(StudentAdd.this);

                            finish();

                        }else {

                            Toasty.error(getApplicationContext(),
                                    "Some error occurred. Try Again",
                                    Toast.LENGTH_SHORT, true).show();

                        }


                        progressDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(Commons.TAG, "add_guardian- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(StudentAdd.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });


    }


    public void updateStudent(){

        progressDialog.show();

        String url = ApiClient.update_student;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        params.put(ApiClient.student_id, studentData.getId());
        params.put(ApiClient.student_name, student_name.getText().toString());
        params.put(ApiClient.institute_id, prefManager.getInstitute_id());
        params.put(ApiClient.class_id, class_id);
        params.put(ApiClient.section_id, section_id);
        params.put(ApiClient.subject_id, subjectSpinnerAdapter.getSubjectIds());
        params.put(ApiClient.gender, gender);
        params.put(ApiClient.dob, edt_dob.getText().toString());
        params.put(ApiClient.mobile_no, phone_no.getText().toString().trim());
        params.put(ApiClient.email_id, email.getText().toString().trim());
        params.put(ApiClient.address, street_name.getText().toString());
        params.put(ApiClient.city, city_name.getText().toString());
        params.put(ApiClient.state, state_name.getText().toString());
        params.put(ApiClient.country, country_name.getText().toString());
        params.put(ApiClient.pincode, pincode.getText().toString());
        params.put(ApiClient.roll_no, roll_no.getText().toString());
        params.put(ApiClient.barcode, barcode.getText().toString());

        try{

            params.put(ApiClient.image, p_image_student);

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.d(Constants.TAG , "update_student - " + url);
        Log.d(Constants.TAG , "update_student - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "update_student- " + response.toString());

                if (response != null) {
                    try {

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            Toasty.success(getApplicationContext(),
                                    message,
                                    Toast.LENGTH_LONG, true).show();

                        }else {

                            Toasty.error(getApplicationContext(),
                                    "Some error occurred. Try Again",
                                    Toast.LENGTH_SHORT, true).show();

                        }


                        progressDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(Commons.TAG, "update_student- " + res);
                progressDialog.dismiss();

                android.app.AlertDialog alert =
                        new android.app.AlertDialog.Builder(StudentAdd.this).create();
                alert.setMessage("Server Error");
                alert.show();

            }

        });


    }









}
