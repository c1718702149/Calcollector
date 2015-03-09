package com.nocompany.calcollector;

public class CalCollectorVariables {
    public static final int LAYOUT_DEPOSIT = R.id.linearlayout_deposit;
    public static final int LAYOUT_TODO = R.id.linearlayout_todo;
    public static final int LAYOUT_USER = R.id.linearlayout_user;
    public static final int LAYOUT_STAND = R.id.linearlayout_stand;

    public static final int POSITION_DAILY = 0;
    public static final int POSITION_WEEKLY = 1;
    public static final int POSITION_MONTHLY = 2;
    public static final int POSITION_SPEND = 3;
    public static final int POSITION_BAD_DEBT = 4;
    public static final int POSITION_BLACKLISTED = 5;

    public static final int ACCOUNT_DEPOSIT = 600;
    public static final int ACCOUNT_SPEND = 601;
    public static final int ACCOUNT_STAND = 602;
    public static final int ACCOUNT_MONTH_FIXED = 603;
    public static final int ACCOUNT_MONTH_FLEX = 604;
    public static final int ACCOUNT_WEEK_FIXED = 605;
    public static final int ACCOUNT_WEEK_FLEX = 606;
    public static final int ACCOUNT_DAY_FIXED = 607;
    public static final int ACCOUNT_DAY_FLEX = 608;

    public static final int ACCOUNT_HEADER_STATUS_UNPAID = 0;
    public static final int ACCOUNT_HEADER_STATUS_PAID = 1;
    public static final int ACCOUNT_HEADER_STATUS_BADDEBT = 2;

    public static final int ACCOUNT_LINE_STATUS_UNPAID = 0;
    public static final int ACCOUNT_LINE_STATUS_PAID = 1;

    public static final int ACCOUNT_LINE_TYPE_DEBIT = 0;
    public static final int ACCOUNT_LINE_TYPE_DISBURSEMENT = 1;
    public static final int ACCOUNT_LINE_TYPE_CREDIT = 2;

    public static final String USER_CREDENTIAL_DELIMITER = "[C]";

    public static final String INTENT_USER_ID = "INTENT_USER_ID";
    public static final String INTENT_USER_NAME = "INTENT_USER_NAME";
    public static final String HOST_NAME = "IAMHOST";
    public static final String HOST_DIGIT = "01234567890";
    public static final int HOST_TYPE = 1;
    public static final String USERNAME_DELIMITER = "[H]";
    public static final String INTENT_ACCOUNT_ID = "INTENT_ACCOUNT_ID";

    public static final String ADDRESS_DELIMITER = "[|]";
    public static final int ADDRESS_LINE_1 = 0;
    public static final int ADDRESS_LINE_2 = 1;
    public static final int ADDRESS_POSTCODE = 2;
    public static final int ADDRESS_CITY = 3;
    public static final int ADDRESS_STATE = 4;

    public static final String RECEIVER_REMARK_DELIMITER = "[R]";
    public static final String INTENT_ACCOUNT_TYPE = "INTENT_ACCOUNT_TYPE";
}
