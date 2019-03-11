package com.oado.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.oado.utils.ApiClient;
import com.oado.utils.Commons;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "oado.db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    private static final String TABLE_NOTIFICATION = "oado_noti";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_MSG = "msg";
    private static final String COLUMN_DATE_TIME = "date_time";
    private static final String COLUMN_SEND_BY_WHO = "by_who";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NOTIFICATION + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_MSG + " TEXT,"
                    + COLUMN_DATE_TIME + " TEXT,"
                    + COLUMN_SEND_BY_WHO + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);

        onCreate(db);
    }


    private String getCurrentDateTime() {

        DateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }


    public void insertData(String msg, String by_who){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_MSG, msg);
        values.put(COLUMN_SEND_BY_WHO, by_who);
        values.put(COLUMN_DATE_TIME, getCurrentDateTime());

        db.insert(TABLE_NOTIFICATION, null, values);

        db.close();

        Log.d(Commons.TAG, "inserted");
    }

    public void deleteItem(int id){

        String rowId = String.valueOf(id);

        SQLiteDatabase db = this.getReadableDatabase();

        String where = COLUMN_ID +" = ?";
        String[] whereArg = {rowId};

        db.delete(TABLE_NOTIFICATION, where, whereArg);

    }

    public void deleteTableData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTIFICATION, null, null);
    }

    public ArrayList<MsgData> getAllMsg(){

        ArrayList<MsgData> lisMsgData = new ArrayList<>();
        MsgData msgData;

        SQLiteDatabase db = this.getReadableDatabase();
        String orderBy = DatabaseHelper.COLUMN_TIMESTAMP + " DESC";
        Cursor cursor = db.query(TABLE_NOTIFICATION, null, null,
                null,null,null, orderBy);

        while(cursor.moveToNext()){
            msgData = new MsgData();

            msgData.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            msgData.setMsg(cursor.getString(cursor.getColumnIndex(COLUMN_MSG)));
            msgData.setDate_time(cursor.getString(cursor.getColumnIndex(COLUMN_DATE_TIME)));
            msgData.setBy_who(cursor.getString(cursor.getColumnIndex(COLUMN_SEND_BY_WHO)));
            msgData.setTimestamp(cursor.getLong(cursor.getColumnIndex(COLUMN_TIMESTAMP)));

            lisMsgData.add(msgData);
        }
        cursor.close();

        return lisMsgData;
    }

    public String getDateFromTimeStamp(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy, hh:mm a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis((int) milliSeconds);
        return formatter.format(calendar.getTime());
    }


}
