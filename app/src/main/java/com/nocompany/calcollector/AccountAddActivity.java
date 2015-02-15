package com.nocompany.calcollector;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AccountAddActivity extends ActionBarActivity {

    private LinearLayout linearlayout_account_duration;

    private ImageView imagebutton_account_daily;
    private ImageView imagebutton_account_weekly;
    private ImageView imagebutton_account_monthly;
    private ImageView imagebutton_account_fixed;
    private ImageView imagebutton_account_flexi;

    private EditText edittext_account_amount;
    private EditText edittext_account_disbursement;
    private EditText edittext_account_installment;
    private EditText edittext_account_length;

    private TextView textview_account_owner_name;
    private TextView textview_account_create_date;
    private TextView textview_account_amount_validator;
    private TextView textview_account_disbursement_validator;
    private TextView textview_account_installment_validator;
    private TextView textview_account_length_validator;
    private TextView textview_account_length_unit;

    private Button button_reset_account;
    private Button button_save_account;

    private final String TPYE_ONE_STATE = "Type_One";
    private final String TPYE_TWO_STATE = "Type_Two";
    private int typeOneId = R.id.imagebutton_account_daily;
    private int typeTwoId = R.id.imagebutton_account_fixed;

    private DatabaseHandler database;
    private int userId;
    private String userName;
    private int amount;
    private int disbursement;
    private int installment;
    private int length;

    private ArrayList<String> arrayListErrorMessage;
    private int totalAllBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_add);
        setupDatabaseAndProperties();
        setupLayoutVariable();
        setupLayoutFunction();
    }

    private void setupDatabaseAndProperties() {
        database = new DatabaseHandler(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getInt(CalCollectorVariables.INTENT_USER_ID);
            userName = extras.getString(CalCollectorVariables.INTENT_USER_NAME);
        }
    }

    private void setupLayoutVariable() {
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        linearlayout_account_duration = (LinearLayout)findViewById(R.id.linearlayout_account_duration);

        imagebutton_account_daily = (ImageButton)findViewById(R.id.imagebutton_account_daily);
        imagebutton_account_weekly = (ImageButton)findViewById(R.id.imagebutton_account_weekly);
        imagebutton_account_monthly = (ImageButton)findViewById(R.id.imagebutton_account_monthly);
        imagebutton_account_fixed = (ImageButton)findViewById(R.id.imagebutton_account_fixed);
        imagebutton_account_flexi = (ImageButton)findViewById(R.id.imagebutton_account_flexi);

        edittext_account_amount = (EditText)findViewById(R.id.edittext_account_amount);
        edittext_account_disbursement = (EditText)findViewById(R.id.edittext_account_disbursement);
        edittext_account_installment = (EditText)findViewById(R.id.edittext_account_installment);
        edittext_account_length = (EditText)findViewById(R.id.edittext_account_length);

        textview_account_owner_name = (TextView)findViewById(R.id.textview_account_owner_name);
        textview_account_create_date = (TextView)findViewById(R.id.textview_account_create_date);
        textview_account_amount_validator = (TextView)findViewById(R.id.textview_account_amount_validator);
        textview_account_disbursement_validator = (TextView)findViewById(R.id.textview_account_disbursement_validator);
        textview_account_installment_validator = (TextView)findViewById(R.id.textview_account_installment_validator);
        textview_account_length_validator = (TextView)findViewById(R.id.textview_account_length_validator);
        textview_account_length_unit = (TextView)findViewById(R.id.textview_account_length_unit);

        button_reset_account = (Button)findViewById(R.id.button_reset_account);
        button_save_account = (Button)findViewById(R.id.button_save_account);
    }

    private void setupLayoutFunction() {
        textview_account_owner_name.setText(userName);
        textview_account_create_date.setText(getCurrentDate());

        imagebutton_account_daily.setOnClickListener(onClickListener);
        imagebutton_account_weekly.setOnClickListener(onClickListener);
        imagebutton_account_monthly.setOnClickListener(onClickListener);
        imagebutton_account_fixed.setOnClickListener(onClickListener);
        imagebutton_account_flexi.setOnClickListener(onClickListener);

        button_reset_account.setOnClickListener(onSaveOrResetListener);
        button_save_account.setOnClickListener(onSaveOrResetListener);
    }

    private String getCurrentDate(){
        String currentDate;
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat time = new SimpleDateFormat("hh:mm a");
        Date current = new Date();
        currentDate  = "Date: " + date.format(current) + "\n";
        currentDate += "Time: " + time.format(current);
        return currentDate;
    }

    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.imagebutton_account_daily:
                    setTypeOneRadioButtonView((ImageButton) v);
                    textview_account_length_unit.setText("Day(s)");
                    break;
                case R.id.imagebutton_account_weekly:
                    setTypeOneRadioButtonView((ImageButton) v);
                    textview_account_length_unit.setText("Week(s)");
                    break;
                case R.id.imagebutton_account_monthly:
                    setTypeOneRadioButtonView((ImageButton) v);
                    textview_account_length_unit.setText("Month(s)");
                    break;
                case R.id.imagebutton_account_fixed:
                    setTypeTwoRadioButtonView((ImageButton)v);
                    linearlayout_account_duration.setVisibility(View.INVISIBLE);
                    break;
                case R.id.imagebutton_account_flexi:
                    setTypeTwoRadioButtonView((ImageButton) v);
                    linearlayout_account_duration.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    private void setTypeOneRadioButtonView(ImageButton imageButton){
        imagebutton_account_daily.setBackground(null);
        imagebutton_account_weekly.setBackground(null);
        imagebutton_account_monthly.setBackground(null);
        imageButton.setBackground(getResources().getDrawable(R.drawable.borders));
        typeOneId = imageButton.getId();
    }

    private void setTypeTwoRadioButtonView(ImageButton imageButton){
        imagebutton_account_fixed.setBackground(null);
        imagebutton_account_flexi.setBackground(null);
        imageButton.setBackground(getResources().getDrawable(R.drawable.borders));
        typeTwoId = imageButton.getId();
    }

    private int parseStringToInt(String intString){
        int strInt = 0;
        try{
            strInt = Integer.parseInt(intString);
        }
        catch(NumberFormatException ex){
        }
        return strInt;
    }

    View.OnClickListener onSaveOrResetListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.button_reset_account:
                    resetAccountFields();
                    break;
                case R.id.button_save_account:
                    saveAccount();
                    break;
            }
        }
    };

    private void resetAccountFields() {
        setTypeOneRadioButtonView((ImageButton) findViewById(R.id.imagebutton_account_daily));
        setTypeTwoRadioButtonView((ImageButton) findViewById(R.id.imagebutton_account_fixed));
        edittext_account_amount.setText("");
        edittext_account_disbursement.setText("");
        edittext_account_installment.setText("");
        edittext_account_length.setText("");
        textview_account_length_unit.setText("Day(s)");
        textview_account_amount_validator.setBackground(null);
        textview_account_disbursement_validator.setBackground(null);
        textview_account_installment_validator.setBackground(null);
        textview_account_length_validator.setBackground(null);
    }

    private void saveAccount() {
        amount = parseStringToInt(String.valueOf(edittext_account_amount.getText()).trim());
        disbursement = parseStringToInt(String.valueOf(edittext_account_disbursement.getText()).trim());
        installment = parseStringToInt(String.valueOf(edittext_account_installment.getText()).trim());
        length = parseStringToInt(String.valueOf(edittext_account_length.getText()).trim());

        if(allValidation()){
            if(checkBalance(disbursement)){
                Account newAccount = new Account();
                newAccount.setType(getAccountType());
                newAccount.setOwner(userId);
                newAccount.setInterest(installment);
                newAccount.setInstallment_length(length);
                newAccount.setStatus(CalCollectorVariables.ACCOUNT_HEADER_STATUS_UNPAID);

                AccountLine newAccountLineAmount = new AccountLine();
                newAccountLineAmount.setAmount(amount);
                newAccountLineAmount.setStatus(CalCollectorVariables.ACCOUNT_LINE_STATUS_PAID);
                newAccountLineAmount.setType(CalCollectorVariables.ACCOUNT_LINE_TYPE_DEBIT);

                AccountLine newAccountLineDisbursement = new AccountLine();
                newAccountLineDisbursement.setAmount(disbursement);
                newAccountLineDisbursement.setStatus(CalCollectorVariables.ACCOUNT_LINE_STATUS_PAID);
                newAccountLineDisbursement.setType(CalCollectorVariables.ACCOUNT_LINE_TYPE_DISBURSEMENT);

                boolean inserted = database.addAccountAndAccountLine(newAccount, newAccountLineAmount, newAccountLineDisbursement);
                if(inserted){
                    Toast.makeText(getApplication(), "Account: \nOwner: " + userName + "\nAmount: " + amount + "\n is created.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            else{
                textview_account_disbursement_validator.setBackgroundResource(R.color.not_valid);
                arrayListErrorMessage.add("No enough money for disbursement!\nAccount Balance: RM " + totalAllBalance);
                errorDialog();
            }
        }
        else{
            errorDialog();
        }
    }

    private boolean allValidation(){
        boolean isAllValidate = false;
        boolean isAmountValid = false,
                isDisbursementValid = false,
                isInstallmentValid = false,
                isLengthValid = false;

        arrayListErrorMessage = new ArrayList<String>();

        if(amount > 0){
            isAmountValid = true;
            textview_account_amount_validator.setBackground(null);
        }
        else{
            textview_account_amount_validator.setBackgroundResource(R.color.not_valid);
            arrayListErrorMessage.add("Amount is not valid.");
        }

        if(disbursement > 0){
            isDisbursementValid = true;
            textview_account_disbursement_validator.setBackground(null);
        }
        else{
            textview_account_disbursement_validator.setBackgroundResource(R.color.not_valid);
            arrayListErrorMessage.add("Disbursement is not valid.");
        }

        if(installment > 0){
            isInstallmentValid = true;
            textview_account_installment_validator.setBackground(null);
        }
        else{
            textview_account_installment_validator.setBackgroundResource(R.color.not_valid);
            arrayListErrorMessage.add("Installment is not valid.");
        }

        if(getAccountType() == CalCollectorVariables.ACCOUNT_DAY_FLEX || getAccountType() == CalCollectorVariables.ACCOUNT_WEEK_FLEX || getAccountType() == CalCollectorVariables.ACCOUNT_MONTH_FLEX){
            if(length > 0){
                isLengthValid = true;
                textview_account_length_validator.setBackground(null);
            }
            else{
                textview_account_length_validator.setBackgroundResource(R.color.not_valid);
                arrayListErrorMessage.add("Duration length must be at least 1.");
            }
        }
        else{
            this.length = 0;
            isLengthValid = true;
            textview_account_length_validator.setBackground(null);
        }

        if(isAmountValid && isDisbursementValid && isInstallmentValid && isLengthValid){
            isAllValidate = true;
        }

        return isAllValidate;
    }

    private boolean checkBalance(int transactionAmount) {
        ArrayList<ContentValues> accountList = (ArrayList<ContentValues>)database.getAllAccountsSummary();
        if(accountList.size()>0){
            int totalInAmount = 0;
            int totalOutAmount = 0;
            totalAllBalance = 0;
            for(ContentValues account:accountList){
                int accountType = account.getAsInteger(Account.TableAccount.COLUMN_NAME_TYPE);
                int debit = account.getAsInteger("total_amount_debit");
                int disbursement = account.getAsInteger("total_amount_disbursement");
                int credit = account.getAsInteger("total_amount_credit");
                if(accountType == CalCollectorVariables.ACCOUNT_DEPOSIT){
                    totalInAmount += debit;
                    totalOutAmount += credit;
                }
                else if(accountType == CalCollectorVariables.ACCOUNT_SPEND){
                    totalInAmount += credit;
                    totalOutAmount += debit;
                }
                else if(accountType == CalCollectorVariables.ACCOUNT_STAND){

                }
                else{
                    totalInAmount += credit;
                    totalOutAmount += disbursement;
                }
            }
            totalAllBalance = totalInAmount - totalOutAmount;
            if(totalAllBalance >= transactionAmount){
                return true;
            }
        }
        return false;
    }

    private int getAccountType(){
        int accountType = CalCollectorVariables.ACCOUNT_DAY_FIXED;
        if(typeOneId == R.id.imagebutton_account_daily && typeTwoId == R.id.imagebutton_account_fixed){
            accountType = CalCollectorVariables.ACCOUNT_DAY_FIXED;
        }
        else if(typeOneId == R.id.imagebutton_account_daily && typeTwoId == R.id.imagebutton_account_flexi){
            accountType = CalCollectorVariables.ACCOUNT_DAY_FLEX;
        }
        else if(typeOneId == R.id.imagebutton_account_weekly && typeTwoId == R.id.imagebutton_account_fixed){
            accountType = CalCollectorVariables.ACCOUNT_WEEK_FIXED;
        }
        else if(typeOneId == R.id.imagebutton_account_weekly && typeTwoId == R.id.imagebutton_account_flexi){
            accountType = CalCollectorVariables.ACCOUNT_WEEK_FLEX;
        }
        else if(typeOneId == R.id.imagebutton_account_monthly && typeTwoId == R.id.imagebutton_account_fixed){
            accountType = CalCollectorVariables.ACCOUNT_MONTH_FIXED;
        }
        else if(typeOneId == R.id.imagebutton_account_monthly && typeTwoId == R.id.imagebutton_account_flexi){
            accountType = CalCollectorVariables.ACCOUNT_MONTH_FLEX;
        }
        return accountType;
    }

    private void errorDialog(){
        String errorMessageString = "";
        for(int i = 0; i < arrayListErrorMessage.size(); i++){
            if(i != arrayListErrorMessage.size() - 1){
                errorMessageString += (i+1)+") "+ arrayListErrorMessage.get(i) + "\n";
            }
            else{
                errorMessageString += (i+1)+") "+ arrayListErrorMessage.get(i);
            }
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AccountAddActivity.this);
        alertDialogBuilder.setTitle("Error Message");
        alertDialogBuilder
                .setMessage(errorMessageString)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null){
            typeOneId = savedInstanceState.getInt(TPYE_ONE_STATE);
            typeTwoId = savedInstanceState.getInt(TPYE_TWO_STATE);

            if(findViewById(typeOneId) != null){
                setTypeOneRadioButtonView((ImageButton)findViewById(typeOneId));
            }
            if(findViewById(typeTwoId) != null){
                setTypeTwoRadioButtonView((ImageButton)findViewById(typeTwoId));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(TPYE_ONE_STATE, typeOneId);
        outState.putInt(TPYE_TWO_STATE, typeTwoId);
        super.onSaveInstanceState(outState);
    }
}
