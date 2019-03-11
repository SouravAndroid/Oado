package com.oado.database;

public class MsgData {

    private int id;
    private String msg;
    private String date_time;
    private String by_who;
    private long timestamp;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getBy_who() {
        return by_who;
    }

    public void setBy_who(String by_who) {
        this.by_who = by_who;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
