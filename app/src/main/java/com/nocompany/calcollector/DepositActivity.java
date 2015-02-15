package com.nocompany.calcollector;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DepositActivity extends ActionBarActivity {

    private LinearLayout linearlayout_deposit_no_record;
    private LinearLayout linearlayout_depositList_title;
    private LinearLayout linearlayout_depositList_divider;
    private LinearLayout linearlayout_depositList;
    private LinearLayout linearlayout_depositList_subfooter;
    private LinearLayout linearlayout_depositList_footer;
    private TextView textview_deposit_subintotal;
    private TextView textview_deposit_subouttotal;
    private TextView textview_deposit_total;

    private DatabaseHandler database;
    private ArrayList<ContentValues> accountList;
    private int totalInAmount;
    private int totalOutAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);
        setupLayoutVariable();
        setupLayoutFunction();
    }

    private void setupLayoutVariable() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.actionbar_1_deposit);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        linearlayout_deposit_no_record = (LinearLayout)findViewById(R.id.linearlayout_deposit_no_record);
        linearlayout_depositList_title = (LinearLayout)findViewById(R.id.linearlayout_depositList_title);
        linearlayout_depositList_divider = (LinearLayout)findViewById(R.id.linearlayout_depositList_divider);
        linearlayout_depositList = (LinearLayout)findViewById(R.id.linearlayout_depositList);
        linearlayout_depositList_subfooter = (LinearLayout)findViewById(R.id.linearlayout_depositList_subfooter);
        linearlayout_depositList_footer = (LinearLayout)findViewById(R.id.linearlayout_depositList_footer);
        textview_deposit_subintotal = (TextView)findViewById(R.id.textview_deposit_subintotal);
        textview_deposit_subouttotal = (TextView)findViewById(R.id.textview_deposit_subouttotal);
        textview_deposit_total = (TextView)findViewById(R.id.textview_deposit_total);
    }

    private void setupLayoutFunction() {
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupDatabaseAndProperties();
        setupAccountListProperties();
    }

    private void setupDatabaseAndProperties() {
        database = new DatabaseHandler(this);
        accountList = (ArrayList<ContentValues>)database.getAllAccountsSummary();
        totalInAmount = 0;
        totalOutAmount = 0;
    }

    private void setupAccountListProperties() {
        if(accountList.size()>0){
            linearlayout_deposit_no_record.setVisibility(View.GONE);
            linearlayout_depositList_title.setVisibility(View.VISIBLE);
            linearlayout_depositList_divider.setVisibility(View.VISIBLE);
            linearlayout_depositList.setVisibility(View.VISIBLE);
            linearlayout_depositList_subfooter.setVisibility(View.VISIBLE);
            linearlayout_depositList_footer.setVisibility(View.VISIBLE);
            linearlayout_depositList.removeAllViews();
            LayoutInflater layout_inflator = LayoutInflater.from(this);
            LinearLayout linearlayout_accountlist_id;
            TextView line_no;
            ImageView imageview_accountlist_type_1;
            ImageView imageview_accountlist_type_2;
            TextView textview_accountlist_detail;
            TextView textview_accountlist_amount_in;
            TextView textview_accountlist_amount_out;
            TextView textview_accountlist_status;

            int counter = 1;
            for(ContentValues account:accountList){
                View view = layout_inflator.inflate(R.layout.deposit_view, null, false);
                linearlayout_accountlist_id = (LinearLayout)view.findViewById(R.id.linearlayout_accountlist_id);
                line_no = (TextView)view.findViewById(R.id.line_no);
                imageview_accountlist_type_1 = (ImageView)view.findViewById(R.id.imageview_accountlist_type_1);
                imageview_accountlist_type_2 = (ImageView)view.findViewById(R.id.imageview_accountlist_type_2);
                textview_accountlist_detail = (TextView)view.findViewById(R.id.textview_accountlist_detail);
                textview_accountlist_amount_in = (TextView)view.findViewById(R.id.textview_accountlist_amount_in);
                textview_accountlist_amount_out = (TextView)view.findViewById(R.id.textview_accountlist_amount_out);
                textview_accountlist_status = (TextView)view.findViewById(R.id.textview_accountlist_status);

                int id = account.getAsInteger(Account.TableAccount.COLUMN_NAME_ID);
                int accountType = account.getAsInteger(Account.TableAccount.COLUMN_NAME_TYPE);
                int accountStatus = account.getAsInteger(Account.TableAccount.COLUMN_NAME_STATUS);
                String details = account.getAsString(Person.TablePerson.COLUMN_NAME_USERNAME);
                int debit = account.getAsInteger("total_amount_debit");
                int disbursement = account.getAsInteger("total_amount_disbursement");
                int credit = account.getAsInteger("total_amount_credit");

                linearlayout_accountlist_id.setOnClickListener(new AccountOnClickListener(id, accountType, details));
                line_no.setText(String.valueOf(counter));
                int resId_type_1 = R.drawable.account_line_type1_1_daily;
                int resId_type_2 = R.drawable.account_line_type2_1_fixed;
                switch (accountType){
                    case CalCollectorVariables.ACCOUNT_DEPOSIT:
                        imageview_accountlist_type_1.setVisibility(View.INVISIBLE);
                        imageview_accountlist_type_2.setVisibility(View.INVISIBLE);
                        details = "Deposit";
                        break;
                    case CalCollectorVariables.ACCOUNT_SPEND:
                        imageview_accountlist_type_1.setVisibility(View.INVISIBLE);
                        imageview_accountlist_type_2.setVisibility(View.INVISIBLE);
                        details = "Spend";
                        break;
                    case CalCollectorVariables.ACCOUNT_STAND:
                        imageview_accountlist_type_1.setVisibility(View.INVISIBLE);
                        imageview_accountlist_type_2.setVisibility(View.INVISIBLE);
                        details = "Stand";
                        break;
                    case CalCollectorVariables.ACCOUNT_DAY_FIXED:
                        resId_type_1 = R.drawable.account_line_type1_1_daily;
                        resId_type_2 = R.drawable.account_line_type2_1_fixed;
                        break;
                    case CalCollectorVariables.ACCOUNT_DAY_FLEX:
                        resId_type_1 = R.drawable.account_line_type1_1_daily;
                        resId_type_2 = R.drawable.account_line_type2_2_flexi;
                        break;
                    case CalCollectorVariables.ACCOUNT_WEEK_FIXED:
                        resId_type_1 = R.drawable.account_line_type1_2_weekly;
                        resId_type_2 = R.drawable.account_line_type2_1_fixed;
                        break;
                    case CalCollectorVariables.ACCOUNT_WEEK_FLEX:
                        resId_type_1 = R.drawable.account_line_type1_2_weekly;
                        resId_type_2 = R.drawable.account_line_type2_2_flexi;
                        break;
                    case CalCollectorVariables.ACCOUNT_MONTH_FIXED:
                        resId_type_1 = R.drawable.account_line_type1_3_monthly;
                        resId_type_2 = R.drawable.account_line_type2_1_fixed;
                        break;
                    case CalCollectorVariables.ACCOUNT_MONTH_FLEX:
                        resId_type_1 = R.drawable.account_line_type1_3_monthly;
                        resId_type_2 = R.drawable.account_line_type2_2_flexi;
                        break;
                    default:
                        imageview_accountlist_type_1.setVisibility(View.INVISIBLE);
                        imageview_accountlist_type_2.setVisibility(View.INVISIBLE);
                        break;
                }
                imageview_accountlist_type_1.setImageResource(resId_type_1);
                imageview_accountlist_type_2.setImageResource(resId_type_2);
                textview_accountlist_detail.setText(details);

                if(accountType == CalCollectorVariables.ACCOUNT_DEPOSIT){
                    textview_accountlist_amount_in.setText(String.valueOf(debit));
                    textview_accountlist_amount_out.setText(String.valueOf(credit));
                    totalInAmount += debit;
                    totalOutAmount += credit;
                }
                else if(accountType == CalCollectorVariables.ACCOUNT_SPEND){
                    textview_accountlist_amount_in.setText(String.valueOf(credit));
                    textview_accountlist_amount_out.setText(String.valueOf(debit));
                    totalInAmount += credit;
                    totalOutAmount += debit;
                }
                else if(accountType == CalCollectorVariables.ACCOUNT_STAND){
                    textview_accountlist_amount_in.setText(String.valueOf(credit));
                    textview_accountlist_amount_out.setText(String.valueOf(debit));
                }
                else{
                    textview_accountlist_amount_in.setText(String.valueOf(credit));
                    textview_accountlist_amount_out.setText(String.valueOf(disbursement));
                    totalInAmount += credit;
                    totalOutAmount += disbursement;
                }

                switch (accountStatus){
                    case CalCollectorVariables.ACCOUNT_HEADER_STATUS_UNPAID:
                        textview_accountlist_status.setBackgroundResource(R.color.account_unpaid);
                        break;
                    case CalCollectorVariables.ACCOUNT_HEADER_STATUS_PAID:
                        textview_accountlist_status.setBackgroundResource(R.color.account_paid);
                        break;
                    case CalCollectorVariables.ACCOUNT_HEADER_STATUS_BADDEBT:
                        textview_accountlist_status.setBackgroundResource(R.color.account_baddebt);
                        break;
                }

                linearlayout_depositList.addView(view);
                ImageView divider = new ImageView(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 10);
                divider.setLayoutParams(lp);
                linearlayout_depositList.addView(divider);
                counter++;
            }
            textview_deposit_subintotal.setText(String.valueOf(totalInAmount));
            textview_deposit_subouttotal.setText(String.valueOf(totalOutAmount));
            textview_deposit_total.setText(String.valueOf(totalInAmount - totalOutAmount));
        }
        else{
            linearlayout_deposit_no_record.setVisibility(View.VISIBLE);
            linearlayout_depositList_title.setVisibility(View.GONE);
            linearlayout_depositList_divider.setVisibility(View.GONE);
            linearlayout_depositList.setVisibility(View.GONE);
            linearlayout_depositList_subfooter.setVisibility(View.GONE);
            linearlayout_depositList_footer.setVisibility(View.GONE);
        }
    }

    class AccountOnClickListener implements View.OnClickListener{

        int selectedAccountId = 0;
        int accountType;
        String owner;
        AccountOnClickListener(int selectedAccountId, int accountType, String owner){
            this.selectedAccountId = selectedAccountId;
            this.accountType = accountType;
            this.owner = owner;
        };

        @Override
        public void onClick(final View v) {
            Intent intent;
            if(accountType == CalCollectorVariables.ACCOUNT_DEPOSIT){
                intent = new Intent(getApplication(), PrimaryAccountActivity.class);
                intent.putExtra(CalCollectorVariables.INTENT_ACCOUNT_TYPE, CalCollectorVariables.ACCOUNT_DEPOSIT);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else if(accountType == CalCollectorVariables.ACCOUNT_SPEND){
                intent = new Intent(getApplication(), PrimaryAccountActivity.class);
                intent.putExtra(CalCollectorVariables.INTENT_ACCOUNT_TYPE, CalCollectorVariables.ACCOUNT_SPEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else if(accountType == CalCollectorVariables.ACCOUNT_STAND){
                intent = new Intent(getApplication(), PrimaryAccountActivity.class);
                intent.putExtra(CalCollectorVariables.INTENT_ACCOUNT_TYPE, CalCollectorVariables.ACCOUNT_STAND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else{
                intent  = new Intent(getApplication(), AccountDetailsActivity.class);
                intent.putExtra(CalCollectorVariables.INTENT_ACCOUNT_ID, selectedAccountId);
                intent.putExtra(CalCollectorVariables.INTENT_USER_NAME, owner);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_deposit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_deposit) {
            openDialog().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Dialog openDialog(){
        LayoutInflater layout_inflator = LayoutInflater.from(this);
        final View view = layout_inflator.inflate(R.layout.dialog_add_money, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final TextView textview_add_amount = (TextView)view.findViewById(R.id.textview_add_amount);
        final EditText edittext_add_amount = (EditText)view.findViewById(R.id.edittext_add_amount);
        final TextView textview_add_amount_validator = (TextView)view.findViewById(R.id.textview_add_amount_validator);
        textview_add_amount.setText(getResources().getText(R.string.dialog_add_deposit_title));
        builder.setPositiveButton(R.string.dialog_positive, null);
        builder.setNegativeButton(R.string.dialog_negative, null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button_positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button_positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int addDepositAmount = parseStringToInt(String.valueOf(edittext_add_amount.getText()));
                        if(validateDepositAmount(addDepositAmount)){
                            if(addDepositToDatabase(addDepositAmount)){
                                Toast.makeText(getApplication(), "Deposit was successfully added.", Toast.LENGTH_SHORT).show();
                                setupDatabaseAndProperties();
                                setupAccountListProperties();
                                alertDialog.dismiss();
                            }
                            else{
                                Toast.makeText(getApplication(), "Something wrong to the system.\nPlease contact developer.", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        }
                        else{
                            Toast.makeText(getApplication(), "Amount is not valid.", Toast.LENGTH_SHORT).show();
                            textview_add_amount_validator.setBackgroundResource(R.color.not_valid);
                        }
                    }
                });
                Button button_negative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                button_negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
        return alertDialog;
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

    private boolean validateDepositAmount(int addDepositAmount) {
        if(addDepositAmount > 0){
            return true;
        }
        return false;
    }

    private boolean addDepositToDatabase(int addDepositAmount){
        boolean isInserted = false;
        Person host = database.getPersonByIC(CalCollectorVariables.HOST_DIGIT);
        Account newDepositAccount = database.existedAccount(CalCollectorVariables.ACCOUNT_DEPOSIT);
        int hostID;

        if(host == null){
            host = new Person();
            host.setName(CalCollectorVariables.HOST_NAME);
            host.setIc(CalCollectorVariables.HOST_DIGIT);
            host.setType(CalCollectorVariables.HOST_TYPE);
            host.setPhone(CalCollectorVariables.HOST_DIGIT);
            hostID = (int)database.addPerson(host);
            if(hostID == -1){
                return false;
            }
        }
        else{
            hostID = host.getId();
        }

        if(newDepositAccount == null){
            newDepositAccount = new Account();
            newDepositAccount.setType(CalCollectorVariables.ACCOUNT_DEPOSIT);
            newDepositAccount.setOwner(hostID);
            newDepositAccount.setStatus(CalCollectorVariables.ACCOUNT_HEADER_STATUS_PAID);

            AccountLine newDepositAccountLine = new AccountLine();
            newDepositAccountLine.setAmount(addDepositAmount);
            newDepositAccountLine.setStatus(CalCollectorVariables.ACCOUNT_LINE_STATUS_PAID);
            newDepositAccountLine.setType(CalCollectorVariables.ACCOUNT_LINE_TYPE_DEBIT);

            isInserted = database.addAccountAndAccountLine(newDepositAccount, newDepositAccountLine);
        }
        else{
            AccountLine newDepositAccountLine = new AccountLine();
            newDepositAccountLine.setAccount_header_id(newDepositAccount.getId());
            newDepositAccountLine.setAmount(addDepositAmount);
            newDepositAccountLine.setStatus(CalCollectorVariables.ACCOUNT_LINE_STATUS_PAID);
            newDepositAccountLine.setType(CalCollectorVariables.ACCOUNT_LINE_TYPE_DEBIT);

            long newDepositAccountLineID = database.addAccountLine(newDepositAccountLine);
            if(newDepositAccountLineID > 0){
                isInserted = true;
            }
        }
        return isInserted;
    }

    private String getDateTime(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dateAndTime = sdf.format(date);
        return dateAndTime;
    }
}
