package com.nocompany.calcollector;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class AccountDetailsActivity extends ActionBarActivity {

    private LinearLayout linearlayout_account_amount;
    private LinearLayout linearlayout_account_disbursement;
    private LinearLayout linearlayout_account_installment;
    private LinearLayout linearlayout_account_length;

    private TextView textview_account_owner_name;
    private TextView textview_account_amount;
    private TextView textview_account_disbursment;
    private TextView textview_account_intallment;
    private TextView textview_account_length;

    private LinearLayout linearlayout_indicator_unpaid;
    private LinearLayout linearlayout_indicator_oncredit;

    private TextView textview_account_in;
    private TextView textview_account_out;
    private TextView textview_account_total;

    private DatabaseHandler database;
    private int accountId;
    private String owner;
    private Account account;
    private int accountType;
    private String lengthUnit;
    private Date accountCreatedDate;
    private ArrayList<Integer> amounts;
    private ArrayList<Integer> disbursements;
    private ArrayList<Date> paidDates;
    private ArrayList<Date> unpaidDates;
    private ArrayList<Date> onCreditDates;

    private int amountReceived;
    private int amountPaid;
    private int amountBalance;

    private int totalAllBalance;

    private CaldroidFragment caldroidFragment;
    private ArrayList<String> arrayListErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            accountId = extras.getInt(CalCollectorVariables.INTENT_ACCOUNT_ID);
            owner =  extras.getString(CalCollectorVariables.INTENT_USER_NAME);
        }

        setupLayoutVariable();
        setupDatabaseAndProperties();
        setupLayoutFunction();
        setupCalendar(savedInstanceState);
    }

    private void setupLayoutVariable() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.dashboard_2_todo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        linearlayout_account_amount =  (LinearLayout)findViewById(R.id.linearlayout_account_amount);
        linearlayout_account_disbursement =  (LinearLayout)findViewById(R.id.linearlayout_account_disbursement);
        linearlayout_account_installment =  (LinearLayout)findViewById(R.id.linearlayout_account_installment);
        linearlayout_account_length =  (LinearLayout)findViewById(R.id.linearlayout_account_length);

        textview_account_owner_name =  (TextView)findViewById(R.id.textview_account_owner_name);
        textview_account_amount =  (TextView)findViewById(R.id.textview_account_amount);
        textview_account_disbursment =  (TextView)findViewById(R.id.textview_account_disbursment);
        textview_account_intallment =  (TextView)findViewById(R.id.textview_account_intallment);
        textview_account_length =  (TextView)findViewById(R.id.textview_account_length);

        linearlayout_indicator_unpaid = (LinearLayout)findViewById(R.id.linearlayout_indicator_unpaid);
        linearlayout_indicator_oncredit = (LinearLayout)findViewById(R.id.linearlayout_indicator_oncredit);

        textview_account_in =  (TextView)findViewById(R.id.textview_account_in);
        textview_account_out =  (TextView)findViewById(R.id.textview_account_out);
        textview_account_total =  (TextView)findViewById(R.id.textview_account_total);
    }

    private void setupDatabaseAndProperties() {
        database = new DatabaseHandler(this);
        account = database.getAccountByID(accountId);
        accountCreatedDate = formatStringToDate(account.getCreated_date());
        setAccountLineInfo();
    }

    private void setAccountLineInfo(){
        ArrayList<AccountLine> accountLines = (ArrayList<AccountLine>)database.getAccountLineByHeader(account.getId());
        amounts = new ArrayList<Integer>();
        disbursements = new ArrayList<Integer>();
        paidDates = new ArrayList<Date>();
        unpaidDates = new ArrayList<Date>();
        onCreditDates = new ArrayList<Date>();
        if(accountLines.size() > 0){
            for(AccountLine accountLine: accountLines){
                if(accountLine.getType() == CalCollectorVariables.ACCOUNT_LINE_TYPE_DEBIT){
                    amounts.add(accountLine.getAmount());
                    onCreditDates.add(formatStringToDate(accountLine.getCreated_date()));
                }
                else if(accountLine.getType() == CalCollectorVariables.ACCOUNT_LINE_TYPE_DISBURSEMENT){
                    disbursements.add(accountLine.getAmount());
                    amountReceived += accountLine.getAmount();
                }
                else if(accountLine.getType() == CalCollectorVariables.ACCOUNT_LINE_TYPE_CREDIT){
                    if(accountLine.getTarget_date() != null || accountLine.getTarget_date().trim().length() > 0){
                        paidDates.add(formatStringToDate(accountLine.getTarget_date()));
                    }
                    amountPaid += accountLine.getAmount();
                }
            }
            amountBalance = amountReceived - amountPaid;
        }

        accountType = account.getType();
        int intervalType = Calendar.DATE;
        int interval = account.getPaid_length() > 0 ? account.getPaid_length() + 1 : 1;
        if(accountType == CalCollectorVariables.ACCOUNT_DAY_FIXED
                || accountType ==  CalCollectorVariables.ACCOUNT_DAY_FLEX){
            intervalType = Calendar.DATE;
            lengthUnit = "d";
        }
        else if(accountType == CalCollectorVariables.ACCOUNT_WEEK_FIXED
                || accountType ==  CalCollectorVariables.ACCOUNT_WEEK_FLEX){
            intervalType = Calendar.WEEK_OF_YEAR;
            lengthUnit = "wk";
        }
        else if(accountType == CalCollectorVariables.ACCOUNT_MONTH_FIXED
                || accountType ==  CalCollectorVariables.ACCOUNT_MONTH_FLEX){
            intervalType = Calendar.MONTH;
            lengthUnit = "mo";
        }

        if(account.getStatus() == CalCollectorVariables.ACCOUNT_HEADER_STATUS_UNPAID){
            Date lastOnCdreditDate = onCreditDates.get(onCreditDates.size()-1);
            Calendar cal = Calendar.getInstance();
            cal.setTime(lastOnCdreditDate);
            if(account.getInstallment_length() > 0){
                cal.add(intervalType, interval);
                for(int i=0; i<account.getInstallment_length() - account.getPaid_length(); i++){
                    unpaidDates.add(cal.getTime());
                    cal.add(intervalType, 1);
                }
            }
            else{
                cal.add(intervalType, interval);
                unpaidDates.add(cal.getTime());
            }
        }
    }

    private void setupLayoutFunction() {
        textview_account_owner_name.setText(owner);
        if(amounts.size() > 0){
            int amountTotal = 0;
            for(int amount : amounts){
                amountTotal += amount;
            }
            textview_account_amount.setText(String.format("RM%7s", amountTotal));
        }
        else{
            linearlayout_account_amount.setVisibility(View.INVISIBLE);
        }

        if(disbursements.size() > 0){
            int disbursementTotal = 0;
            for(int disbursement : disbursements){
                disbursementTotal += disbursement;
            }
            textview_account_disbursment.setText(String.format("RM%7s", disbursementTotal));
        }
        else{
            linearlayout_account_disbursement.setVisibility(View.INVISIBLE);
        }

        if(account.getInterest() > 0){
            textview_account_intallment.setText(String.format("RM%7s", account.getInterest()));
        }
        else{
            linearlayout_account_installment.setVisibility(View.INVISIBLE);
        }

        if(account.getInstallment_length() > 0){
            textview_account_length.setText(account.getInstallment_length() + lengthUnit);
        }
        else{
            linearlayout_account_length.setVisibility(View.INVISIBLE);
            linearlayout_indicator_unpaid.setVisibility(View.INVISIBLE);
            linearlayout_indicator_oncredit.setVisibility(View.INVISIBLE);
        }

        textview_account_in.setText(String.format("RM%7s", amountReceived));
        textview_account_out.setText(String.format("RM%7s", amountPaid));
        textview_account_total.setText(String.format("RM%7s", amountBalance));

    }

    private void setupCalendar(Bundle savedInstanceState) {
        caldroidFragment = new CaldroidFragment();
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState, "CALDROID_SAVED_STATE");
        }
        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
            caldroidFragment.setArguments(args);
        }
        setCustomResourceForDates();
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.cal, caldroidFragment);
        t.commit();

        final CaldroidListener listener = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
            }

            @Override
            public void onLongClickDate(Date date, View view) {
                if(unpaidDates.size() > 0){
                    Calendar nextPaidDate = formatDateToCalendar(unpaidDates.get(0));
                    Calendar selectedDate = formatDateToCalendar(date);
                    if(nextPaidDate.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR)
                            && nextPaidDate.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH)
                            && nextPaidDate.get(Calendar.DATE) == selectedDate.get(Calendar.DATE)){
                        openDialog(date, false).show();
                    }
                }

            }
        };
        caldroidFragment.setCaldroidListener(listener);
    }

    private void setCustomResourceForDates() {
        if(caldroidFragment != null){
            if(paidDates.size() > 0){
                for(Date paidDate : paidDates){
                    caldroidFragment.setBackgroundResourceForDate(R.color.account_paid, paidDate);
                    caldroidFragment.setTextColorForDate(R.color.white, paidDate);
                }
            }
            if(onCreditDates.size() > 0){
                for(Date oncreditDate : onCreditDates){
                    caldroidFragment.setBackgroundResourceForDate(R.color.account_oncredit, oncreditDate);
                }
            }
            caldroidFragment.setBackgroundResourceForDate(R.color.account_created, accountCreatedDate);
            caldroidFragment.setTextColorForDate(R.color.white, accountCreatedDate);

            if(unpaidDates.size() > 0){
                for(Date unpaidDate : unpaidDates){
                    caldroidFragment.setBackgroundResourceForDate(R.color.account_unpaid, unpaidDate);
                    caldroidFragment.setTextColorForDate(R.color.white, unpaidDate);
                }
                caldroidFragment.setBackgroundResourceForDate(R.color.account_next_unpaid, unpaidDates.get(0));
                caldroidFragment.setTextColorForDate(R.color.white, unpaidDates.get(0));
            }
            caldroidFragment.refreshView();
        }
    }

    private Dialog openDialog(final Date date, final boolean isFullPayment){
        LayoutInflater layout_inflator = LayoutInflater.from(this);
        final View view = layout_inflator.inflate(R.layout.dialog_add_money, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final TextView textview_add_amount = (TextView)view.findViewById(R.id.textview_add_amount);
        final EditText edittext_add_amount = (EditText)view.findViewById(R.id.edittext_add_amount);
        final TextView textview_add_amount_validator = (TextView)view.findViewById(R.id.textview_add_amount_validator);
        int amountTotal = 0;
        CharSequence title;
        if(isFullPayment){
            if(amounts.size() > 0){
                for(int amount : amounts){
                    amountTotal += amount;
                }
            }
            title = getResources().getText(R.string.dialog_full_payment);
        }
        else{
            amountTotal = account.getInterest();
            title = getResources().getText(R.string.dialog_add_money_title);
        }
        textview_add_amount.setText(title);
        edittext_add_amount.setHint(String.valueOf(amountTotal));
        builder.setPositiveButton(R.string.dialog_positive, null);
        builder.setNegativeButton(R.string.dialog_negative, null);
        final AlertDialog alertDialog = builder.create();
        final int finalAmountTotal = amountTotal;
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button_positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button_positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int installmentAmount = parseStringToInt(String.valueOf(edittext_add_amount.getText()));
                        if(validateMoneyAmount(installmentAmount)){
                            if(installmentAmount < finalAmountTotal){
                                Toast.makeText(getApplication(), "Key in amount is less than total amount user should pay!", Toast.LENGTH_SHORT).show();
                                textview_add_amount_validator.setBackgroundResource(R.color.not_valid);
                            }
                            else{
                                textview_add_amount_validator.setBackground(null);
                                if(addMoneyToDatabase(installmentAmount, date, isFullPayment)){
                                    Toast.makeText(getApplication(), "Money is successfully paid.", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(getIntent());
                                    alertDialog.dismiss();
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

    private int parseStringToInt(String intString){
        int strInt = 0;
        try{
            strInt = Integer.parseInt(intString);
        }
        catch(NumberFormatException ex){

        }
        return strInt;
    }

    private boolean validateMoneyAmount(int payAmount) {
        boolean isValid = false;
        if(payAmount > 0){
            isValid = true;
        }
        return isValid;
    }

    private boolean addMoneyToDatabase(int payAmount, Date date, boolean isFullPayment){
        boolean isInserted;
        AccountLine newPayAccountLine = new AccountLine();
        newPayAccountLine.setAccount_header_id(account.getId());
        newPayAccountLine.setAmount(payAmount);
        newPayAccountLine.setStatus(CalCollectorVariables.ACCOUNT_LINE_STATUS_PAID);
        newPayAccountLine.setType(CalCollectorVariables.ACCOUNT_LINE_TYPE_CREDIT);
        newPayAccountLine.setTarget_date(getDateTime(date));

        if(isFullPayment){
            isInserted = database.addFullPaymentToDatabase(newPayAccountLine);
        }
        else{
            isInserted = database.addInstallmentToDatabase(newPayAccountLine);
        }
        return isInserted;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (caldroidFragment != null) {
            caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }
    }

    private Date formatStringToDate(String dateTimeString){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateTime = null;
        try{
            dateTime = sdf.parse(dateTimeString);
        }
        catch(ParseException ex){

        }
        return dateTime;
    }

    private Calendar formatDateToCalendar(Date dateTime){
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(dateTime);
        return dateCalendar;
    }

    private String getDateTime(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dateAndTime = sdf.format(date);
        return dateAndTime;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(account.getStatus() == CalCollectorVariables.ACCOUNT_HEADER_STATUS_UNPAID){
            getMenuInflater().inflate(R.menu.menu_account_details, menu);
            if(accountType == CalCollectorVariables.ACCOUNT_DAY_FLEX
                    || accountType ==  CalCollectorVariables.ACCOUNT_WEEK_FLEX
                    || accountType == CalCollectorVariables.ACCOUNT_MONTH_FLEX){
                menu.findItem(R.id.action_account_oncredit).setVisible(true);
            }
        }
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_pay_all) {
            openDialog(new Date(), true).show();
            return true;
        }
        else if(id ==  R.id.action_account_oncredit){
            openDialog().show();
            return true;
        }
        else if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private Dialog openDialog(){
        LayoutInflater layout_inflator = LayoutInflater.from(this);
        final View view = layout_inflator.inflate(R.layout.dialog_on_credit, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final EditText edittext_account_amount = (EditText)view.findViewById(R.id.edittext_account_amount);
        final EditText edittext_account_disbursement = (EditText)view.findViewById(R.id.edittext_account_disbursement);
        final EditText edittext_account_installment = (EditText)view.findViewById(R.id.edittext_account_installment);
        final EditText edittext_account_length = (EditText)view.findViewById(R.id.edittext_account_length);
        final TextView textview_account_amount_validator = (TextView)view.findViewById(R.id.textview_account_amount_validator);
        final TextView textview_account_disbursement_validator = (TextView)view.findViewById(R.id.textview_account_disbursement_validator);
        final TextView textview_account_installment_validator = (TextView)view.findViewById(R.id.textview_account_installment_validator);
        final TextView textview_account_length_validator = (TextView)view.findViewById(R.id.textview_account_length_validator);
        final TextView textview_account_length_unit = (TextView)view.findViewById(R.id.textview_account_length_unit);
        if(accountType == CalCollectorVariables.ACCOUNT_DAY_FLEX){
            textview_account_length_unit.setText("Day(s)");
        }
        else if(accountType == CalCollectorVariables.ACCOUNT_WEEK_FLEX){
            textview_account_length_unit.setText("Week(s)");
        }
        else if(accountType == CalCollectorVariables.ACCOUNT_MONTH_FLEX){
            textview_account_length_unit.setText("Month(s)");
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
                        boolean isAmountValid = false, isDisbursementValid = false, isInstallmentValid = false, isLengthValid = false;
                        int amount = parseStringToInt(String.valueOf(edittext_account_amount.getText()).trim());
                        int disbursement = parseStringToInt(String.valueOf(edittext_account_disbursement.getText()).trim());
                        int installment = parseStringToInt(String.valueOf(edittext_account_installment.getText()).trim());
                        int length = parseStringToInt(String.valueOf(edittext_account_length.getText()).trim());
                        arrayListErrorMessage = new ArrayList<String>();

                        if(amount <= 0){
                            textview_account_amount_validator.setBackgroundResource(R.color.not_valid);
                            arrayListErrorMessage.add("- Invalid amount.");
                        }
                        else{
                            textview_account_amount_validator.setBackground(null);
                            isAmountValid = true;
                        }

                        if(disbursement <= 0){
                            textview_account_disbursement_validator.setBackgroundResource(R.color.not_valid);
                            arrayListErrorMessage.add("- Disbursement is not valid.");
                        }
                        else{
                            textview_account_disbursement_validator.setBackground(null);
                            isDisbursementValid = true;
                        }

                        if(installment <= 0){
                            textview_account_installment_validator.setBackgroundResource(R.color.not_valid);
                            arrayListErrorMessage.add("- Installment is not valid.");
                        }
                        else{
                            textview_account_installment_validator.setBackground(null);
                            isInstallmentValid = true;
                        }

                        if(length <= 0){
                            textview_account_length_validator.setBackgroundResource(R.color.not_valid);
                            arrayListErrorMessage.add("- Duration length must be at least 1.");
                        }
                        else{
                            textview_account_length_validator.setBackground(null);
                            isLengthValid = true;
                        }

                        if(isAmountValid && isDisbursementValid && isInstallmentValid && isLengthValid) {
                            if(checkBalance(disbursement)){
                                account.setInterest(installment);
                                account.setInstallment_length(length);
                                account.setPaid_length(0);

                                AccountLine newAccountLineAmount = new AccountLine();
                                newAccountLineAmount.setAmount(amount);
                                newAccountLineAmount.setStatus(CalCollectorVariables.ACCOUNT_LINE_STATUS_PAID);
                                newAccountLineAmount.setType(CalCollectorVariables.ACCOUNT_LINE_TYPE_DEBIT);

                                AccountLine newAccountLineDisbursement = new AccountLine();
                                newAccountLineDisbursement.setAmount(disbursement);
                                newAccountLineDisbursement.setStatus(CalCollectorVariables.ACCOUNT_LINE_STATUS_PAID);
                                newAccountLineDisbursement.setType(CalCollectorVariables.ACCOUNT_LINE_TYPE_DISBURSEMENT);

                                boolean isSuccess = database.addOnCreditToAccount(account, newAccountLineAmount, newAccountLineDisbursement);
                                if(isSuccess){
                                    Toast.makeText(getApplication(), "Account is successfully On-Credited.", Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                    finish();
                                    startActivity(getIntent());
                                }
                            }
                            else{
                                textview_account_disbursement_validator.setBackgroundResource(R.color.not_valid);
                                arrayListErrorMessage.add("No enough money for disbursement!\nAccount Balance: RM " + totalAllBalance);
                                errorToast();
                            }
                        }
                        else{
                            errorToast();
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
