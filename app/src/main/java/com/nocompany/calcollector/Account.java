package com.nocompany.calcollector;

import android.provider.BaseColumns;

public class Account {
    private int id;
    private int type;
    private int owner;
    private int interest;
    private int installment_length;
    private int paid_length;
    private int status;
    private String created_date;
    private String updated_date;

    public Account(){
    }

    public Account(int type, int owner, int interest, int installment_length, int paid_length, int status) {
        this.type = type;
        this.owner = owner;
        this.interest = interest;
        this.installment_length = installment_length;
        this.paid_length = paid_length;
        this.status = status;
    }

    public Account(int id, int type, int owner, int interest, int installment_length, int paid_length, int status, String created_date, String updated_date) {
        this.id = id;
        this.type = type;
        this.owner = owner;
        this.interest = interest;
        this.installment_length = installment_length;
        this.paid_length = paid_length;
        this.status = status;
        this.created_date = created_date;
        this.updated_date = updated_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public int getInterest() {
        return interest;
    }

    public void setInterest(int interest) {
        this.interest = interest;
    }

    public int getInstallment_length() {
        return installment_length;
    }

    public void setInstallment_length(int installment_length) {
        this.installment_length = installment_length;
    }

    public int getPaid_length() {
        return paid_length;
    }

    public void setPaid_length(int paid_length) {
        this.paid_length = paid_length;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public static abstract class TableAccount implements BaseColumns {
        public static final String TABLE_NAME = "cal_account";
        public static final String COLUMN_NAME_ID = "cal_account_id";
        public static final String COLUMN_NAME_TYPE = "cal_account_type";
        public static final String COLUMN_NAME_OWNER = "cal_account_owner";
        public static final String COLUMN_NAME_INTEREST = "cal_account_interest";
        public static final String COLUMN_NAME_INSTALLMENT_LENGTH = "cal_account_installment_length";
        public static final String COLUMN_NAME_PAID_LENGTH = "cal_account_paid_length";
        public static final String COLUMN_NAME_STATUS = "cal_account_status";
        public static final String COLUMN_NAME_CREATED_AT = "cal_account_created_at";
        public static final String COLUMN_NAME_UPDATED_AT = "cal_account_updated_at";
    }
}
