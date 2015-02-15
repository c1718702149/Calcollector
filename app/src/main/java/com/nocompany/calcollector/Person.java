package com.nocompany.calcollector;

import android.provider.BaseColumns;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class Person {

    private int id;
    private String name;
    private String ic;
    private int type;
    private String address;
    private String phone;
    private boolean is_blacklisted;
    private String created_datetime;
    private String updated_datetime;

    public Person() {}

    public Person(String name, String ic, int type, String address, String phone) {
        this.name = name;
        this.ic = ic;
        this.type = type;
        this.address = address;
        this.phone = phone;
    }

    public Person(int id, String name, String ic, int type, String address, String phone, boolean is_blacklisted, String created_datetime, String updated_datetime) {
        this.id = id;
        this.name = name;
        this.ic = ic;
        this.type = type;
        this.address = address;
        this.phone = phone;
        this.is_blacklisted = is_blacklisted;
        this.created_datetime = created_datetime;
        this.updated_datetime = updated_datetime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIc() {
        return ic;
    }

    public void setIc(String ic) {
        this.ic = ic;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isIs_blacklisted() {
        return is_blacklisted;
    }

    public void setIs_blacklisted(boolean is_blacklisted) {
        this.is_blacklisted = is_blacklisted;
    }

    public String getCreated_datetime() {
        return created_datetime;
    }

    public void setCreated_datetime(String created_datetime) {
        this.created_datetime = created_datetime;
    }

    public String getUpdated_datetime() {
        return updated_datetime;
    }

    public void setUpdated_datetime(String updated_datetime) {
        this.updated_datetime = updated_datetime;
    }

    public static abstract class TablePerson implements BaseColumns{
        public static final String TABLE_NAME = "cal_person";
        public static final String COLUMN_NAME_ID = "cal_person_id";
        public static final String COLUMN_NAME_USERNAME = "cal_person_name";
        public static final String COLUMN_NAME_IC = "cal_person_ic";
        public static final String COLUMN_NAME_TYPE= "cal_person_type";
        public static final String COLUMN_NAME_ADDRESS = "cal_person_address";
        public static final String COLUMN_NAME_PHONE = "cal_person_phone";
        public static final String COLUMN_NAME_BLACKLISTED = "cal_person_blacklisted";
        public static final String COLUMN_NAME_CREATED_AT = "cal_person_created_at";
        public static final String COLUMN_NAME_UPDATED_AT = "cal_person_updated_at";
    }
}

