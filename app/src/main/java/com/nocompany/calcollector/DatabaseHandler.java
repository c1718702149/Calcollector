package com.nocompany.calcollector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHandler extends SQLiteOpenHelper{

    public static final String TAG = DatabaseHandler.class.getSimpleName();
    private static final int DATABASE_VERSION = 16;
    private static final String DATABASE_NAME = "calcollectorDatabase";

    private static final String SQL_CREATE_PERSON =
            "CREATE TABLE " + Person.TablePerson.TABLE_NAME + " (" +
            Person.TablePerson.COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
            Person.TablePerson.COLUMN_NAME_USERNAME + " TEXT NOT NULL, " +
            Person.TablePerson.COLUMN_NAME_IC + " TEXT NOT NULL, " +
            Person.TablePerson.COLUMN_NAME_TYPE + " INTEGER NOT NULL," +
            Person.TablePerson.COLUMN_NAME_ADDRESS + " TEXT, " +
            Person.TablePerson.COLUMN_NAME_PHONE + " TEXT NOT NULL, " +
            Person.TablePerson.COLUMN_NAME_BLACKLISTED + " INTEGER DEFAULT 0, " +
            Person.TablePerson.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            Person.TablePerson.COLUMN_NAME_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

    private static final String SQL_CREATE_ACCOUNT =
            "CREATE TABLE " + Account.TableAccount.TABLE_NAME + " (" +
            Account.TableAccount.COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
            Account.TableAccount.COLUMN_NAME_TYPE + " INTEGER NOT NULL, " +
            Account.TableAccount.COLUMN_NAME_OWNER + " INTEGER NOT NULL, " +
            Account.TableAccount.COLUMN_NAME_INTEREST + " INTEGER DEFAULT 0, " +
            Account.TableAccount.COLUMN_NAME_INSTALLMENT_LENGTH + " INTEGER DEFAULT 0, " +
            Account.TableAccount.COLUMN_NAME_PAID_LENGTH + " INTEGER DEFAULT 0, " +
            Account.TableAccount.COLUMN_NAME_STATUS + " INTEGER DEFAULT 0, " +
            Account.TableAccount.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            Account.TableAccount.COLUMN_NAME_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (" + Account.TableAccount.COLUMN_NAME_OWNER + ") REFERENCES " +
            Person.TablePerson.TABLE_NAME + " (" + Person.TablePerson.COLUMN_NAME_ID + "));";

    private static final String SQL_CREATE_ACCOUNT_LINE =
            "CREATE TABLE " + AccountLine.TableAccountLine.TABLE_NAME + " (" +
            AccountLine.TableAccountLine.COLUMN_NAME_ID + " INTEGER PRIMARY KEY, " +
            AccountLine.TableAccountLine.COLUMN_NAME_HEADER + " INTEGER  NOT NULL, " +
            AccountLine.TableAccountLine.COLUMN_NAME_AMOUNT + " INTEGER DEFAULT 0, " +
            AccountLine.TableAccountLine.COLUMN_NAME_STATUS + " INTEGER DEFAULT 0, " +
            AccountLine.TableAccountLine.COLUMN_NAME_TYPE + " INTEGER DEFAULT 0, " +
            AccountLine.TableAccountLine.COLUMN_NAME_REMARK + " TEXT, " +
            AccountLine.TableAccountLine.COLUMN_NAME_TARGET_DATE + " DATETIME, " +
            AccountLine.TableAccountLine.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            AccountLine.TableAccountLine.COLUMN_NAME_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (" + AccountLine.TableAccountLine.COLUMN_NAME_HEADER  + ") REFERENCES " +
            Account.TableAccount.TABLE_NAME + " (" + Account.TableAccount.COLUMN_NAME_ID + "));";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PERSON);
        db.execSQL(SQL_CREATE_ACCOUNT);
        db.execSQL(SQL_CREATE_ACCOUNT_LINE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AccountLine.TableAccountLine.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Account.TableAccount.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Person.TablePerson.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    public String getDateTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        return currentDateAndTime;
    }

    /**
     * Person
     */
    public long addPerson(Person person){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Person.TablePerson.COLUMN_NAME_USERNAME, person.getName());
        values.put(Person.TablePerson.COLUMN_NAME_IC, person.getIc());
        values.put(Person.TablePerson.COLUMN_NAME_TYPE, person.getType());
        values.put(Person.TablePerson.COLUMN_NAME_ADDRESS, person.getAddress());
        values.put(Person.TablePerson.COLUMN_NAME_PHONE, person.getPhone());
        values.put(Person.TablePerson.COLUMN_NAME_BLACKLISTED, person.isIs_blacklisted());
        values.put(Person.TablePerson.COLUMN_NAME_CREATED_AT, getDateTime());
        values.put(Person.TablePerson.COLUMN_NAME_UPDATED_AT, getDateTime());
        long id = db.insert(Person.TablePerson.TABLE_NAME, null, values);
        db.close();
        return id;
    }
    public Person getPersonByID(int personId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                Person.TablePerson.TABLE_NAME,
                new String[]{
                        Person.TablePerson.COLUMN_NAME_ID,
                        Person.TablePerson.COLUMN_NAME_USERNAME,
                        Person.TablePerson.COLUMN_NAME_IC,
                        Person.TablePerson.COLUMN_NAME_TYPE,
                        Person.TablePerson.COLUMN_NAME_ADDRESS,
                        Person.TablePerson.COLUMN_NAME_PHONE,
                        Person.TablePerson.COLUMN_NAME_BLACKLISTED,
                        Person.TablePerson.COLUMN_NAME_CREATED_AT,
                        Person.TablePerson.COLUMN_NAME_UPDATED_AT},
                Person.TablePerson.COLUMN_NAME_ID + " = ?",
                new String[] {String.valueOf(personId)},
                null,
                null,
                null,
                null);
        Person person = null;
        if(cursor != null && cursor.getCount()>0){
            cursor.moveToFirst();
            person = new Person(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    Integer.parseInt(cursor.getString(3)),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getInt(6)>0,
                    cursor.getString(7),
                    cursor.getString(8)
            );
        }
        cursor.close();
        db.close();
        return person;
    }
    public Person getPersonByName(String personName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                Person.TablePerson.TABLE_NAME,
                new String[]{
                        Person.TablePerson.COLUMN_NAME_ID,
                        Person.TablePerson.COLUMN_NAME_USERNAME,
                        Person.TablePerson.COLUMN_NAME_IC,
                        Person.TablePerson.COLUMN_NAME_TYPE,
                        Person.TablePerson.COLUMN_NAME_ADDRESS,
                        Person.TablePerson.COLUMN_NAME_PHONE,
                        Person.TablePerson.COLUMN_NAME_BLACKLISTED,
                        Person.TablePerson.COLUMN_NAME_CREATED_AT,
                        Person.TablePerson.COLUMN_NAME_UPDATED_AT},
                Person.TablePerson.COLUMN_NAME_USERNAME + " = ?",
                new String[] {personName},
                null,
                null,
                null,
                null);
        Person person = null;
        if(cursor != null && cursor.getCount()>0){
            cursor.moveToFirst();
            person = new Person(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    Integer.parseInt(cursor.getString(3)),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getInt(6)>0,
                    cursor.getString(7),
                    cursor.getString(8)
            );
        }
        cursor.close();
        db.close();
        return person;
    }
    public Person getPersonByIC(String personIC){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                Person.TablePerson.TABLE_NAME,
                new String[]{
                        Person.TablePerson.COLUMN_NAME_ID,
                        Person.TablePerson.COLUMN_NAME_USERNAME,
                        Person.TablePerson.COLUMN_NAME_IC,
                        Person.TablePerson.COLUMN_NAME_TYPE,
                        Person.TablePerson.COLUMN_NAME_ADDRESS,
                        Person.TablePerson.COLUMN_NAME_PHONE,
                        Person.TablePerson.COLUMN_NAME_BLACKLISTED,
                        Person.TablePerson.COLUMN_NAME_CREATED_AT,
                        Person.TablePerson.COLUMN_NAME_UPDATED_AT},
                Person.TablePerson.COLUMN_NAME_IC + " = ?",
                new String[] {personIC},
                null,
                null,
                null,
                null);
        Person person = null;
        if(cursor != null && cursor.getCount()>0){
            cursor.moveToFirst();
            person = new Person(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    Integer.parseInt(cursor.getString(3)),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getInt(6)>0,
                    cursor.getString(7),
                    cursor.getString(8)
            );
        }
        cursor.close();
        db.close();
        return person;
    }
    public boolean isPersonExisted(String personICNo){
        boolean existed = false;
        String selectQuery = "SELECT " + Person.TablePerson.COLUMN_NAME_IC +
                " FROM " + Person.TablePerson.TABLE_NAME +
                " WHERE " + Person.TablePerson.COLUMN_NAME_IC + " = '" + personICNo + "'" +
                " LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor !=null && cursor.getCount()>0){
            existed = true;
        }
        cursor.close();
        db.close();

        return existed;
    }
    public List<Person> getAllPersons(){
        List<Person> personList = new ArrayList<Person>();
        String selectQuery = "SELECT * FROM " + Person.TablePerson.TABLE_NAME
                + " WHERE " + Person.TablePerson.COLUMN_NAME_USERNAME + " != '" + CalCollectorVariables.HOST_NAME + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{
                Person person = new Person();
                person.setId(Integer.parseInt(cursor.getString(0)));
                person.setName(cursor.getString(1));
                person.setIc(cursor.getString(2));
                person.setType(Integer.parseInt(cursor.getString(3)));
                person.setAddress(cursor.getString(4));
                person.setPhone(cursor.getString(5));
                person.setIs_blacklisted(cursor.getInt(6)>0);
                personList.add(person);
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return personList;
    }
    public List<Person> getBlacklistedPerson(){
        List<Person> personList = new ArrayList<Person>();
        String selectQuery = "SELECT * FROM " + Person.TablePerson.TABLE_NAME
                + " WHERE " + Person.TablePerson.COLUMN_NAME_BLACKLISTED + " = 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{
                Person person = new Person();
                person.setId(Integer.parseInt(cursor.getString(0)));
                person.setName(cursor.getString(1));
                person.setIc(cursor.getString(2));
                person.setType(Integer.parseInt(cursor.getString(3)));
                person.setAddress(cursor.getString(4));
                person.setPhone(cursor.getString(5));
                person.setIs_blacklisted(cursor.getInt(6)>0);
                personList.add(person);
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return personList;
    }
    public int getPersonsCount() {
        String countQuery = "SELECT  * FROM " + Person.TablePerson.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
    public int updatePerson(Person person) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Person.TablePerson.COLUMN_NAME_USERNAME, person.getName());
        values.put(Person.TablePerson.COLUMN_NAME_IC, person.getIc());
        values.put(Person.TablePerson.COLUMN_NAME_TYPE, person.getType());
        values.put(Person.TablePerson.COLUMN_NAME_ADDRESS, person.getAddress());
        values.put(Person.TablePerson.COLUMN_NAME_PHONE, person.getPhone());
        values.put(Person.TablePerson.COLUMN_NAME_BLACKLISTED, person.isIs_blacklisted());
        values.put(Person.TablePerson.COLUMN_NAME_UPDATED_AT, getDateTime());
        return db.update(Person.TablePerson.TABLE_NAME,
                values,
                Person.TablePerson.COLUMN_NAME_ID + " = ?",
                new String[] { String.valueOf(person.getId())});
    }
    public void deletePerson(Person person){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Person.TablePerson.TABLE_NAME,
                Person.TablePerson.COLUMN_NAME_ID  + " = ?",
                new String[] { String.valueOf(person.getId())});
        db.close();
    }

    /**
     * Account
     */
    public long addAccount(Account account){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Account.TableAccount.COLUMN_NAME_TYPE, account.getType());
        values.put(Account.TableAccount.COLUMN_NAME_OWNER, account.getOwner());
        values.put(Account.TableAccount.COLUMN_NAME_INTEREST, account.getInterest());
        values.put(Account.TableAccount.COLUMN_NAME_INSTALLMENT_LENGTH, account.getInstallment_length());
        values.put(Account.TableAccount.COLUMN_NAME_PAID_LENGTH, account.getPaid_length());
        values.put(Account.TableAccount.COLUMN_NAME_STATUS, account.getStatus());
        values.put(Account.TableAccount.COLUMN_NAME_CREATED_AT, getDateTime());
        values.put(Account.TableAccount.COLUMN_NAME_UPDATED_AT, getDateTime());
        long id = db.insert(Account.TableAccount.TABLE_NAME, null, values);
        db.close();
        return id;
    }
    public Account getAccountByID(int account_id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                Account.TableAccount.TABLE_NAME,
                new String[]{
                        Account.TableAccount.COLUMN_NAME_ID,
                        Account.TableAccount.COLUMN_NAME_TYPE,
                        Account.TableAccount.COLUMN_NAME_OWNER,
                        Account.TableAccount.COLUMN_NAME_INTEREST,
                        Account.TableAccount.COLUMN_NAME_INSTALLMENT_LENGTH,
                        Account.TableAccount.COLUMN_NAME_PAID_LENGTH,
                        Account.TableAccount.COLUMN_NAME_STATUS,
                        Account.TableAccount.COLUMN_NAME_CREATED_AT,
                        Account.TableAccount.COLUMN_NAME_UPDATED_AT},
                Account.TableAccount.COLUMN_NAME_ID + " = ?",
                new String[] {Integer.toString(account_id)},
                null,
                null,
                null,
                null);
        if(cursor != null)
            cursor.moveToFirst();

        Account account = new Account(
                Integer.parseInt(cursor.getString(0)),
                Integer.parseInt(cursor.getString(1)),
                Integer.parseInt(cursor.getString(2)),
                Integer.parseInt(cursor.getString(3)),
                Integer.parseInt(cursor.getString(4)),
                Integer.parseInt(cursor.getString(5)),
                Integer.parseInt(cursor.getString(6)),
                cursor.getString(7),
                cursor.getString(8));

        cursor.close();
        db.close();
        return account;
    }
    public List<Account> getAccountByType(int account_type){
        List<Account> accountList = new ArrayList<Account>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                Account.TableAccount.TABLE_NAME,
                new String[]{
                        Account.TableAccount.COLUMN_NAME_ID,
                        Account.TableAccount.COLUMN_NAME_TYPE,
                        Account.TableAccount.COLUMN_NAME_OWNER,
                        Account.TableAccount.COLUMN_NAME_INTEREST,
                        Account.TableAccount.COLUMN_NAME_INSTALLMENT_LENGTH,
                        Account.TableAccount.COLUMN_NAME_PAID_LENGTH,
                        Account.TableAccount.COLUMN_NAME_STATUS,
                        Account.TableAccount.COLUMN_NAME_CREATED_AT,
                        Account.TableAccount.COLUMN_NAME_UPDATED_AT},
                Account.TableAccount.COLUMN_NAME_TYPE + " = ?",
                new String[] {Integer.toString(account_type)},
                null,
                null,
                null,
                null);
        if(cursor.moveToFirst()){
            do{
                Account account = new Account();
                account.setId(Integer.parseInt(cursor.getString(0)));
                account.setType(Integer.parseInt(cursor.getString(1)));
                account.setOwner(Integer.parseInt(cursor.getString(2)));
                account.setInterest(Integer.parseInt(cursor.getString(3)));
                account.setInstallment_length(Integer.parseInt(cursor.getString(4)));
                account.setPaid_length(Integer.parseInt(cursor.getString(5)));
                account.setStatus(Integer.parseInt(cursor.getString(6)));
                account.setCreated_date(cursor.getString(7));
                account.setUpdated_date(cursor.getString(8));
                accountList.add(account);
            }
            while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return accountList;
    }
    public List<Account> getAccountByOwner(int account_owner){
        List<Account> accountList = new ArrayList<Account>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                Account.TableAccount.TABLE_NAME,
                new String[]{
                        Account.TableAccount.COLUMN_NAME_ID,
                        Account.TableAccount.COLUMN_NAME_TYPE,
                        Account.TableAccount.COLUMN_NAME_OWNER,
                        Account.TableAccount.COLUMN_NAME_INTEREST,
                        Account.TableAccount.COLUMN_NAME_INSTALLMENT_LENGTH,
                        Account.TableAccount.COLUMN_NAME_PAID_LENGTH,
                        Account.TableAccount.COLUMN_NAME_STATUS,
                        Account.TableAccount.COLUMN_NAME_CREATED_AT,
                        Account.TableAccount.COLUMN_NAME_UPDATED_AT},
                Account.TableAccount.COLUMN_NAME_OWNER + " = ?",
                new String[] {Integer.toString(account_owner)},
                null,
                null,
                null,
                null);
        if(cursor.moveToFirst()){
            do{
                Account account = new Account();
                account.setId(Integer.parseInt(cursor.getString(0)));
                account.setType(Integer.parseInt(cursor.getString(1)));
                account.setOwner(Integer.parseInt(cursor.getString(2)));
                account.setInterest(Integer.parseInt(cursor.getString(3)));
                account.setInstallment_length(Integer.parseInt(cursor.getString(4)));
                account.setPaid_length(Integer.parseInt(cursor.getString(5)));
                account.setStatus(Integer.parseInt(cursor.getString(6)));
                account.setCreated_date(cursor.getString(7));
                account.setUpdated_date(cursor.getString(8));
                accountList.add(account);
            }
            while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return accountList;
    }
    public List<Account> getAccountByStatus(int account_status){
        List<Account> accountList = new ArrayList<Account>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                Account.TableAccount.TABLE_NAME,
                new String[]{
                        Account.TableAccount.COLUMN_NAME_ID,
                        Account.TableAccount.COLUMN_NAME_TYPE,
                        Account.TableAccount.COLUMN_NAME_OWNER,
                        Account.TableAccount.COLUMN_NAME_INTEREST,
                        Account.TableAccount.COLUMN_NAME_INSTALLMENT_LENGTH,
                        Account.TableAccount.COLUMN_NAME_PAID_LENGTH,
                        Account.TableAccount.COLUMN_NAME_STATUS,
                        Account.TableAccount.COLUMN_NAME_CREATED_AT,
                        Account.TableAccount.COLUMN_NAME_UPDATED_AT},
                Account.TableAccount.COLUMN_NAME_STATUS + " = ?",
                new String[] {Integer.toString(account_status)},
                null,
                null,
                null,
                null);
        if(cursor.moveToFirst()){
            do{
                Account account = new Account();
                account.setId(Integer.parseInt(cursor.getString(0)));
                account.setType(Integer.parseInt(cursor.getString(1)));
                account.setOwner(Integer.parseInt(cursor.getString(2)));
                account.setInterest(Integer.parseInt(cursor.getString(3)));
                account.setInstallment_length(Integer.parseInt(cursor.getString(4)));
                account.setPaid_length(Integer.parseInt(cursor.getString(5)));
                account.setStatus(Integer.parseInt(cursor.getString(6)));
                account.setCreated_date(cursor.getString(7));
                account.setUpdated_date(cursor.getString(8));
                accountList.add(account);
            }
            while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return accountList;
    }
    public List<Account> getAllAccounts(){
        List<Account> accountList = new ArrayList<Account>();
        String selectQuery = "SELECT * FROM " + Account.TableAccount.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{
                Account account = new Account();
                account.setId(Integer.parseInt(cursor.getString(0)));
                account.setType(Integer.parseInt(cursor.getString(1)));
                account.setOwner(Integer.parseInt(cursor.getString(2)));
                account.setInterest(Integer.parseInt(cursor.getString(3)));
                account.setInstallment_length(Integer.parseInt(cursor.getString(4)));
                account.setPaid_length(Integer.parseInt(cursor.getString(5)));
                account.setStatus(Integer.parseInt(cursor.getString(6)));
                account.setCreated_date(cursor.getString(7));
                account.setUpdated_date(cursor.getString(8));
                accountList.add(account);
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return accountList;
    }
    public int getAccountsCount() {
        String countQuery = "SELECT  * FROM " + Account.TableAccount.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
    public int updateAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Account.TableAccount.COLUMN_NAME_TYPE, account.getType());
        values.put(Account.TableAccount.COLUMN_NAME_OWNER, account.getOwner());
        values.put(Account.TableAccount.COLUMN_NAME_INTEREST, account.getInterest());
        values.put(Account.TableAccount.COLUMN_NAME_INSTALLMENT_LENGTH, account.getInstallment_length());
        values.put(Account.TableAccount.COLUMN_NAME_PAID_LENGTH, account.getPaid_length());
        values.put(Account.TableAccount.COLUMN_NAME_STATUS, account.getStatus());
        values.put(Account.TableAccount.COLUMN_NAME_UPDATED_AT, getDateTime());
        return db.update(Account.TableAccount.TABLE_NAME,
                values,
                Account.TableAccount.COLUMN_NAME_ID + " = ?",
                new String[] { String.valueOf(account.getId())});
    }
    public void deleteAccount(Account account){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Account.TableAccount.TABLE_NAME,
                Account.TableAccount.COLUMN_NAME_ID + " = ?",
                new String[] { String.valueOf(account.getId())});
        db.close();
    }

    /**
     * Account Line
     */
    public long addAccountLine(AccountLine accountLine){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_HEADER, accountLine.getAccount_header_id());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_AMOUNT, accountLine.getAmount());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_STATUS, accountLine.getStatus());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_TYPE, accountLine.getType());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_REMARK, accountLine.getRemark());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_TARGET_DATE, accountLine.getTarget_date());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_CREATED_AT, getDateTime());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_UPDATED_AT, getDateTime());
        long id = db.insert(AccountLine.TableAccountLine.TABLE_NAME, null, values);
        db.close();
        return id;
    }
    public AccountLine getAccountLineByID(int accountLine_id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                AccountLine.TableAccountLine.TABLE_NAME,
                new String[]{
                        AccountLine.TableAccountLine.COLUMN_NAME_ID,
                        AccountLine.TableAccountLine.COLUMN_NAME_HEADER,
                        AccountLine.TableAccountLine.COLUMN_NAME_AMOUNT,
                        AccountLine.TableAccountLine.COLUMN_NAME_STATUS,
                        AccountLine.TableAccountLine.COLUMN_NAME_TYPE,
                        AccountLine.TableAccountLine.COLUMN_NAME_REMARK,
                        AccountLine.TableAccountLine.COLUMN_NAME_TARGET_DATE,
                        AccountLine.TableAccountLine.COLUMN_NAME_CREATED_AT,
                        AccountLine.TableAccountLine.COLUMN_NAME_UPDATED_AT},
                AccountLine.TableAccountLine.COLUMN_NAME_ID + " = ?",
                new String[] {Integer.toString(accountLine_id)},
                null,
                null,
                null,
                null);
        if(cursor != null)
            cursor.moveToFirst();

        AccountLine accountLine = new AccountLine(
                Integer.parseInt(cursor.getString(0)),
                Integer.parseInt(cursor.getString(1)),
                Integer.parseInt(cursor.getString(2)),
                Integer.parseInt(cursor.getString(3)),
                Integer.parseInt(cursor.getString(4)),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7),
                cursor.getString(8));

        cursor.close();
        db.close();
        return accountLine;
    }
    public List<AccountLine> getAccountLineByHeader(int accountLine_header){
        List<AccountLine> accountLineList = new ArrayList<AccountLine>();
        String selectQuery = "SELECT * FROM " + AccountLine.TableAccountLine.TABLE_NAME
                + " WHERE " + AccountLine.TableAccountLine.COLUMN_NAME_HEADER + " = " + accountLine_header
                + " ORDER BY " + AccountLine.TableAccountLine.COLUMN_NAME_CREATED_AT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{
                AccountLine accountLine = new AccountLine();
                accountLine.setId(Integer.parseInt(cursor.getString(0)));
                accountLine.setAccount_header_id(Integer.parseInt(cursor.getString(1)));
                accountLine.setAmount(Integer.parseInt(cursor.getString(2)));
                accountLine.setStatus(Integer.parseInt(cursor.getString(3)));
                accountLine.setType(Integer.parseInt(cursor.getString(4)));
                accountLine.setRemark(cursor.getString(5));
                accountLine.setTarget_date(cursor.getString(6));
                accountLine.setCreated_date(cursor.getString(7));
                accountLine.setUpdated_date(cursor.getString(8));
                accountLineList.add(accountLine);
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return accountLineList;
    }
    public List<AccountLine> getAllAccountLines(){
        List<AccountLine> accountLineList = new ArrayList<AccountLine>();
        String selectQuery = "SELECT * FROM " + AccountLine.TableAccountLine.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{
                AccountLine accountLine = new AccountLine();
                accountLine.setId(Integer.parseInt(cursor.getString(0)));
                accountLine.setAccount_header_id(Integer.parseInt(cursor.getString(1)));
                accountLine.setAmount(Integer.parseInt(cursor.getString(2)));
                accountLine.setStatus(Integer.parseInt(cursor.getString(3)));
                accountLine.setType(Integer.parseInt(cursor.getString(4)));
                accountLine.setRemark(cursor.getString(5));
                accountLine.setTarget_date(cursor.getString(6));
                accountLine.setCreated_date(cursor.getString(7));
                accountLine.setUpdated_date(cursor.getString(8));
                accountLineList.add(accountLine);
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return accountLineList;
    }
    public int getAccountLinesCount() {
        String countQuery = "SELECT  * FROM " + AccountLine.TableAccountLine.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
    public int updateAccountLine(AccountLine accountLine) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_HEADER, accountLine.getAccount_header_id());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_AMOUNT, accountLine.getAmount());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_STATUS, accountLine.getStatus());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_TYPE, accountLine.getType());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_REMARK, accountLine.getRemark());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_TARGET_DATE, accountLine.getTarget_date());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_UPDATED_AT, getDateTime());
        return db.update(AccountLine.TableAccountLine.TABLE_NAME,
                values,
                AccountLine.TableAccountLine.COLUMN_NAME_ID + " = ?",
                new String[] { String.valueOf(accountLine.getId())});
    }
    public void deleteAccountLine(AccountLine accountLine){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(AccountLine.TableAccountLine.TABLE_NAME,
                AccountLine.TableAccountLine.COLUMN_NAME_ID + " = ?",
                new String[] { String.valueOf(accountLine.getId())});
        db.close();
    }

    /**
     * Custom CRUD
     */
    public List<ContentValues> retrieveAccountType(int account_type, boolean isBadDebt){
        StringBuilder sqlSubQuery = new StringBuilder();
        sqlSubQuery.append("SELECT ");
        sqlSubQuery.append(Account.TableAccount.COLUMN_NAME_ID + ", ");
        sqlSubQuery.append(Account.TableAccount.COLUMN_NAME_UPDATED_AT + ", ");
        sqlSubQuery.append(Account.TableAccount.COLUMN_NAME_OWNER + ", ");
        sqlSubQuery.append("SUM(" + AccountLine.TableAccountLine.COLUMN_NAME_AMOUNT + ") AS total_amount, ");
        sqlSubQuery.append(Account.TableAccount.COLUMN_NAME_STATUS + " ");
        sqlSubQuery.append("FROM " + Account.TableAccount.TABLE_NAME + " INNER JOIN " + AccountLine.TableAccountLine.TABLE_NAME + " ");
        sqlSubQuery.append("ON " + Account.TableAccount.COLUMN_NAME_ID + " = " + AccountLine.TableAccountLine.COLUMN_NAME_HEADER + " ");
        sqlSubQuery.append("WHERE " + AccountLine.TableAccountLine.COLUMN_NAME_TYPE + " = " + CalCollectorVariables.ACCOUNT_LINE_TYPE_DEBIT + " ");

        if(isBadDebt){
            sqlSubQuery.append("AND " + Account.TableAccount.COLUMN_NAME_STATUS + " = " + CalCollectorVariables.ACCOUNT_HEADER_STATUS_BADDEBT + " ");
        }
        else{
            sqlSubQuery.append("AND " + Account.TableAccount.COLUMN_NAME_TYPE + " = " + account_type + " ");
        }

        sqlSubQuery.append("GROUP BY " + Account.TableAccount.COLUMN_NAME_OWNER + ", " + Account.TableAccount.COLUMN_NAME_ID);

        StringBuilder sqlMainQuery = new StringBuilder();
        sqlMainQuery.append("SELECT ");
        sqlMainQuery.append(Account.TableAccount.COLUMN_NAME_ID + ", ");
        sqlMainQuery.append(Account.TableAccount.COLUMN_NAME_UPDATED_AT + ", ");
        sqlMainQuery.append(Person.TablePerson.COLUMN_NAME_USERNAME + ", ");
        sqlMainQuery.append("total_amount, ");
        sqlMainQuery.append(Account.TableAccount.COLUMN_NAME_STATUS + " ");
        sqlMainQuery.append("FROM " + Person.TablePerson.TABLE_NAME + " INNER JOIN (");
        sqlMainQuery.append(sqlSubQuery.toString() + ") ");
        sqlMainQuery.append("ON " + Person.TablePerson.COLUMN_NAME_ID + " = " + Account.TableAccount.COLUMN_NAME_OWNER);

        List<ContentValues> accountList = new ArrayList<ContentValues>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlMainQuery.toString(), null);
        if(cursor.moveToFirst()){
            do{
                ContentValues values = new ContentValues();
                values.put(Account.TableAccount.COLUMN_NAME_ID, Integer.parseInt(cursor.getString(0)));
                values.put(Account.TableAccount.COLUMN_NAME_UPDATED_AT, cursor.getString(1));
                values.put(Person.TablePerson.COLUMN_NAME_USERNAME, cursor.getString(2));
                values.put("total_amount", Integer.parseInt(cursor.getString(3)));
                values.put(Account.TableAccount.COLUMN_NAME_STATUS, Integer.parseInt(cursor.getString(4)));
                accountList.add(values);
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return accountList;
    }

    public List<ContentValues> getUserAccounts(int userID){
        StringBuilder sqlSubQueryAccountLine = new StringBuilder();
        sqlSubQueryAccountLine.append("SELECT ");
        sqlSubQueryAccountLine.append(AccountLine.TableAccountLine.COLUMN_NAME_HEADER + ", ");
        sqlSubQueryAccountLine.append("SUM(" + AccountLine.TableAccountLine.COLUMN_NAME_AMOUNT + ") AS total_amount ");
        sqlSubQueryAccountLine.append("FROM " + AccountLine.TableAccountLine.TABLE_NAME + " ");
        sqlSubQueryAccountLine.append("WHERE " + AccountLine.TableAccountLine.COLUMN_NAME_TYPE + " = " + CalCollectorVariables.ACCOUNT_LINE_TYPE_DEBIT + " ");
        sqlSubQueryAccountLine.append("GROUP BY " + AccountLine.TableAccountLine.COLUMN_NAME_HEADER);

        StringBuilder sqlSubQueryAccount = new StringBuilder();
        sqlSubQueryAccount.append("SELECT ");
        sqlSubQueryAccount.append(Account.TableAccount.COLUMN_NAME_ID + ", ");
        sqlSubQueryAccount.append(Account.TableAccount.COLUMN_NAME_TYPE + ", ");
        sqlSubQueryAccount.append(Account.TableAccount.COLUMN_NAME_OWNER + ", ");
        sqlSubQueryAccount.append("total_amount, ");
        sqlSubQueryAccount.append(Account.TableAccount.COLUMN_NAME_STATUS + ", ");
        sqlSubQueryAccount.append(Account.TableAccount.COLUMN_NAME_UPDATED_AT + " ");
        sqlSubQueryAccount.append("FROM ");
        sqlSubQueryAccount.append(Account.TableAccount.TABLE_NAME + " ");
        sqlSubQueryAccount.append("INNER JOIN ");
        sqlSubQueryAccount.append("(" + sqlSubQueryAccountLine.toString() + ") temp_account_line ");
        sqlSubQueryAccount.append("ON " + Account.TableAccount.COLUMN_NAME_ID + " = " + AccountLine.TableAccountLine.COLUMN_NAME_HEADER + " ");
        sqlSubQueryAccount.append("WHERE " + Account.TableAccount.COLUMN_NAME_OWNER + " = " + userID);

        List<ContentValues> accountList = new ArrayList<ContentValues>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlSubQueryAccount.toString(), null);
        if(cursor.moveToFirst()){
            do{
                ContentValues values = new ContentValues();
                values.put(Account.TableAccount.COLUMN_NAME_ID, Integer.parseInt(cursor.getString(0)));
                values.put(Account.TableAccount.COLUMN_NAME_TYPE, Integer.parseInt(cursor.getString(1)));
                values.put(Account.TableAccount.COLUMN_NAME_OWNER, Integer.parseInt(cursor.getString(2)));
                values.put("total_amount", Integer.parseInt(cursor.getString(3)));
                values.put(Account.TableAccount.COLUMN_NAME_STATUS, Integer.parseInt(cursor.getString(4)));
                values.put(Account.TableAccount.COLUMN_NAME_UPDATED_AT, cursor.getString(5));
                accountList.add(values);
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return accountList;
    }

    public boolean addAccountAndAccountLine(Account account, AccountLine accountLineAmount, AccountLine accountLineDisbursement){
        boolean isSuccess = false;
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = addAccountFirst(account);
            long id = db.insert(Account.TableAccount.TABLE_NAME, null, values);
            if(id > 0){
                accountLineAmount.setAccount_header_id((int)id);
                values = addAccountLineFirst(accountLineAmount);
                long amountLineID  = db.insert(AccountLine.TableAccountLine.TABLE_NAME, null, values);

                accountLineDisbursement.setAccount_header_id((int)id);
                values = addAccountLineFirst(accountLineDisbursement);
                long disbursementLineID = db.insert(AccountLine.TableAccountLine.TABLE_NAME, null, values);

                if(amountLineID > 0 && disbursementLineID > 0){
                    isSuccess = true;
                    db.setTransactionSuccessful();
                }
            }
        }
        finally{
            db.endTransaction();
            db.close();
        }
        return isSuccess;
    }
    private ContentValues addAccountFirst(Account account){
        ContentValues values = new ContentValues();
        values.put(Account.TableAccount.COLUMN_NAME_TYPE, account.getType());
        values.put(Account.TableAccount.COLUMN_NAME_OWNER, account.getOwner());
        values.put(Account.TableAccount.COLUMN_NAME_INTEREST, account.getInterest());
        values.put(Account.TableAccount.COLUMN_NAME_INSTALLMENT_LENGTH, account.getInstallment_length());
        values.put(Account.TableAccount.COLUMN_NAME_PAID_LENGTH, account.getPaid_length());
        values.put(Account.TableAccount.COLUMN_NAME_STATUS, account.getStatus());
        values.put(Account.TableAccount.COLUMN_NAME_CREATED_AT, getDateTime());
        values.put(Account.TableAccount.COLUMN_NAME_UPDATED_AT, getDateTime());
        return values;
    }
    private ContentValues addAccountLineFirst(AccountLine accountLine){
        ContentValues values = new ContentValues();
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_HEADER, accountLine.getAccount_header_id());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_AMOUNT, accountLine.getAmount());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_STATUS, accountLine.getStatus());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_TYPE, accountLine.getType());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_REMARK, accountLine.getRemark());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_TARGET_DATE, accountLine.getTarget_date());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_CREATED_AT, getDateTime());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_UPDATED_AT, getDateTime());
        return values;
    }

    public boolean addOnCreditToAccount(Account account, AccountLine accountLineAmount, AccountLine accountLineDisbursement){
        boolean isSuccess = false;
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = updateAccountOnCredit(account);
            int rowAffedted = db.update(Account.TableAccount.TABLE_NAME,
                    values,
                    Account.TableAccount.COLUMN_NAME_ID + " = ?",
                    new String[] { String.valueOf(account.getId())});
            if(rowAffedted > 0){
                accountLineAmount.setAccount_header_id(account.getId());
                values = addAccountLineFirst(accountLineAmount);
                long amountLineID  = db.insert(AccountLine.TableAccountLine.TABLE_NAME, null, values);

                accountLineDisbursement.setAccount_header_id(account.getId());
                values = addAccountLineFirst(accountLineDisbursement);
                long disbursementLineID = db.insert(AccountLine.TableAccountLine.TABLE_NAME, null, values);

                if(amountLineID > 0 && disbursementLineID > 0){
                    isSuccess = true;
                    db.setTransactionSuccessful();
                }
            }
        }
        finally {
            db.endTransaction();
            db.close();
        }
        return isSuccess;
    }
    private ContentValues updateAccountOnCredit(Account account){
        ContentValues values = new ContentValues();
        values.put(Account.TableAccount.COLUMN_NAME_INTEREST, account.getInterest());
        values.put(Account.TableAccount.COLUMN_NAME_INSTALLMENT_LENGTH, account.getInstallment_length());
        values.put(Account.TableAccount.COLUMN_NAME_PAID_LENGTH, account.getPaid_length());
        values.put(Account.TableAccount.COLUMN_NAME_UPDATED_AT, getDateTime());
        return values;
    }

    public Account existedDepositAccount(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                Account.TableAccount.TABLE_NAME,
                new String[]{
                        Account.TableAccount.COLUMN_NAME_ID,
                        Account.TableAccount.COLUMN_NAME_TYPE,
                        Account.TableAccount.COLUMN_NAME_OWNER,
                        Account.TableAccount.COLUMN_NAME_INTEREST,
                        Account.TableAccount.COLUMN_NAME_INSTALLMENT_LENGTH,
                        Account.TableAccount.COLUMN_NAME_PAID_LENGTH,
                        Account.TableAccount.COLUMN_NAME_STATUS,
                        Account.TableAccount.COLUMN_NAME_CREATED_AT,
                        Account.TableAccount.COLUMN_NAME_UPDATED_AT},
                Account.TableAccount.COLUMN_NAME_TYPE + " = ?",
                new String[] {Integer.toString(CalCollectorVariables.ACCOUNT_DEPOSIT)},
                null,
                null,
                null,
                null);

        Account account = null;
        if(cursor != null && cursor.getCount()>0){
            cursor.moveToFirst();
            account = new Account(
                    Integer.parseInt(cursor.getString(0)),
                    Integer.parseInt(cursor.getString(1)),
                    Integer.parseInt(cursor.getString(2)),
                    Integer.parseInt(cursor.getString(3)),
                    Integer.parseInt(cursor.getString(4)),
                    Integer.parseInt(cursor.getString(5)),
                    Integer.parseInt(cursor.getString(6)),
                    cursor.getString(6),
                    cursor.getString(7));
        }

        cursor.close();
        db.close();
        return account;
    }
    public boolean addAccountAndAccountLineDeposit(Account accountDeposit, AccountLine accountLineDeposit){
        boolean isSuccess = false;
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = addAccountFirst(accountDeposit);
            long id = db.insert(Account.TableAccount.TABLE_NAME, null, values);
            if(id > 0){
                accountLineDeposit.setAccount_header_id((int)id);
                values = addAccountLineFirst(accountLineDeposit);
                long amountLineID  = db.insert(AccountLine.TableAccountLine.TABLE_NAME, null, values);

                if(amountLineID > 0){
                    isSuccess = true;
                    db.setTransactionSuccessful();
                }
            }
        }
        finally{
            db.endTransaction();
            db.close();
        }
        return isSuccess;
    }

    //Be careful when amending this SQL query especially at WHERE clause.
    public List<ContentValues> getAllAccountsSummary(){
        StringBuilder sqlSubInnerQuery = new StringBuilder();
        sqlSubInnerQuery.append("SELECT ");
        sqlSubInnerQuery.append(Account.TableAccount.COLUMN_NAME_ID + ", ");
        sqlSubInnerQuery.append(Account.TableAccount.COLUMN_NAME_TYPE + ", ");
        sqlSubInnerQuery.append(Account.TableAccount.COLUMN_NAME_STATUS + ", ");
        sqlSubInnerQuery.append(Account.TableAccount.COLUMN_NAME_OWNER + ", ");
        sqlSubInnerQuery.append("SUM(CASE WHEN " + AccountLine.TableAccountLine.COLUMN_NAME_TYPE + " = " + CalCollectorVariables.ACCOUNT_LINE_TYPE_DEBIT + " ");
        sqlSubInnerQuery.append("THEN " + AccountLine.TableAccountLine.COLUMN_NAME_AMOUNT + " ELSE 0 END) AS total_amount_debit, ");
        sqlSubInnerQuery.append("SUM(CASE WHEN " + AccountLine.TableAccountLine.COLUMN_NAME_TYPE + " = " + CalCollectorVariables.ACCOUNT_LINE_TYPE_DISBURSEMENT + " ");
        sqlSubInnerQuery.append("THEN " + AccountLine.TableAccountLine.COLUMN_NAME_AMOUNT + " ELSE 0 END) AS total_amount_disbursement, ");
        sqlSubInnerQuery.append("SUM(CASE WHEN " + AccountLine.TableAccountLine.COLUMN_NAME_TYPE + " = " + CalCollectorVariables.ACCOUNT_LINE_TYPE_CREDIT + " ");
        sqlSubInnerQuery.append("THEN " + AccountLine.TableAccountLine.COLUMN_NAME_AMOUNT + " ELSE 0 END) AS total_amount_credit ");
        sqlSubInnerQuery.append("FROM " + Account.TableAccount.TABLE_NAME + " INNER JOIN " + AccountLine.TableAccountLine.TABLE_NAME + " ");
        sqlSubInnerQuery.append("ON " + Account.TableAccount.COLUMN_NAME_ID + " = " + AccountLine.TableAccountLine.COLUMN_NAME_HEADER + " ");

        sqlSubInnerQuery.append("GROUP BY " + Account.TableAccount.COLUMN_NAME_TYPE + ", " + Account.TableAccount.COLUMN_NAME_OWNER  + ", ");
        sqlSubInnerQuery.append(Account.TableAccount.COLUMN_NAME_ID + " ");
        sqlSubInnerQuery.append("ORDER BY (CASE " + Account.TableAccount.COLUMN_NAME_TYPE + " ");
        sqlSubInnerQuery.append("WHEN " + CalCollectorVariables.ACCOUNT_DEPOSIT + " THEN 1 ");
        sqlSubInnerQuery.append("WHEN " + CalCollectorVariables.ACCOUNT_SPEND + " THEN 2 ");
        sqlSubInnerQuery.append("WHEN " + CalCollectorVariables.ACCOUNT_STAND + " THEN 3 ");
        sqlSubInnerQuery.append("ELSE 4 ");
        sqlSubInnerQuery.append("END), ");
        sqlSubInnerQuery.append(Account.TableAccount.COLUMN_NAME_TYPE + " DESC");

        StringBuilder sqlSubOuterQuery = new StringBuilder();
        sqlSubOuterQuery.append("SELECT ");
        sqlSubOuterQuery.append(Account.TableAccount.COLUMN_NAME_ID + ", ");
        sqlSubOuterQuery.append(Account.TableAccount.COLUMN_NAME_TYPE + ", ");
        sqlSubOuterQuery.append(Account.TableAccount.COLUMN_NAME_STATUS + ", ");
        sqlSubOuterQuery.append(Person.TablePerson.COLUMN_NAME_USERNAME + ", ");
        sqlSubOuterQuery.append("total_amount_debit, ");
        sqlSubOuterQuery.append("total_amount_disbursement, ");
        sqlSubOuterQuery.append("total_amount_credit ");
        sqlSubOuterQuery.append("FROM ");
        sqlSubOuterQuery.append(Person.TablePerson.TABLE_NAME + " INNER JOIN ( ");
        sqlSubOuterQuery.append(sqlSubInnerQuery.toString() + ") ");
        sqlSubOuterQuery.append("ON " + Person.TablePerson.COLUMN_NAME_ID + " = " + Account.TableAccount.COLUMN_NAME_OWNER);

        List<ContentValues> accountList = new ArrayList<ContentValues>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlSubOuterQuery.toString(), null);
        if(cursor.moveToFirst()){
            do{
                ContentValues values = new ContentValues();
                values.put(Account.TableAccount.COLUMN_NAME_ID, Integer.parseInt(cursor.getString(0)));
                values.put(Account.TableAccount.COLUMN_NAME_TYPE, Integer.parseInt(cursor.getString(1)));
                values.put(Account.TableAccount.COLUMN_NAME_STATUS, Integer.parseInt(cursor.getString(2)));
                values.put(Person.TablePerson.COLUMN_NAME_USERNAME, cursor.getString(3));
                values.put("total_amount_debit", Integer.parseInt(cursor.getString(4)));
                values.put("total_amount_disbursement", Integer.parseInt(cursor.getString(5)));
                values.put("total_amount_credit", Integer.parseInt(cursor.getString(6)));
                accountList.add(values);
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return accountList;
    }

    public List<ContentValues> getToDoAccountSummary(){
        StringBuilder sqlSub1Query = new StringBuilder();
        sqlSub1Query.append("SELECT ");
        sqlSub1Query.append("OUTER_TABLE." + AccountLine.TableAccountLine.COLUMN_NAME_HEADER + " AS " + AccountLine.TableAccountLine.COLUMN_NAME_HEADER + ", ");
        sqlSub1Query.append("OUTER_TABLE." + AccountLine.TableAccountLine.COLUMN_NAME_CREATED_AT  + " AS " + AccountLine.TableAccountLine.COLUMN_NAME_CREATED_AT + " ");
        sqlSub1Query.append("FROM " + AccountLine.TableAccountLine.TABLE_NAME + " OUTER_TABLE INNER JOIN ( ");
        sqlSub1Query.append("SELECT ");
        sqlSub1Query.append(AccountLine.TableAccountLine.COLUMN_NAME_HEADER + ", ");
        sqlSub1Query.append("MAX(" + AccountLine.TableAccountLine.COLUMN_NAME_CREATED_AT + ") AS MAX_DATE ");
        sqlSub1Query.append("FROM " + AccountLine.TableAccountLine.TABLE_NAME + " ");
        sqlSub1Query.append("WHERE " + AccountLine.TableAccountLine.COLUMN_NAME_TYPE + " = " + CalCollectorVariables.ACCOUNT_LINE_TYPE_DEBIT + " ");
        sqlSub1Query.append("GROUP BY " + AccountLine.TableAccountLine.COLUMN_NAME_HEADER + ") INNER_TABLE ");
        sqlSub1Query.append("ON OUTER_TABLE." + AccountLine.TableAccountLine.COLUMN_NAME_HEADER + " = INNER_TABLE." + AccountLine.TableAccountLine.COLUMN_NAME_HEADER + " ");
        sqlSub1Query.append("AND OUTER_TABLE." + AccountLine.TableAccountLine.COLUMN_NAME_CREATED_AT +" = INNER_TABLE.MAX_DATE ");
        sqlSub1Query.append("GROUP BY OUTER_TABLE." + AccountLine.TableAccountLine.COLUMN_NAME_HEADER + " ");

        StringBuilder sqlSub2Query = new StringBuilder();
        sqlSub2Query.append("SELECT ");
        sqlSub2Query.append(Account.TableAccount.COLUMN_NAME_ID + ", ");
        sqlSub2Query.append(Account.TableAccount.COLUMN_NAME_TYPE + ", ");
        sqlSub2Query.append(Account.TableAccount.COLUMN_NAME_OWNER + ", ");
        sqlSub2Query.append(Account.TableAccount.COLUMN_NAME_INTEREST + ", ");
        sqlSub2Query.append(Account.TableAccount.COLUMN_NAME_PAID_LENGTH + ", ");
        sqlSub2Query.append(AccountLine.TableAccountLine.COLUMN_NAME_CREATED_AT + " ");
        sqlSub2Query.append("FROM " + Account.TableAccount.TABLE_NAME + " INNER JOIN ( ");
        sqlSub2Query.append(sqlSub1Query.toString() + ") ");
        sqlSub2Query.append("ON " + Account.TableAccount.COLUMN_NAME_ID + " = " + AccountLine.TableAccountLine.COLUMN_NAME_HEADER + " ");
        sqlSub2Query.append("WHERE " + Account.TableAccount.COLUMN_NAME_STATUS + " = " + CalCollectorVariables.ACCOUNT_HEADER_STATUS_UNPAID + " ");
        sqlSub2Query.append("AND " + Account.TableAccount.COLUMN_NAME_TYPE + " NOT IN ("
                + CalCollectorVariables.ACCOUNT_DEPOSIT + ", "
                + CalCollectorVariables.ACCOUNT_SPEND + ", "
                + CalCollectorVariables.ACCOUNT_STAND +") ");

        StringBuilder sqlSub3Query = new StringBuilder();
        sqlSub3Query.append("SELECT ");
        sqlSub3Query.append(Account.TableAccount.COLUMN_NAME_ID + ", ");
        sqlSub3Query.append(Account.TableAccount.COLUMN_NAME_TYPE + ", ");
        sqlSub3Query.append(Person.TablePerson.COLUMN_NAME_USERNAME + ", ");
        sqlSub3Query.append(Account.TableAccount.COLUMN_NAME_INTEREST + ", ");
        sqlSub3Query.append(Account.TableAccount.COLUMN_NAME_PAID_LENGTH + ", ");
        sqlSub3Query.append(AccountLine.TableAccountLine.COLUMN_NAME_CREATED_AT + " ");
        sqlSub3Query.append("FROM " + Person.TablePerson.TABLE_NAME + " INNER JOIN ( ");
        sqlSub3Query.append(sqlSub2Query.toString() + ") ");
        sqlSub3Query.append("ON " + Person.TablePerson.COLUMN_NAME_ID + " = " + Account.TableAccount.COLUMN_NAME_OWNER);

        List<ContentValues> accountList = new ArrayList<ContentValues>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlSub3Query.toString(), null);//first one
        if(cursor.moveToFirst()){
            do{
                ContentValues values = new ContentValues();
                values.put(Account.TableAccount.COLUMN_NAME_ID, Integer.parseInt(cursor.getString(0)));
                values.put(Account.TableAccount.COLUMN_NAME_TYPE, Integer.parseInt(cursor.getString(1)));
                values.put(Person.TablePerson.COLUMN_NAME_USERNAME, cursor.getString(2));
                values.put(Account.TableAccount.COLUMN_NAME_INTEREST, Integer.parseInt(cursor.getString(3)));
                values.put(Account.TableAccount.COLUMN_NAME_PAID_LENGTH, Integer.parseInt(cursor.getString(4)));
                values.put(AccountLine.TableAccountLine.COLUMN_NAME_CREATED_AT, cursor.getString(5));
                accountList.add(values);
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return accountList;
    }

    //Future concern -- if pay amount is greater than owing amount? should set to full payment?
    public boolean addInstallmentToDatabase(AccountLine accountLine){
        boolean isSuccess = false;
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = getAccountLineInfo(accountLine);
            long newAccountLineID  = db.insert(AccountLine.TableAccountLine.TABLE_NAME, null, values);
            if(newAccountLineID > 0){
                ContentValues valuesAccount = updateAccountInfo(accountLine.getAccount_header_id());
                int rowEffected = db.update(Account.TableAccount.TABLE_NAME, valuesAccount, Account.TableAccount.COLUMN_NAME_ID + " = ?", new String[] { String.valueOf(accountLine.getAccount_header_id())});
                if(rowEffected > 0){
                    isSuccess = true;
                    db.setTransactionSuccessful();
                }
            }
        }
        finally {
            db.endTransaction();
            db.close();
        }
        return isSuccess;
    }
    private ContentValues getAccountLineInfo(AccountLine accountLine){
        ContentValues values = new ContentValues();
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_HEADER, accountLine.getAccount_header_id());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_AMOUNT, accountLine.getAmount());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_STATUS, accountLine.getStatus());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_TYPE, accountLine.getType());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_TARGET_DATE, accountLine.getTarget_date());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_CREATED_AT, getDateTime());
        values.put(AccountLine.TableAccountLine.COLUMN_NAME_UPDATED_AT, getDateTime());
        return values;
    }
    private ContentValues updateAccountInfo(int account_id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                Account.TableAccount.TABLE_NAME,
                new String[]{
                        Account.TableAccount.COLUMN_NAME_TYPE,
                        Account.TableAccount.COLUMN_NAME_INSTALLMENT_LENGTH,
                        Account.TableAccount.COLUMN_NAME_PAID_LENGTH},
                Account.TableAccount.COLUMN_NAME_ID + " = ?",
                new String[] {Integer.toString(account_id)},
                null,
                null,
                null,
                null);

        if(cursor != null)
            cursor.moveToFirst();

        Account account = new Account();
        account.setType(Integer.parseInt(cursor.getString(0)));
        account.setInstallment_length(Integer.parseInt(cursor.getString(1)));
        account.setPaid_length(Integer.parseInt(cursor.getString(2)));

        cursor.close();

        int currentPaidLength = account.getPaid_length() + 1;
        int accountStatus = CalCollectorVariables.ACCOUNT_HEADER_STATUS_UNPAID;
        if(account.getType() == CalCollectorVariables.ACCOUNT_DAY_FLEX
                || account.getType() == CalCollectorVariables.ACCOUNT_WEEK_FLEX
                || account.getType() == CalCollectorVariables.ACCOUNT_MONTH_FLEX){
            if(currentPaidLength >= account.getInstallment_length()){
                accountStatus = CalCollectorVariables.ACCOUNT_HEADER_STATUS_PAID;
            }
        }

        ContentValues values = new ContentValues();
        values.put(Account.TableAccount.COLUMN_NAME_PAID_LENGTH, currentPaidLength);
        values.put(Account.TableAccount.COLUMN_NAME_STATUS, accountStatus);
        values.put(Account.TableAccount.COLUMN_NAME_UPDATED_AT, getDateTime());
        return values;
    }

    public boolean addFullPaymentToDatabase(AccountLine accountLine){
        boolean isSuccess = false;
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = getAccountLineInfo(accountLine);
            long newAccountLineID  = db.insert(AccountLine.TableAccountLine.TABLE_NAME, null, values);
            if(newAccountLineID > 0){
                ContentValues valuesAccount = updateAccountFullPayment();
                int rowEffected = db.update(Account.TableAccount.TABLE_NAME, valuesAccount, Account.TableAccount.COLUMN_NAME_ID + " = ?", new String[] { String.valueOf(accountLine.getAccount_header_id())});
                if(rowEffected > 0){
                    isSuccess = true;
                    db.setTransactionSuccessful();
                }
            }
        }
        finally {
            db.endTransaction();
            db.close();
        }
        return isSuccess;
    }
    private ContentValues updateAccountFullPayment(){
        ContentValues values = new ContentValues();
        values.put(Account.TableAccount.COLUMN_NAME_STATUS, CalCollectorVariables.ACCOUNT_HEADER_STATUS_PAID);
        values.put(Account.TableAccount.COLUMN_NAME_UPDATED_AT, getDateTime());
        return values;
    }

    public boolean blacklistPersonAndAccounts(int personId){
        boolean isSuccess = false;
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues personValues = blacklistPerson();
            int rowEffectedPerson = db.update(Person.TablePerson.TABLE_NAME, personValues, Person.TablePerson.COLUMN_NAME_ID + " = ?", new String[] { String.valueOf(personId)});
            if(rowEffectedPerson > 0){
                ContentValues accountValuesAccount = blacklistPersonAccount();
                int rowEffectedAccount = db.update(Account.TableAccount.TABLE_NAME,
                        accountValuesAccount,
                        Account.TableAccount.COLUMN_NAME_OWNER + " = ? AND " + Account.TableAccount.COLUMN_NAME_STATUS + " = ?",
                        new String[] { String.valueOf(personId), String.valueOf(CalCollectorVariables.ACCOUNT_HEADER_STATUS_UNPAID)});
                if(rowEffectedAccount > 0){
                    isSuccess = true;
                    db.setTransactionSuccessful();
                }
            }
        }
        finally {
            db.endTransaction();
            db.close();
        }

        return isSuccess;
    }
    private ContentValues blacklistPerson() {
        ContentValues values = new ContentValues();
        values.put(Person.TablePerson.COLUMN_NAME_BLACKLISTED, true);
        values.put(Person.TablePerson.COLUMN_NAME_UPDATED_AT, getDateTime());
        return values;
    }
    private ContentValues blacklistPersonAccount(){
        ContentValues values = new ContentValues();
        values.put(Account.TableAccount.COLUMN_NAME_STATUS, CalCollectorVariables.ACCOUNT_HEADER_STATUS_BADDEBT);
        values.put(Account.TableAccount.COLUMN_NAME_UPDATED_AT, getDateTime());
        return values;
    }

    public boolean unblacklistPersonAndAccount(int personId){
        boolean isSuccess = false;
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues personValues = unblacklistPerson();
            int rowEffectedPerson = db.update(Person.TablePerson.TABLE_NAME, personValues, Person.TablePerson.COLUMN_NAME_ID + " = ?", new String[] { String.valueOf(personId)});
            if(rowEffectedPerson > 0){
                ContentValues accountValuesAccount = unblacklistPersonAccount();
                int rowEffectedAccount = db.update(Account.TableAccount.TABLE_NAME,
                        accountValuesAccount,
                        Account.TableAccount.COLUMN_NAME_OWNER + " = ? AND " + Account.TableAccount.COLUMN_NAME_STATUS + " = ?",
                        new String[] { String.valueOf(personId), String.valueOf(CalCollectorVariables.ACCOUNT_HEADER_STATUS_BADDEBT)});
                if(rowEffectedAccount > 0){
                    isSuccess = true;
                    db.setTransactionSuccessful();
                }
            }
        }
        finally {
            db.endTransaction();
            db.close();
        }
        return isSuccess;
    }
    private ContentValues unblacklistPerson(){
        ContentValues values = new ContentValues();
        values.put(Person.TablePerson.COLUMN_NAME_BLACKLISTED, false);
        values.put(Person.TablePerson.COLUMN_NAME_UPDATED_AT, getDateTime());
        return values;
    }
    private ContentValues unblacklistPersonAccount(){
        ContentValues values = new ContentValues();
        values.put(Account.TableAccount.COLUMN_NAME_STATUS, CalCollectorVariables.ACCOUNT_HEADER_STATUS_UNPAID);
        values.put(Account.TableAccount.COLUMN_NAME_UPDATED_AT, getDateTime());
        return values;
    }

    public boolean deletePersonAndAccount(int personId){
        boolean isSuccess = false;
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try{

        }
        finally {
            db.endTransaction();
            db.close();
        }
        return isSuccess;
    }
    private String deleteAccountDetailsString(int personId){
        String sqlSubquery = "SELECT " +
                Account.TableAccount.COLUMN_NAME_ID +
                " WHERE " + Account.TableAccount.COLUMN_NAME_OWNER + " = " + personId;

        String sqlMainQuery = "DELETE FROM " + AccountLine.TableAccountLine.TABLE_NAME +
                " WHERE " + AccountLine.TableAccountLine.COLUMN_NAME_HEADER + " IN (" +
                sqlSubquery + ")";

        return sqlMainQuery;
    }
    private String deleteAccountsString(int personId){
        String sqlMainQuery = "DELETE FROM " + Account.TableAccount.TABLE_NAME +
                " WHERE " + Account.TableAccount.COLUMN_NAME_OWNER + " = " + personId;
        return sqlMainQuery;
    }

    public List<ContentValues> getHelperNameList(){
        List<ContentValues> helperNameList = new ArrayList<ContentValues>();
        String selectQuery = "SELECT " + Person.TablePerson.COLUMN_NAME_ID
                + ", " + Person.TablePerson.COLUMN_NAME_USERNAME
                + " FROM " + Person.TablePerson.TABLE_NAME
                + " WHERE " + Person.TablePerson.COLUMN_NAME_TYPE + " = 2";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{
                ContentValues values = new ContentValues();
                values.put(Person.TablePerson.COLUMN_NAME_ID, Integer.parseInt(cursor.getString(0)));
                values.put(Person.TablePerson.COLUMN_NAME_USERNAME, cursor.getString(1));
                helperNameList.add(values);
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return helperNameList;
    }

    public Account existedAccount(int accountType){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                Account.TableAccount.TABLE_NAME,
                new String[]{
                        Account.TableAccount.COLUMN_NAME_ID,
                        Account.TableAccount.COLUMN_NAME_TYPE,
                        Account.TableAccount.COLUMN_NAME_OWNER,
                        Account.TableAccount.COLUMN_NAME_INTEREST,
                        Account.TableAccount.COLUMN_NAME_INSTALLMENT_LENGTH,
                        Account.TableAccount.COLUMN_NAME_PAID_LENGTH,
                        Account.TableAccount.COLUMN_NAME_STATUS,
                        Account.TableAccount.COLUMN_NAME_CREATED_AT,
                        Account.TableAccount.COLUMN_NAME_UPDATED_AT},
                Account.TableAccount.COLUMN_NAME_TYPE + " = ?",
                new String[] {Integer.toString(accountType)},
                null,
                null,
                null,
                null);

        Account account = null;
        if(cursor != null && cursor.getCount()>0){
            cursor.moveToFirst();
            account = new Account(
                    Integer.parseInt(cursor.getString(0)),
                    Integer.parseInt(cursor.getString(1)),
                    Integer.parseInt(cursor.getString(2)),
                    Integer.parseInt(cursor.getString(3)),
                    Integer.parseInt(cursor.getString(4)),
                    Integer.parseInt(cursor.getString(5)),
                    Integer.parseInt(cursor.getString(6)),
                    cursor.getString(6),
                    cursor.getString(7));
        }

        cursor.close();
        db.close();
        return account;
    }
    public boolean addAccountAndAccountLine(Account account, AccountLine accountLine){
        boolean isSuccess = false;
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = addAccountFirst(account);
            long id = db.insert(Account.TableAccount.TABLE_NAME, null, values);
            if(id > 0){
                accountLine.setAccount_header_id((int)id);
                values = addAccountLineFirst(accountLine);
                long amountLineID  = db.insert(AccountLine.TableAccountLine.TABLE_NAME, null, values);

                if(amountLineID > 0){
                    isSuccess = true;
                    db.setTransactionSuccessful();
                }
            }
        }
        finally{
            db.endTransaction();
            db.close();
        }
        return isSuccess;
    }

    public List<ContentValues> getUserAccountsAudit(int userID){
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append("SELECT ");
        sqlQuery.append("SUM(CASE WHEN " + AccountLine.TableAccountLine.COLUMN_NAME_TYPE + " = " + CalCollectorVariables.ACCOUNT_LINE_TYPE_DEBIT + " ");
        sqlQuery.append("THEN " + AccountLine.TableAccountLine.COLUMN_NAME_AMOUNT + " ELSE 0 END) AS total_amount_debit, ");
        sqlQuery.append("SUM(CASE WHEN " + AccountLine.TableAccountLine.COLUMN_NAME_TYPE + " = " + CalCollectorVariables.ACCOUNT_LINE_TYPE_DISBURSEMENT + " ");
        sqlQuery.append("THEN " + AccountLine.TableAccountLine.COLUMN_NAME_AMOUNT + " ELSE 0 END) AS total_amount_disbursement, ");
        sqlQuery.append("SUM(CASE WHEN " + AccountLine.TableAccountLine.COLUMN_NAME_TYPE + " = " + CalCollectorVariables.ACCOUNT_LINE_TYPE_CREDIT + " ");
        sqlQuery.append("THEN " + AccountLine.TableAccountLine.COLUMN_NAME_AMOUNT + " ELSE 0 END) AS total_amount_credit ");
        sqlQuery.append("FROM " + Account.TableAccount.TABLE_NAME + " INNER JOIN " + AccountLine.TableAccountLine.TABLE_NAME + " ");
        sqlQuery.append("ON " + Account.TableAccount.COLUMN_NAME_ID + " = " + AccountLine.TableAccountLine.COLUMN_NAME_HEADER + " ");
        sqlQuery.append("WHERE " + Account.TableAccount.COLUMN_NAME_OWNER + " = " + userID);

        List<ContentValues> accountList = new ArrayList<ContentValues>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery.toString(), null);
        if(cursor.moveToFirst()){
            do{
                try{
                    ContentValues values = new ContentValues();
                    values.put("total_amount_debit", Integer.parseInt(cursor.getString(0)));
                    values.put("total_amount_disbursement", Integer.parseInt(cursor.getString(1)));
                    values.put("total_amount_credit", Integer.parseInt(cursor.getString(2)));
                    accountList.add(values);
                }
                catch(NumberFormatException ex){
                }
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return accountList;
    }
}












































