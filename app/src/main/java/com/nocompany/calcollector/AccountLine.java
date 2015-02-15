package com.nocompany.calcollector;

import android.provider.BaseColumns;

public class AccountLine {
    private int id;
    private int account_header_id;
    private int amount;
    private int status;
    private int type;
    private String remark;
    private String target_date;
    private String created_date;
    private String updated_date;

    public AccountLine(){}

    public AccountLine(int account_header_id, int amount, int status, int type, String remark, String target_date) {
        this.account_header_id = account_header_id;
        this.amount = amount;
        this.status = status;
        this.type = type;
        this.remark = remark;
        this.target_date = target_date;
    }

    public AccountLine(int id, int account_header_id, int amount, int status, int type, String remark, String target_date, String created_date, String updated_date) {
        this.id = id;
        this.account_header_id = account_header_id;
        this.amount = amount;
        this.status = status;
        this.type = type;
        this.remark = remark;
        this.target_date = target_date;
        this.created_date = created_date;
        this.updated_date = updated_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccount_header_id() {
        return account_header_id;
    }

    public void setAccount_header_id(int account_header_id) {
        this.account_header_id = account_header_id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTarget_date() {
        return target_date;
    }

    public void setTarget_date(String target_date) {
        this.target_date = target_date;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(String updated_date) {
        this.updated_date = updated_date;
    }

    public static abstract class TableAccountLine implements BaseColumns {
        public static final String TABLE_NAME = "cal_account_line";
        public static final String COLUMN_NAME_ID = "cal_account_line_id";
        public static final String COLUMN_NAME_HEADER = "cal_account_header_id";
        public static final String COLUMN_NAME_AMOUNT = "cal_account_line_amount";
        public static final String COLUMN_NAME_STATUS = "cal_account_line_status";
        public static final String COLUMN_NAME_TYPE = "cal_account_line_type";
        public static final String COLUMN_NAME_REMARK = "cal_account_line_remark";
        public static final String COLUMN_NAME_TARGET_DATE = "cal_account_line_target_date";
        public static final String COLUMN_NAME_CREATED_AT = "cal_account_line_created_at";
        public static final String COLUMN_NAME_UPDATED_AT = "cal_account_line_updated_at";
    }
}
