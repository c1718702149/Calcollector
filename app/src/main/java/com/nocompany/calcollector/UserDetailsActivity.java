package com.nocompany.calcollector;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;


public class UserDetailsActivity extends ActionBarActivity {

    private LinearLayout linearlayout_username;
    private LinearLayout linearlayout_ic;
    private LinearLayout linearlayout_contact_no;

    private LinearLayout linearlayout_address;
    private LinearLayout linearlayout_address_title;
    private LinearLayout linearlayout_address_streetno1;
    private LinearLayout linearlayout_address_streetno2;
    private LinearLayout linearlayout_address_postcode;
    private LinearLayout linearlayout_address_city;
    private LinearLayout linearlayout_address_state;

    private LinearLayout linearlayout_accounts;
    private LinearLayout linearlayout_accounts_title;
    private LinearLayout linearlayout_accounts_no_record;
    private LinearLayout linearlayout_accounts_header;
    private LinearLayout linearlayout_account_list;

    private TextView textview_username;
    private TextView textview_ic;
    private TextView textview_contact_no;
    private TextView textview_address_streetno1;
    private TextView textview_address_streetno2;
    private TextView textview_address_postcode;
    private TextView textview_address_city;
    private TextView textview_address_state;

    private ImageView imageview_user_edit;
    private ImageView imageview_account_add;
    private ImageView imageview_account_add_handcursor;

    private LinearLayout linearlayout_accounts_audit;
    private TextView textview_account_in;
    private TextView textview_account_out;
    private TextView textview_account_total;

    private DatabaseHandler database;
    private int userId;
    private Person person;
    private ArrayList<ContentValues> accountList;
    private ArrayList<ContentValues> accountsAudit;

    private int disbursement;
    private int credit;
    private int balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getInt(CalCollectorVariables.INTENT_USER_ID);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupDatabaseAndProperties();
        setupLayoutVariable();
        setupLayoutFunction();
    }

    private void setupDatabaseAndProperties(){
        database = new DatabaseHandler(this);
        person = database.getPersonByID(userId);
        accountList = (ArrayList<ContentValues>)database.getUserAccounts(person.getId());
        accountsAudit = (ArrayList<ContentValues>)database.getUserAccountsAudit(person.getId());
    }

    private void setupLayoutVariable() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.actionbar_10_users);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        linearlayout_username = (LinearLayout)findViewById(R.id.linearlayout_username);
        linearlayout_ic = (LinearLayout)findViewById(R.id.linearlayout_ic);
        linearlayout_contact_no = (LinearLayout)findViewById(R.id.linearlayout_contact_no);

        linearlayout_address = (LinearLayout)findViewById(R.id.linearlayout_address);
        linearlayout_address_title = (LinearLayout)findViewById(R.id.linearlayout_address_title);
        linearlayout_address_streetno1 = (LinearLayout)findViewById(R.id.linearlayout_address_streetno1);
        linearlayout_address_streetno2 = (LinearLayout)findViewById(R.id.linearlayout_address_streetno2);
        linearlayout_address_postcode = (LinearLayout)findViewById(R.id.linearlayout_address_postcode);
        linearlayout_address_city = (LinearLayout)findViewById(R.id.linearlayout_address_city);
        linearlayout_address_state = (LinearLayout)findViewById(R.id.linearlayout_address_state);

        linearlayout_accounts = (LinearLayout)findViewById(R.id.linearlayout_accounts);
        linearlayout_accounts_title = (LinearLayout)findViewById(R.id.linearlayout_accounts_title);
        linearlayout_accounts_no_record = (LinearLayout)findViewById(R.id.linearlayout_accounts_no_record);
        linearlayout_accounts_header = (LinearLayout)findViewById(R.id.linearlayout_accounts_header);
        linearlayout_account_list = (LinearLayout)findViewById(R.id.linearlayout_account_list);

        textview_username = (TextView)findViewById(R.id.textview_username);
        textview_ic = (TextView)findViewById(R.id.textview_ic);
        textview_contact_no = (TextView)findViewById(R.id.textview_contact_no);

        textview_address_streetno1 = (TextView)findViewById(R.id.textview_address_streetno1);
        textview_address_streetno2 = (TextView)findViewById(R.id.textview_address_streetno2);
        textview_address_postcode = (TextView)findViewById(R.id.textview_address_postcode);
        textview_address_city = (TextView)findViewById(R.id.textview_address_city);
        textview_address_state = (TextView)findViewById(R.id.textview_address_state);

        imageview_user_edit = (ImageView)findViewById(R.id.imageview_user_edit);
        imageview_account_add = (ImageView)findViewById(R.id.imageview_account_add);
        imageview_account_add_handcursor = (ImageView)findViewById(R.id.imageview_account_add_handcursor);

        linearlayout_accounts_audit = (LinearLayout)findViewById(R.id.linearlayout_accounts_audit);
        textview_account_in =  (TextView)findViewById(R.id.textview_account_in);
        textview_account_out =  (TextView)findViewById(R.id.textview_account_out);
        textview_account_total =  (TextView)findViewById(R.id.textview_account_total);
    }

    private void setupLayoutFunction() {
        textview_username.setText(person.getName());
        textview_ic.setText(person.getIc());
        textview_contact_no.setText(person.getPhone());
        setupAddressProperties();
        setupAccountListProperties();

        imageview_user_edit.setOnClickListener(onClickEvent);
        if(!person.isIs_blacklisted()){
            imageview_account_add.setOnClickListener(onClickEvent);
        }
        else{
            imageview_account_add.setVisibility(View.GONE);
        }

        if(accountsAudit.size() > 0){
            for(ContentValues account : accountsAudit){
                disbursement = account.getAsInteger("total_amount_disbursement");
                credit = account.getAsInteger("total_amount_credit");
            }
            balance = disbursement - credit;
            textview_account_in.setText(String.format("RM%7s", disbursement));
            textview_account_out.setText(String.format("RM%7s", credit));
            textview_account_total.setText(String.format("RM%7s", balance));
        }
    }

    private void setupAddressProperties(){
        String address = person.getAddress();
        if(address != null){
            String addresses[] = address.split(Pattern.quote(CalCollectorVariables.ADDRESS_DELIMITER));
            if(addresses.length>0){
                try{
                    textview_address_streetno1.setText(addresses[CalCollectorVariables.ADDRESS_LINE_1].trim());
                    textview_address_streetno2.setText(addresses[CalCollectorVariables.ADDRESS_LINE_2].trim());
                    textview_address_postcode.setText(addresses[CalCollectorVariables.ADDRESS_POSTCODE].trim());
                    textview_address_city.setText(addresses[CalCollectorVariables.ADDRESS_CITY].trim());
                    textview_address_state.setText(addresses[CalCollectorVariables.ADDRESS_STATE].trim());
                }
                catch(ArrayIndexOutOfBoundsException exp){
                    exp.printStackTrace();
                }
            }
        }
        else{
            textview_address_streetno1.setText("");
            textview_address_streetno2.setText("");
            textview_address_postcode.setText("");
            textview_address_city.setText("");
            textview_address_state.setText("");
        }
    }

    private void setupAccountListProperties() {
        if(accountList.size()>0){
            linearlayout_accounts_no_record.setVisibility(View.GONE);
            linearlayout_accounts_header.setVisibility(View.VISIBLE);
            linearlayout_account_list.setVisibility(View.VISIBLE);
            linearlayout_accounts_audit.setVisibility(View.VISIBLE);
            linearlayout_account_list.removeAllViews();
            LayoutInflater layout_inflator = LayoutInflater.from(this);
            LinearLayout linearlayout_accountlist_id;
            LinearLayout linearlayout_accountlist_type;
            ImageView imageview_accountlist_type_1;
            ImageView imageview_accountlist_type_2;
            TextView textview_accountlist_date;
            TextView textview_accountlist_amount;
            TextView textview_accountlist_status;

            for(ContentValues account:accountList){
                View view = layout_inflator.inflate(R.layout.accounts_view, null, false);
                linearlayout_accountlist_id = (LinearLayout)view.findViewById(R.id.linearlayout_accountlist_id);
                linearlayout_accountlist_type = (LinearLayout)view.findViewById(R.id.linearlayout_accountlist_type);
                imageview_accountlist_type_1 = (ImageView)view.findViewById(R.id.imageview_accountlist_type_1);
                imageview_accountlist_type_2 = (ImageView)view.findViewById(R.id.imageview_accountlist_type_2);
                textview_accountlist_date = (TextView)view.findViewById(R.id.textview_accountlist_date);
                textview_accountlist_amount = (TextView)view.findViewById(R.id.textview_accountlist_amount);
                textview_accountlist_status = (TextView)view.findViewById(R.id.textview_accountlist_status);

                linearlayout_accountlist_id.setOnClickListener(new AccountOnClickListener(account.getAsInteger(Account.TableAccount.COLUMN_NAME_ID), person.getName()));
                int resId_type_1 = R.drawable.account_line_type1_1_daily;
                int resId_type_2 = R.drawable.account_line_type2_1_fixed;
                switch (account.getAsInteger(Account.TableAccount.COLUMN_NAME_TYPE)){
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

                        break;
                }
                imageview_accountlist_type_1.setImageResource(resId_type_1);
                imageview_accountlist_type_2.setImageResource(resId_type_2);
                textview_accountlist_date.setText(formatDatetime(account.getAsString(Account.TableAccount.COLUMN_NAME_UPDATED_AT)));
                textview_accountlist_amount.setText((account.getAsInteger("total_amount").toString()));
                switch (account.getAsInteger(Account.TableAccount.COLUMN_NAME_STATUS)){
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

                linearlayout_account_list.addView(view);
                ImageView divider = new ImageView(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 10);
                divider.setLayoutParams(lp);
                linearlayout_account_list.addView(divider);
            }
        }
        else{
            linearlayout_accounts_no_record.setVisibility(View.VISIBLE);
            linearlayout_accounts_header.setVisibility(View.GONE);
            linearlayout_account_list.setVisibility(View.GONE);
            linearlayout_accounts_audit.setVisibility(View.GONE);
            imageview_account_add_handcursor.setBackgroundResource(R.drawable.handcursor);
            AnimationDrawable handcursorAnimation = (AnimationDrawable) imageview_account_add_handcursor.getBackground();
            handcursorAnimation.start();
        }
    }

    private String formatDatetime(String datetime){
        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(datetime);
            return format.format(date);
        }
        catch(ParseException e){
            e.printStackTrace();
        }
        return null;
    }

    View.OnClickListener onClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()){
                case  R.id.imageview_user_edit:

                    break;
                case  R.id.imageview_account_add:
                    intent = new Intent(UserDetailsActivity.this, AccountAddActivity.class);
                    intent.putExtra(CalCollectorVariables.INTENT_USER_ID, person.getId());
                    intent.putExtra(CalCollectorVariables.INTENT_USER_NAME, person.getName());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    break;
            }
            if(intent != null){
                startActivity(intent);
            }
        }
    };

    class AccountOnClickListener implements View.OnClickListener{

        int selectedAccountId = 0;
        String owner;
        AccountOnClickListener(int selectedAccountId, String owner){
            this.selectedAccountId = selectedAccountId;
            this.owner = owner;
        };

        @Override
        public void onClick(final View v) {
            Intent intent  = new Intent(getApplication(), AccountDetailsActivity.class);
            intent.putExtra(CalCollectorVariables.INTENT_ACCOUNT_ID, selectedAccountId);
            intent.putExtra(CalCollectorVariables.INTENT_USER_NAME, owner);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplication().startActivity(intent);
        }
    }
}
