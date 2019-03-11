package com.oado.utils;

public class ApiClient {


    // login type
    public static final String admin = "admin";
    public static final String institute = "institute";
    public static final String teacher = "teacher";
    public static final String student = "student";
    public static final String guardian = "guardian";
    public static final String other_staff = "staff";
    public static final String coaching_center = "coaching_center";
    public static final String school = "school";



    private static final String BASE_URL = "https://www.oado.in/Api/";


    public static final String user_login =              BASE_URL + "user_login";
    public static final String forget_password =         BASE_URL + "forget_password";
    public static final String change_password =         BASE_URL + "change_password";


    public static final String add_institute =           BASE_URL + "add_institute";
    public static final String get_all_institutes =      BASE_URL + "get_all_institutes";
    public static final String delete_institute =        BASE_URL + "delete_institute";
    public static final String update_institute =        BASE_URL + "update_institute";


    public static final String add_section =             BASE_URL + "add_section";
    public static final String get_all_sections =        BASE_URL + "get_all_sections";
    public static final String update_section =          BASE_URL + "update_section";
    public static final String delete_section =          BASE_URL + "delete_section";


    public static final String add_subject =             BASE_URL + "add_subject";
    public static final String update_subject =          BASE_URL + "update_subject";
    public static final String get_all_subjects =        BASE_URL + "get_all_subjects";
    public static final String delete_subject =          BASE_URL + "delete_subject";


    public static final String add_class =               BASE_URL + "add_class";
    public static final String update_class =            BASE_URL + "update_class";
    public static final String delete_class =            BASE_URL + "delete_class";
    public static final String get_all_classes =         BASE_URL + "get_all_classes";


    public static final String add_teacher =             BASE_URL + "add_teacher";
    public static final String update_teacher =          BASE_URL + "update_teacher";
    public static final String delete_teacher =          BASE_URL + "delete_teacher";
    public static final String get_all_teachers =        BASE_URL + "get_all_teachers";


    public static final String add_student =             BASE_URL + "add_student";
    public static final String update_student =          BASE_URL + "update_student";
    public static final String delete_student =          BASE_URL + "delete_student";
    public static final String get_all_students =        BASE_URL + "get_all_students";


    public static final String add_guardian =            BASE_URL + "add_guardian";
    public static final String update_guardian =         BASE_URL + "update_guardian";
    public static final String delete_guardian =         BASE_URL + "delete_guardian";
    public static final String get_all_guardians =       BASE_URL + "get_all_guardians";


    public static final String add_staff =               BASE_URL + "add_staff";
    public static final String update_staff =            BASE_URL + "update_staff";
    public static final String delete_staff =            BASE_URL + "delete_staff";
    public static final String get_all_staffs =          BASE_URL + "get_all_staffs";


    public static final String add_timeslot =            BASE_URL + "add_timeslot";
    public static final String get_all_timeslots =       BASE_URL + "get_all_timeslots";
    public static final String edit_timeslot =           BASE_URL + "edit_timeslot";
    public static final String delete_timeslot =         BASE_URL + "delete_timeslot";

    public static final String add_attendance_via_barcode =
            BASE_URL + "add_attendance_via_barcode";

    public static final String get_attendance_list =     BASE_URL + "get_attendance_list";
    public static final String add_attendance_by_teacher =
            BASE_URL + "add_attendance_by_teacher";
    public static final String get_attendance_report =   BASE_URL + "get_attendance_report";


    public static final String get_student_list =        BASE_URL + "get_student_list";


    public static final String create_exam_result =      BASE_URL + "create_exam_result";
    public static final String get_student_result_list = BASE_URL + "get_student_result_list";
    public static final String get_student_exam_result = BASE_URL + "get_student_exam_result";


    public static final String add_fees =                BASE_URL + "add_fees";
    public static final String get_fees_list =           BASE_URL + "get_fees_list";
    public static final String edit_fees =               BASE_URL + "edit_fees";
    public static final String delete_fees =             BASE_URL + "delete_fees";


    public static final String create_diary =            BASE_URL + "create_diary";
    public static final String get_my_diary =            BASE_URL + "get_my_diary";
    public static final String delete_diary =            BASE_URL + "delete_diary";


    public static final String diary_message =           BASE_URL + "diary_message";
    public static final String get_diary_message =       BASE_URL + "get_diary_message";


    public static final String send_message =            BASE_URL + "send_message";
    public static final String get_received_messages =   BASE_URL + "get_received_messages";
    public static final String get_sent_messages =       BASE_URL + "get_sent_messages";
    public static final String delete_message =          BASE_URL + "delete_message";

    public static final String add_student_fees =        BASE_URL + "add_student_fees";
    public static final String get_student_fees_list =   BASE_URL + "get_student_fees_list";
    public static final String get_fees_list_student =   BASE_URL + "get_fees_list_student";



    public static final String get_class_details =       BASE_URL + "get_class_details";
    public static final String lock_unlock_status =      BASE_URL + "lock_unlock_status";
    public static final String fcm_token_update =        BASE_URL + "fcm_token_update";

    public static final String get_subject_list_student =
            BASE_URL + "get_subject_list_student";

    public static final String get_attendance_out_report =
            BASE_URL + "get_attendance_out_report";

    public static final String add_attendance_out_by_teacher =
            BASE_URL + "add_attendance_out_by_teacher";


        // 69 api



    // parameter ...

    public static final String institute_id = "institute_id";
    public static final String section_id = "section_id";
    public static final String subject_id = "subject_id";
    public static final String class_id = "class_id";
    public static final String teacher_id = "teacher_id";
    public static final String student_id = "student_id";
    public static final String guardian_id = "guardian_id";
    public static final String staff_id = "staff_id";
    public static final String timeslot_id = "timeslot_id";
    public static final String id = "id";


    public static final String email = "email";
    public static final String password = "password";
    public static final String oldpassword = "oldpassword";


    public static final String institute_name = "institute_name";
    public static final String institute_code = "institute_code";
    public static final String mobile_no = "mobile_no";
    public static final String email_id = "email_id";
    public static final String type_of_center = "type_of_center";
    public static final String boardname = "boardname";
    public static final String address = "address";
    public static final String city = "city";
    public static final String state = "state";
    public static final String country = "country";
    public static final String pincode = "pincode";
    public static final String image = "image";
    public static final String login_type = "login_type";
    public static final String sms_subscription = "sms_subscription";

    public static final String section_name = "section_name";

    public static final String subject_name = "subject_name";
    public static final String subject_code = "subject_code";

    public static final String class_name = "class_name";

    public static final String teacher_name = "teacher_name";
    public static final String gender = "gender";
    public static final String dob = "dob";

    public static final String student_name = "student_name";
    public static final String roll_no = "roll_no";
    public static final String barcode = "barcode";

    public static final String guardian_name = "guardian_name";
    public static final String relation = "relation";

    public static final String staff_name = "staff_name";

    public static final String week_days = "week_days";
    public static final String start_time = "start_time";
    public static final String end_time = "end_time";

    public static final String attendance_date = "attendance_date";
    public static final String attendance_time = "attendance_time";

    public static final String student_arr = "student_arr";

    public static final String exam_name = "exam_name";
    public static final String exam_score_arr = "exam_score_arr";

    public static final String fees_arr = "fees_arr";
    public static final String fees_id = "fees_id";
    public static final String fees_amount = "fees_amount";

    public static final String diary_name = "diary_name";
    public static final String user_ids = "user_ids";
    public static final String created_by = "created_by";
    public static final String user_id = "user_id";
    public static final String diary_id = "diary_id";

    public static final String message_type = "message_type";
    public static final String message = "message";
    public static final String link = "link";
    public static final String youtube_link = "youtube_link";
    public static final String event_start_date = "event_start_date";
    public static final String event_end_date = "event_end_date";

    public static final String from_id = "from_id";
    public static final String to_id = "to_id";

    public static final String message_id = "message_id";
    public static final String fcm_reg_token = "fcm_reg_token";
    public static final String from_date = "from_date";
    public static final String to_date = "to_date";





}
