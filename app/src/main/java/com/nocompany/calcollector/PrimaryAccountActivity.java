package com.nocompany.calcollector;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.regex.Pattern;

public class PrimaryAccountActivity extends ActionBarActivity {

    private LinearLayout linearlayout_account_no_record;
    private LinearLayout linearlayout_account_title;
    private LinearLayout linearlayout_account_divider;
    private LinearLayout linearlayout_account_list;
    private LinearLayout linearlayout_account_footer;
    private TextView textview_account_title_remark;
    private TextView textview_account_total_title;
    private TextView textview_account_total;

    private int accountType;
    private Account account;
    private ArrayList<AccountLine> accountLineList;
    private ArrayList<ContentValues> helperNameList;
    private int totalInAmount;
    private int totalOutAmount;
    private int totalAllBalance;

    private DatabaseHandler database;
    private ArrayList<String> arrayListErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary_account);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            accountType = extras.getInt(CalCollectorVariables.INTENT_ACCOUNT_TYPE);
        }
        setupLayoutVariable();
        setupLayoutFunction();
    }

    private void setupLayoutVariable() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.actionbar_1_deposit);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        linearlayout_account_no_record = (LinearLayout)findViewById(R.id.linearlayout_account_no_record);
        linearlayout_account_title = (LinearLayout)findViewById(R.id.linearlayout_account_title);
        linearlayout_account_divider = (LinearLayout)findViewById(R.id.linearlayout_account_divider);
        linearlayout_account_list = (LinearLayout)findViewById(R.id.linearlayout_account_list);
        linearlayout_account_footer = (LinearLayout)findViewById(R.id.linearlayout_account_footer);
        textview_account_title_remark = (TextView)findViewById(R.id.textview_account_title_remark);
        textview_account_total_title = (TextView)findViewById(R.id.textview_account_total_title);
        textview_account_total = (TextView)findViewById(R.id.textview_account_total);

        switch (accountType){
            case CalCollectorVariables.ACCOUNT_DEPOSIT:
                setTitle("Deposit");
                getSupportActionBar().setLogo(R.drawable.actionbar_1_deposit);
                textview_account_title_remark.setVisibility(View.GONE);
                textview_account_total_title.setLayoutParams(new TableRow.LayoutParams(0, ActionBar.LayoutParams.MATCH_PARENT, 5f));
                break;
            case CalCollectorVariables.ACCOUNT_SPEND:
                setTitle("Spend");
                getSupportActionBar().setLogo(R.drawable.actionbar_6_spend);
                textview_account_title_remark.setText("Details");
                break;
            case CalCollectorVariables.ACCOUNT_STAND:
                setTitle("Stand");
                getSupportActionBar().setLogo(R.drawable.actionbar_9_stand);
                textview_account_title_remark.setText("Receiver");
                break;
            default:
                break;
        }
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
        if(database.getAccountByType(accountType).size() > 0){
            account = database.getAccountByType(accountType).get(0);
            accountLineList = (ArrayList<AccountLine>)database.getAccountLineByHeader(account.getId());
        }
        helperNameList = (ArrayList<ContentValues>)database.getHelperNameList();
    }

    private void setupAccountListProperties() {
        if(accountLineList != null && accountLineList.size()>0){
            linearlayout_account_no_record.setVisibility(View.GONE);
            linearlayout_account_title.setVisibility(View.VISIBLE);
            linearlayout_account_divider.setVisibility(View.VISIBLE);
            linearlayout_account_list.setVisibility(View.VISIBLE);
            linearlayout_account_footer.setVisibility(View.VISIBLE);
            linearlayout_account_list.removeAllViews();

            LayoutInflater layout_inflator = LayoutInflater.from(this);
            TextView line_no;
            TextView textview_account_list_date;
            LinearLayout linearlayout_account_list_remark;
            TextView textview_account_list_remark;
            ImageView imageview_account_list_extra;
            TextView textview_account_list_amount;

            int counter = 1;
            totalInAmount = 0;
            totalOutAmount = 0;
            for(AccountLine accountLine : accountLineList){
                View view = layout_inflator.inflate(R.layout.primary_account_view, null, false);
                line_no = (TextView)view.findViewById(R.id.line_no);
                textview_account_list_date = (TextView)view.findViewById(R.id.textview_account_list_date);
                linearlayout_account_list_remark = (LinearLayout)view.findViewById(R.id.linearlayout_account_list_remark);
                textview_account_list_remark = (TextView)view.findViewById(R.id.textview_account_list_remark);
                imageview_account_list_extra = (ImageView)view.findViewById(R.id.imageview_account_list_extra);
                textview_account_list_amount = (TextView)view.findViewById(R.id.textview_account_list_amount);

                line_no.setText(String.valueOf(counter));
                textview_account_list_date.setText(formatDatetime(accountLine.getCreated_date()));
                if(accountLine.getRemark() != null){
                    if(accountLine.getRemark().contains(CalCollectorVariables.RECEIVER_REMARK_DELIMITER)){
                        final String[] remarks = accountLine.getRemark().split(Pattern.quote(CalCollectorVariables.RECEIVER_REMARK_DELIMITER));
                        if(accountType == CalCollectorVariables.ACCOUNT_SPEND){
                            textview_account_list_remark.setText(remarks[0]);
                            if(Integer.parseInt(remarks[1]) != 0){
                                imageview_account_list_extra.setImageResource(R.drawable.account_line_primary_extra_2);
                                linearlayout_account_list_remark.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String helperName = "";
                                        for(ContentValues helper : helperNameList){
                                            if(helper.getAsInteger(Person.TablePerson.COLUMN_NAME_ID) == Integer.parseInt(remarks[1])){
                                                helperName = helper.getAsString(Person.TablePerson.COLUMN_NAME_USERNAME);
                                            }
                                        }
                                        Toast.makeText(getApplication(), "Receiver Name: \n" + helperName, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        else if(accountType == CalCollectorVariables.ACCOUNT_STAND){
                            String helperName = "";
                            for(ContentValues helper : helperNameList){
                                if(helper.getAsInteger(Person.TablePerson.COLUMN_NAME_ID) == Integer.parseInt(remarks[1])){
                                    helperName = helper.getAsString(Person.TablePerson.COLUMN_NAME_USERNAME);
                                }
                            }
                            textview_account_list_remark.setText(helperName);
                            if(remarks[0].trim().length() > 0){
                                imageview_account_list_extra.setImageResource(R.drawable.account_line_primary_extra_1);
                                linearlayout_account_list_remark.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(getApplication(), "Remark:\n" + remarks[0] , Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                }
                else{
                    linearlayout_account_list_remark.setVisibility(View.GONE);
                }

                if(accountLine.getType() == CalCollectorVariables.ACCOUNT_LINE_TYPE_CREDIT){
                    textview_account_list_amount.setText("-" + String.valueOf(accountLine.getAmount()));
                    totalOutAmount += accountLine.getAmount();
                }
                else if(accountLine.getType() == CalCollectorVariables.ACCOUNT_LINE_TYPE_DEBIT){
                    textview_account_list_amount.setText(String.valueOf(accountLine.getAmount()));
                    totalInAmount += accountLine.getAmount();
                }

                linearlayout_account_list.addView(view);
                ImageView divider = new ImageView(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 10);
                divider.setLayoutParams(lp);
                linearlayout_account_list.addView(divider);
                counter++;
            }
            textview_account_total.setText(String.valueOf(totalInAmount - totalOutAmount));
        }
        else{
            linearlayout_account_no_record.setVisibility(View.VISIBLE);
            linearlayout_account_title.setVisibility(View.GONE);
            linearlayout_account_divider.setVisibility(View.GONE);
            linearlayout_account_list.setVisibility(View.GONE);
            linearlayout_account_list.setVisibility(View.GONE);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_primary_account, menu);
        switch (accountType){
            case CalCollectorVariables.ACCOUNT_DEPOSIT:
                menu.findItem(R.id.action_add_deposit).setVisible(true);
                menu.findItem(R.id.action_take_deposit).setVisible(true);
                break;
            case CalCollectorVariables.ACCOUNT_SPEND:
                menu.findItem(R.id.action_add_spend).setVisible(true);
                break;
            case CalCollectorVariables.ACCOUNT_STAND:
                menu.findItem(R.id.action_add_stand).setVisible(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_add_deposit:
                openDepositDialog(true).show();
                break;
            case R.id.action_take_deposit:
                openDepositDialog(false).show();
                break;
            case R.id.action_add_spend:
                openDialog().show();
                break;
            case R.id.action_add_stand:
                openDialog().show();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Dialog openDepositDialog(final boolean isAddDeposit){
        LayoutInflater layout_inflator = LayoutInflater.from(this);
        final View view = layout_inflator.inflate(R.layout.dialog_add_money, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final ImageView imageview_add_money = (ImageView)view.findViewById(R.id.imageview_add_money);
        final TextView textview_add_amount = (TextView)view.findViewById(R.id.textview_add_amount);
        final EditText edittext_add_amount = (EditText)view.findViewById(R.id.edittext_add_amount);
        final TextView textview_add_amount_validator = (TextView)view.findViewById(R.id.textview_add_amount_validator);
        if(!isAddDeposit){
            imageview_add_money.setImageResource(R.drawable.dialog_take_money);
            textview_add_amount.setText(getResources().getText(R.string.dialog_take_deposit_title));
        }

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
                        textview_add_amount_validator.setBackground(null);
                        arrayListErrorMessage = new ArrayList<String>();
                        int amount = parseStringToInt(String.valueOf(edittext_add_amount.getText()));
                        if(validateMoneyAmount(amount)){
                            if(isAddDeposit){
                                String message;
                                if(addDepositToDatabase(amount)){
                                    message = "Deposit was successfully added.";
                                }
                                else{
                                    message = "Something wrong to the system.\nPlease contact developer.";
                                }
                                Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
                                setupDatabaseAndProperties();
                                setupAccountListProperties();
                                alertDialog.dismiss();
                            }
                            else{
                                if(checkBalance(amount)){
                                    String message;
                                    if(takeDepositFromDatabase(amount)){
                                        message = "Deposit was successfully deducted.";
                                    }
                                    else{
                                        message = "Something wrong to the system.\nPlease contact developer.";
                                    }
                                    Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
                                    setupDatabaseAndProperties();
                                    setupAccountListProperties();
                                    alertDialog.dismiss();
                                }
                                else{
                                    Toast.makeText(getApplication(), "No enough money for withdrawing!\nAccount Balance: RM " + totalAllBalance, Toast.LENGTH_SHORT).show();
                                    textview_add_amount_validator.setBackgroundResource(R.color.not_valid);
                                }
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

    private Dialog openDialog(){
        LayoutInflater layout_inflator = LayoutInflater.from(this);
        final View view = layout_inflator.inflate(R.layout.dialog_add_spend_stand, null);
        LinearLayout linearlayout_add_title = (LinearLayout)view.findViewById(R.id.linearlayout_add_title);
        LinearLayout linearlayout_item_remark = (LinearLayout)view.findViewById(R.id.linearlayout_item_remark);
        LinearLayout linearlayout_item_price = (LinearLayout)view.findViewById(R.id.linearlayout_item_price);
        LinearLayout linearlayout_item_receiver = (LinearLayout)view.findViewById(R.id.linearlayout_item_receiver);

        ((LinearLayout)view).removeAllViews();
        ((LinearLayout)view).addView(linearlayout_add_title);

        if(accountType == CalCollectorVariables.ACCOUNT_SPEND){
            ((LinearLayout)view).addView(linearlayout_item_remark);
            ((LinearLayout)view).addView(linearlayout_item_price);
            ((LinearLayout)view).addView(linearlayout_item_receiver);
        }
        else if(accountType == CalCollectorVariables.ACCOUNT_STAND){
            ((LinearLayout)view).addView(linearlayout_item_receiver);
            ((LinearLayout)view).addView(linearlayout_item_price);
            ((LinearLayout)view).addView(linearlayout_item_remark);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        final ImageView imageview_add_spend_stand = (ImageView)view.findViewById(R.id.imageview_add_spend_stand);
        final TextView textview_add_spend_stand = (TextView)view.findViewById(R.id.textview_add_spend_stand);
        final EditText edittext_item_remark = (EditText)view.findViewById(R.id.edittext_item_remark);
        final EditText edittext_item_price = (EditText)view.findViewById(R.id.edittext_item_price);
        final Spinner spinner_item_receiver = (Spinner)view.findViewById(R.id.spinner_item_receiver);
        final TextView textview_item_remark_validator = (TextView)view.findViewById(R.id.textview_item_remark_validator);
        final TextView textview_item_price_validator = (TextView)view.findViewById(R.id.textview_item_price_validator);
        final TextView textview_item_receiver_validator = (TextView)view.findViewById(R.id.textview_item_receiver_validator);

        if(accountType == CalCollectorVariables.ACCOUNT_SPEND){
            imageview_add_spend_stand.setImageResource(R.drawable.dialog_spend);
            textview_add_spend_stand.setText(R.string.dialog_add_spend_title);
        }
        else if(accountType == CalCollectorVariables.ACCOUNT_STAND){
            imageview_add_spend_stand.setImageResource(R.drawable.dialog_stand);
            textview_add_spend_stand.setText(R.string.dialog_add_stand_title);
        }

        final ArrayList<ContentValues> helperNameList = (ArrayList<ContentValues>)database.getHelperNameList();
        if(helperNameList.size() <= 0){
            ContentValues values = new ContentValues();
            values.put(Person.TablePerson.COLUMN_NAME_ID, 0);
            values.put(Person.TablePerson.COLUMN_NAME_USERNAME, "No Helper Yet");
            helperNameList.add(values);
        }
        else{
            ContentValues values = new ContentValues();
            values.put(Person.TablePerson.COLUMN_NAME_ID, 0);
            values.put(Person.TablePerson.COLUMN_NAME_USERNAME, "Select one");
            helperNameList.add(0, values);
        }
        ArrayAdapter<ContentValues> spinnerArrayAdapter = new ArrayAdapter<ContentValues>(this, android.R.layout.simple_spinner_item, helperNameList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView)view;
                textView.setText(helperNameList.get(position).getAsString(Person.TablePerson.COLUMN_NAME_USERNAME));
                textView.setGravity(Gravity.CENTER);
                if(position == 0){
                    textView.setTextColor(getResources().getColor(R.color.font_color));
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView)view;
                textView.setText(helperNameList.get(position).getAsString(Person.TablePerson.COLUMN_NAME_USERNAME));
                textView.setGravity(Gravity.CENTER);
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_item_receiver.setAdapter(spinnerArrayAdapter);

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
                        String remark = String.valueOf(edittext_item_remark.getText()).trim();
                        int amount = parseStringToInt(String.valueOf(edittext_item_price.getText()).trim());
                        int receiver = ((ContentValues)spinner_item_receiver.getSelectedItem()).getAsInteger(Person.TablePerson.COLUMN_NAME_ID);
                        boolean isRemarkValid = false,
                                isAmountValid = false,
                                isReceiverValid = false;
                        arrayListErrorMessage = new ArrayList<String>();

                        if(accountType == CalCollectorVariables.ACCOUNT_SPEND){
                            if(remark.length() <=3){
                                arrayListErrorMessage.add("- The remark is too short.");
                                textview_item_remark_validator.setBackgroundResource(R.color.not_valid);
                            }
                            else{
                                textview_item_remark_validator.setBackground(null);
                                isRemarkValid = true;
                            }
                            if(amount <= 0){
                                arrayListErrorMessage.add("- Invalid amount.");
                                textview_item_price_validator.setBackgroundResource(R.color.not_valid);
                            }
                            else{
                                if(!checkBalance(amount)){
                                    arrayListErrorMessage.add("- No enough money for spending!");
                                    textview_item_price_validator.setBackgroundResource(R.color.not_valid);
                                }
                                else{
                                    textview_item_price_validator.setBackground(null);
                                    isAmountValid = true;
                                }
                            }

                            if(isRemarkValid && isAmountValid){
                                String message;
                                if(addSpendingToDatabase(remark, amount, receiver)){
                                    message = "Spending was successfully added.";
                                }
                                else{
                                    message = "Something wrong to the system.\nPlease contact developer.";
                                }
                                Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
                                setupDatabaseAndProperties();
                                setupAccountListProperties();
                                alertDialog.dismiss();
                            }
                            else{
                                errorToast();
                            }
                        }
                        else if(accountType == CalCollectorVariables.ACCOUNT_STAND){
                            if(receiver == 0){
                                arrayListErrorMessage.add("- No receiver is selected.");
                                textview_item_receiver_validator.setBackgroundResource(R.color.not_valid);
                            }
                            else{
                                textview_item_receiver_validator.setBackground(null);
                                isReceiverValid = true;
                            }
                            if(amount <= 0){
                                arrayListErrorMessage.add("- Invalid amount.");
                                textview_item_price_validator.setBackgroundResource(R.color.not_valid);
                            }
                            else{
                                textview_item_price_validator.setBackground(null);
                                isAmountValid = true;
                            }

                            if(isAmountValid && isReceiverValid){
                                String message;
                                if(addStandToDatabase(remark, amount, receiver)){
                                    message = "Stand was successfully added.";
                                }
                                else{
                                    message = "Something wrong to the system.\nPlease contact developer.";
                                }
                                Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
                                setupDatabaseAndProperties();
                                setupAccountListProperties();
                                alertDialog.dismiss();
                            }
                            else{
                                errorToast();
                            }
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

    private boolean validateMoneyAmount(int amount) {
        boolean isValid = false;
        if(amount > 0){
            isValid = true;
        }
        return isValid;
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

    private boolean takeDepositFromDatabase(int takeDepositAmount){
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
            newDepositAccountLine.setAmount(takeDepositAmount);
            newDepositAccountLine.setStatus(CalCollectorVariables.ACCOUNT_LINE_STATUS_PAID);
            newDepositAccountLine.setType(CalCollectorVariables.ACCOUNT_LINE_TYPE_CREDIT);

            isInserted = database.addAccountAndAccountLine(newDepositAccount, newDepositAccountLine);
        }
        else{
            AccountLine newDepositAccountLine = new AccountLine();
            newDepositAccountLine.setAccount_header_id(newDepositAccount.getId());
            newDepositAccountLine.setAmount(takeDepositAmount);
            newDepositAccountLine.setStatus(CalCollectorVariables.ACCOUNT_LINE_STATUS_PAID);
            newDepositAccountLine.setType(CalCollectorVariables.ACCOUNT_LINE_TYPE_CREDIT);

            long newDepositAccountLineID = database.addAccountLine(newDepositAccountLine);
            if(newDepositAccountLineID > 0){
                isInserted = true;
            }
        }
        return isInserted;
    }

    private boolean addSpendingToDatabase(String spendingRemark, int spendingPrice, int spendingReceiver){
        boolean isInserted = false;
        Person host = database.getPersonByIC(CalCollectorVariables.HOST_DIGIT);
        Account newSpendingAccount = database.existedAccount(CalCollectorVariables.ACCOUNT_SPEND);
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

        if(newSpendingAccount == null){
            newSpendingAccount = new Account();
            newSpendingAccount.setType(CalCollectorVariables.ACCOUNT_SPEND);
            newSpendingAccount.setOwner(hostID);
            newSpendingAccount.setStatus(CalCollectorVariables.ACCOUNT_HEADER_STATUS_PAID);

            AccountLine newSpendingAccountLine = new AccountLine();
            newSpendingAccountLine.setAmount(spendingPrice);
            newSpendingAccountLine.setRemark(spendingRemark + CalCollectorVariables.RECEIVER_REMARK_DELIMITER + spendingReceiver);
            newSpendingAccountLine.setStatus(CalCollectorVariables.ACCOUNT_LINE_STATUS_PAID);
            newSpendingAccountLine.setType(CalCollectorVariables.ACCOUNT_LINE_TYPE_DEBIT);

            isInserted = database.addAccountAndAccountLine(newSpendingAccount, newSpendingAccountLine);
        }
        else{
            AccountLine newSpendingAccountLine = new AccountLine();
            newSpendingAccountLine.setAccount_header_id(newSpendingAccount.getId());
            newSpendingAccountLine.setAmount(spendingPrice);
            newSpendingAccountLine.setRemark(spendingRemark + CalCollectorVariables.RECEIVER_REMARK_DELIMITER + spendingReceiver);
            newSpendingAccountLine.setStatus(CalCollectorVariables.ACCOUNT_LINE_STATUS_PAID);
            newSpendingAccountLine.setType(CalCollectorVariables.ACCOUNT_LINE_TYPE_DEBIT);

            long newSpendingAccountLineID = database.addAccountLine(newSpendingAccountLine);
            if(newSpendingAccountLineID > 0){
                isInserted = true;
            }
        }

        return isInserted;
    }

    private boolean addStandToDatabase(String standRemark, int standAmount, int standReceiver){
        boolean isInserted = false;
        Person host = database.getPersonByIC(CalCollectorVariables.HOST_DIGIT);
        Account newStandAccount = database.existedAccount(CalCollectorVariables.ACCOUNT_STAND);
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

        if(newStandAccount == null){
            newStandAccount = new Account();
            newStandAccount.setType(CalCollectorVariables.ACCOUNT_STAND);
            newStandAccount.setOwner(hostID);
            newStandAccount.setStatus(CalCollectorVariables.ACCOUNT_HEADER_STATUS_PAID);

            AccountLine newStandAccountLine = new AccountLine();
            newStandAccountLine.setAmount(standAmount);
            newStandAccountLine.setRemark(standRemark + CalCollectorVariables.RECEIVER_REMARK_DELIMITER + standReceiver);
            newStandAccountLine.setStatus(CalCollectorVariables.ACCOUNT_LINE_STATUS_UNPAID);
            newStandAccountLine.setType(CalCollectorVariables.ACCOUNT_LINE_TYPE_DEBIT);

            isInserted = database.addAccountAndAccountLine(newStandAccount, newStandAccountLine);
        }
        else{
            AccountLine newStandAccountLine = new AccountLine();
            newStandAccountLine.setAccount_header_id(newStandAccount.getId());
            newStandAccountLine.setAmount(standAmount);
            newStandAccountLine.setRemark(standRemark + CalCollectorVariables.RECEIVER_REMARK_DELIMITER + standReceiver);
            newStandAccountLine.setStatus(CalCollectorVariables.ACCOUNT_LINE_STATUS_UNPAID);
            newStandAccountLine.setType(CalCollectorVariables.ACCOUNT_LINE_TYPE_DEBIT);

            long newSpendingAccountLineID = database.addAccountLine(newStandAccountLine);
            if(newSpendingAccountLineID > 0){
                isInserted = true;
            }
        }

        return isInserted;
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

    private void errorToast(){
        String errorMessageString = "Error\n";
        if(arrayListErrorMessage.size() > 0){
            errorMessageString += TextUtils.join("\n", arrayListErrorMessage.toArray());
        }
        Toast.makeText(getApplication(), errorMessageString, Toast.LENGTH_SHORT).show();
    }
}
