package com.nocompany.calcollector;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TodoActivity extends ActionBarActivity {

    private LinearLayout linearlayout_todo_delay_header;
    private ImageView imageview_todo_delay_toggle;
    private LinearLayout linearlayout_todo_today_header;
    private ImageView imageview_todo_today_toggle;
    private LinearLayout linearlayout_todo_tomorrow_header;
    private ImageView imageview_todo_tomorrow_toggle;
    private LinearLayout linearlayout_todo_delay;
    private LinearLayout linearlayout_todo_today;
    private LinearLayout linearlayout_todo_tomorrow;
    private LinearLayout linearlayout_todo_delay_no_record;
    private LinearLayout linearlayout_todo_today_no_record;
    private LinearLayout linearlayout_todo_tomorrow_no_record;
    private LinearLayout linearlayout_todo_delay_record;
    private LinearLayout linearlayout_todo_today_record;
    private LinearLayout linearlayout_todo_tomorrow_record;
    private LinearLayout linearlayout_todo_delay_list;
    private LinearLayout linearlayout_todo_today_list;
    private LinearLayout linearlayout_todo_tomorrow_list;
    private DatabaseHandler database;
    private ArrayList<ContentValues> accountList;
    private ArrayList<ContentValues> accountDelay;
    private ArrayList<ContentValues> accountToday;
    private ArrayList<ContentValues> accountTomorrow;
    private boolean linearlayout_todo_delay_isExpand;
    private boolean linearlayout_todo_today_isExpand;
    private boolean linearlayout_todo_tomorrow_isExpand;
    private Animation slideUp;
    private Animation slideDown;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        getSessionProperties();
        setupLayoutVariable();
        setupLayoutFunction();
    }

    private void setupLayoutVariable() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.actionbar_2_todo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        linearlayout_todo_delay_header = (LinearLayout)findViewById(R.id.linearlayout_todo_delay_header);
        imageview_todo_delay_toggle = (ImageView)findViewById(R.id.imageview_todo_delay_toggle);
        linearlayout_todo_today_header = (LinearLayout)findViewById(R.id.linearlayout_todo_today_header);
        imageview_todo_today_toggle = (ImageView)findViewById(R.id.imageview_todo_today_toggle);
        linearlayout_todo_tomorrow_header = (LinearLayout)findViewById(R.id.linearlayout_todo_tomorrow_header);
        imageview_todo_tomorrow_toggle = (ImageView)findViewById(R.id.imageview_todo_tomorrow_toggle);

        linearlayout_todo_delay = (LinearLayout)findViewById(R.id.linearlayout_todo_delay);
        linearlayout_todo_today = (LinearLayout)findViewById(R.id.linearlayout_todo_today);
        linearlayout_todo_tomorrow = (LinearLayout)findViewById(R.id.linearlayout_todo_tomorrow);
        linearlayout_todo_delay_no_record = (LinearLayout)findViewById(R.id.linearlayout_todo_delay_no_record);
        linearlayout_todo_today_no_record = (LinearLayout)findViewById(R.id.linearlayout_todo_today_no_record);
        linearlayout_todo_tomorrow_no_record = (LinearLayout)findViewById(R.id.linearlayout_todo_tomorrow_no_record);
        linearlayout_todo_delay_record = (LinearLayout)findViewById(R.id.linearlayout_todo_delay_record);
        linearlayout_todo_today_record = (LinearLayout)findViewById(R.id.linearlayout_todo_today_record);
        linearlayout_todo_tomorrow_record = (LinearLayout)findViewById(R.id.linearlayout_todo_tomorrow_record);
        linearlayout_todo_delay_list = (LinearLayout)findViewById(R.id.linearlayout_todo_delay_list);
        linearlayout_todo_today_list = (LinearLayout)findViewById(R.id.linearlayout_todo_today_list);
        linearlayout_todo_tomorrow_list = (LinearLayout)findViewById(R.id.linearlayout_todo_tomorrow_list);
        slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
    }

    private void setupLayoutFunction() {
        linearlayout_todo_delay_header.setOnClickListener(onClickListener);
        linearlayout_todo_today_header.setOnClickListener(onClickListener);
        linearlayout_todo_tomorrow_header.setOnClickListener(onClickListener);
    }

    private void getSessionProperties() {
        session = new SessionManager(getApplicationContext());
        if(!session.isLoggedIn()){
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(i);
            finish();
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.bringToFront();
            switch(v.getId()){
                case R.id.linearlayout_todo_delay_header:
                    if(!linearlayout_todo_delay_isExpand){
                        imageview_todo_delay_toggle.setImageResource(R.drawable.todo_collapse);
                        imageview_todo_today_toggle.setImageResource(R.drawable.todo_expand);
                        imageview_todo_tomorrow_toggle.setImageResource(R.drawable.todo_expand);
                        slideDown.setAnimationListener(new MyAnimationListener(linearlayout_todo_delay, true));
                        linearlayout_todo_delay.startAnimation(slideDown);
                        linearlayout_todo_today.setVisibility(View.GONE);
                        linearlayout_todo_tomorrow.setVisibility(View.GONE);
                        linearlayout_todo_delay_isExpand = true;
                        linearlayout_todo_today_isExpand =  false;
                        linearlayout_todo_tomorrow_isExpand = false;
                    }
                    else{
                        imageview_todo_delay_toggle.setImageResource(R.drawable.todo_expand);
                        slideUp.setAnimationListener(new MyAnimationListener(linearlayout_todo_delay, false));
                        linearlayout_todo_delay.startAnimation(slideUp);
                        linearlayout_todo_delay_isExpand = false;
                    }

                    break;
                case R.id.linearlayout_todo_today_header:
                    if(!linearlayout_todo_today_isExpand){
                        imageview_todo_delay_toggle.setImageResource(R.drawable.todo_expand);
                        imageview_todo_today_toggle.setImageResource(R.drawable.todo_collapse);
                        imageview_todo_tomorrow_toggle.setImageResource(R.drawable.todo_expand);
                        slideDown.setAnimationListener(new MyAnimationListener(linearlayout_todo_today, true));
                        linearlayout_todo_today.startAnimation(slideDown);
                        linearlayout_todo_delay.setVisibility(View.GONE);
                        linearlayout_todo_tomorrow.setVisibility(View.GONE);
                        linearlayout_todo_delay_isExpand = false;
                        linearlayout_todo_today_isExpand =  true;
                        linearlayout_todo_tomorrow_isExpand = false;
                    }
                    else{
                        imageview_todo_today_toggle.setImageResource(R.drawable.todo_expand);
                        slideUp.setAnimationListener(new MyAnimationListener(linearlayout_todo_today, false));
                        linearlayout_todo_today.startAnimation(slideUp);
                        linearlayout_todo_delay_isExpand = false;
                        linearlayout_todo_today_isExpand = false;
                        linearlayout_todo_tomorrow_isExpand = false;
                    }

                    break;
                case R.id.linearlayout_todo_tomorrow_header:
                    if(!linearlayout_todo_tomorrow_isExpand){
                        imageview_todo_delay_toggle.setImageResource(R.drawable.todo_expand);
                        imageview_todo_delay_toggle.setImageResource(R.drawable.todo_expand);
                        imageview_todo_tomorrow_toggle.setImageResource(R.drawable.todo_collapse);
                        slideDown.setAnimationListener(new MyAnimationListener(linearlayout_todo_tomorrow, true));
                        linearlayout_todo_tomorrow.startAnimation(slideDown);
                        linearlayout_todo_delay.setVisibility(View.GONE);
                        linearlayout_todo_today.setVisibility(View.GONE);
                        linearlayout_todo_delay_isExpand = false;
                        linearlayout_todo_today_isExpand =  false;
                        linearlayout_todo_tomorrow_isExpand = true;
                    }
                    else{
                        imageview_todo_tomorrow_toggle.setImageResource(R.drawable.todo_expand);
                        slideUp.setAnimationListener(new MyAnimationListener(linearlayout_todo_tomorrow, false));
                        linearlayout_todo_tomorrow.startAnimation(slideUp);
                        linearlayout_todo_delay_isExpand = false;
                        linearlayout_todo_today_isExpand =  false;
                        linearlayout_todo_tomorrow_isExpand = false;
                    }
                    break;
            }
        }
    };

    private class MyAnimationListener implements Animation.AnimationListener{
        boolean isSlideDown;
        LinearLayout linearlayout;
        MyAnimationListener(LinearLayout linearlayout, boolean isSlideDown) {
            this.linearlayout = linearlayout;
            this.isSlideDown = isSlideDown;
        }

        @Override
        public void onAnimationStart(Animation animation) {
            if(isSlideDown){
                linearlayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if(!isSlideDown){
                linearlayout.setVisibility(View.GONE);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupDatabaseAndProperties();
        setupAccountListProperties();
    }

    private void setupDatabaseAndProperties() {
        database = new DatabaseHandler(this);
        accountList = (ArrayList<ContentValues>)database.getToDoAccountSummary();
        setupTODOList();
    }

    private void setupTODOList(){
        accountDelay = new ArrayList<ContentValues>();
        accountToday = new ArrayList<ContentValues>();
        accountTomorrow = new ArrayList<ContentValues>();

        if(accountList.size() > 0){
            for(ContentValues account : accountList){
                Calendar cal = Calendar.getInstance();
                cal.setTime(formatStringToDate(account.getAsString(AccountLine.TableAccountLine.COLUMN_NAME_CREATED_AT)));
                int accountType = account.getAsInteger(Account.TableAccount.COLUMN_NAME_TYPE);
                int intervalType = Calendar.DATE;
                int interval = account.getAsInteger(Account.TableAccount.COLUMN_NAME_PAID_LENGTH) > 0 ? account.getAsInteger(Account.TableAccount.COLUMN_NAME_PAID_LENGTH) + 1 : 1;
                if(accountType == CalCollectorVariables.ACCOUNT_DAY_FIXED || accountType ==  CalCollectorVariables.ACCOUNT_DAY_FLEX){
                    intervalType = Calendar.DATE;
                }
                else if(accountType == CalCollectorVariables.ACCOUNT_WEEK_FIXED || accountType ==  CalCollectorVariables.ACCOUNT_WEEK_FLEX){
                    intervalType = Calendar.WEEK_OF_YEAR;
                }
                else if(accountType == CalCollectorVariables.ACCOUNT_MONTH_FIXED || accountType ==  CalCollectorVariables.ACCOUNT_MONTH_FLEX){
                    intervalType = Calendar.MONTH;
                }

                cal.add(intervalType, interval);
                if(isBeforeToday(cal)){
                    accountDelay.add(account);
                }
                else if(isToday(cal)){
                    accountToday.add(account);
                }
                else if(isTomorrow(cal)){
                    accountTomorrow.add(account);
                }
            }
        }
    }
    private boolean isBeforeToday(Calendar cal) {
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date dateSpecified = cal.getTime();
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        return (dateSpecified.before(today.getTime()));
    }
    private boolean isToday(Calendar cal) {
        Calendar today = Calendar.getInstance();
        return (cal.get(Calendar.ERA) == today.get(Calendar.ERA) &&
                cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR));
    }
    private boolean isTomorrow(Calendar cal) {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        return (cal.get(Calendar.ERA) == tomorrow.get(Calendar.ERA) &&
                cal.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR) &&
                cal.get(Calendar.DAY_OF_YEAR) == tomorrow.get(Calendar.DAY_OF_YEAR));
    }

    private void setupAccountListProperties() {
        if(accountDelay.size() > 0){
            linearlayout_todo_delay_no_record.setVisibility(View.GONE);
            linearlayout_todo_delay_record.setVisibility(View.VISIBLE);
            buildTodoList(accountDelay, linearlayout_todo_delay_list);
        }
        else{
            linearlayout_todo_delay_no_record.setVisibility(View.VISIBLE);
            linearlayout_todo_delay_record.setVisibility(View.GONE);
        }
        if(accountToday.size() > 0){
            linearlayout_todo_today_no_record.setVisibility(View.GONE);
            linearlayout_todo_today_record.setVisibility(View.VISIBLE);
            buildTodoList(accountToday, linearlayout_todo_today_list);
        }
        else{
            linearlayout_todo_today_no_record.setVisibility(View.VISIBLE);
            linearlayout_todo_today_record.setVisibility(View.GONE);
        }
        if(accountTomorrow.size() > 0){
            linearlayout_todo_tomorrow_no_record.setVisibility(View.GONE);
            linearlayout_todo_tomorrow_record.setVisibility(View.VISIBLE);
            buildTodoList(accountTomorrow, linearlayout_todo_tomorrow_list);
        }
        else{
            linearlayout_todo_tomorrow_no_record.setVisibility(View.VISIBLE);
            linearlayout_todo_tomorrow_record.setVisibility(View.GONE);
        }
    }

    private void buildTodoList(ArrayList<ContentValues> accounts, LinearLayout linearlayout){
        linearlayout.removeAllViews();
        LayoutInflater layout_inflator = LayoutInflater.from(this);
        LinearLayout linearlayout_todolist_id;
        TextView line_no;
        ImageView imageview_todolist_type_1;
        ImageView imageview_todolist_type_2;
        TextView textview_todolist_username;
        TextView textview_todolist_installment;

        int counter = 1;
        for(ContentValues account : accounts){
            View view = layout_inflator.inflate(R.layout.todos_view, null, false);
            linearlayout_todolist_id = (LinearLayout)view.findViewById(R.id.linearlayout_todolist_id);
            line_no = (TextView)view.findViewById(R.id.line_no);
            imageview_todolist_type_1 = (ImageView)view.findViewById(R.id.imageview_todolist_type_1);
            imageview_todolist_type_2 = (ImageView)view.findViewById(R.id.imageview_todolist_type_2);
            textview_todolist_username = (TextView)view.findViewById(R.id.textview_todolist_username);
            textview_todolist_installment = (TextView)view.findViewById(R.id.textview_todolist_installment);

            linearlayout_todolist_id.setOnClickListener(new AccountOnClickListener(account.getAsInteger(Account.TableAccount.COLUMN_NAME_ID), account.getAsString(Person.TablePerson.COLUMN_NAME_USERNAME)));
            line_no.setText(String.valueOf(counter));
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
                    imageview_todolist_type_1.setVisibility(View.INVISIBLE);
                    imageview_todolist_type_2.setVisibility(View.INVISIBLE);
                    break;
            }
            imageview_todolist_type_1.setImageResource(resId_type_1);
            imageview_todolist_type_2.setImageResource(resId_type_2);
            textview_todolist_username.setText(account.getAsString(Person.TablePerson.COLUMN_NAME_USERNAME));
            textview_todolist_installment.setText(String.format(getString(R.string.textview_money), account.getAsInteger(Account.TableAccount.COLUMN_NAME_INTEREST)));

            linearlayout.addView(view);
            ImageView divider = new ImageView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 10);
            divider.setLayoutParams(lp);
            linearlayout.addView(divider);
            counter++;
        }
    }

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

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
