package com.nocompany.calcollector;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends ActionBarActivity {

    private Display display;
    private int screenOrientation;

    private LinearLayout linearlayout_deposit = null;
    private LinearLayout linearlayout_todo = null;
    private GridView gridview = null;
    private LinearLayout linearlayout_user = null;
    private LinearLayout linearlayout_stand = null;
    private TextView textview_deposit = null;

    private SessionManager session;
    private DatabaseHandler database;
    private ArrayList<ContentValues> accountList;

    NotificationMessage notification = new NotificationMessage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupLayoutVariable();
        setupLayoutFunction();
        getSessionProperties();
    }

    private void setupLayoutVariable() {
        display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        linearlayout_deposit = (LinearLayout) findViewById(R.id.linearlayout_deposit);
        linearlayout_todo = (LinearLayout) findViewById(R.id.linearlayout_todo);
        gridview = (GridView) findViewById(R.id.gridview);
        linearlayout_user = (LinearLayout) findViewById(R.id.linearlayout_user);
        linearlayout_stand = (LinearLayout) findViewById(R.id.linearlayout_stand);
        textview_deposit = (TextView) findViewById(R.id.textview_deposit);
        setLayoutWithOrientation();
    }

    private void setLayoutWithOrientation() {
        ViewGroup.LayoutParams layoutParams = gridview.getLayoutParams();
        screenOrientation = display.getRotation();
        if(gridview.getHeight() > 0){
            if((screenOrientation == Surface.ROTATION_0) || (screenOrientation == Surface.ROTATION_180)){
                gridview.setNumColumns(3);
            }
            else{
                gridview.setNumColumns(6);
            }
            gridview.setLayoutParams(layoutParams);
        }
    }

    private void setupLayoutFunction() {
        linearlayout_deposit.setOnClickListener(onclicklistener);
        linearlayout_todo.setOnClickListener(onclicklistener);
        gridview.setAdapter(new GridViewImageAdapter(this));
        gridview.setOnItemClickListener(adapterview_onclicklistener);
        linearlayout_user.setOnClickListener(onclicklistener);
        linearlayout_stand.setOnClickListener(onclicklistener);
    }

    private View.OnClickListener onclicklistener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = null;
            switch (view.getId()){
                case CalCollectorVariables.LAYOUT_DEPOSIT:
                    intent = new Intent(MainActivity.this, DepositActivity.class);
                    break;
                case CalCollectorVariables.LAYOUT_TODO:
                    intent = new Intent(MainActivity.this, TodoActivity.class);
                    break;
                case CalCollectorVariables.LAYOUT_STAND:
                    intent = new Intent(MainActivity.this, PrimaryAccountActivity.class);
                    intent.putExtra(CalCollectorVariables.INTENT_ACCOUNT_TYPE, CalCollectorVariables.ACCOUNT_STAND);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    break;
                case CalCollectorVariables.LAYOUT_USER:
                    intent = new Intent(MainActivity.this, UserActivity.class);
                    break;
                default:
                    break;
            }
            if(intent != null){
                startActivity(intent);
            }
        }
    };

    private AdapterView.OnItemClickListener adapterview_onclicklistener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = null;
            switch (position){
                case CalCollectorVariables.POSITION_DAILY:
                    intent = new Intent(MainActivity.this, DailyActivity.class);
                    break;
                case CalCollectorVariables.POSITION_WEEKLY:
                    intent = new Intent(MainActivity.this, WeeklyActivity.class);
                    break;
                case CalCollectorVariables.POSITION_MONTHLY:
                    intent = new Intent(MainActivity.this, MonthlyActivity.class);
                    break;
                case CalCollectorVariables.POSITION_SPEND:
                    intent = new Intent(MainActivity.this, PrimaryAccountActivity.class);
                    intent.putExtra(CalCollectorVariables.INTENT_ACCOUNT_TYPE, CalCollectorVariables.ACCOUNT_SPEND);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    break;
                case CalCollectorVariables.POSITION_BAD_DEBT:
                    intent = new Intent(MainActivity.this, BadDebtActivity.class);
                    break;
                case CalCollectorVariables.POSITION_BLACKLISTED:
                    intent = new Intent(MainActivity.this, BlacklistedActivity.class);
                    break;
                default:
                    break;
            }
            if(intent != null){
                startActivity(intent);
            }
        }
    };

    private void getSessionProperties() {
        session = new SessionManager(this);
        session.checkLogin();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupDatabase();
        notification.setAlarm(this);
    }

    private void setupDatabase() {
        database = new DatabaseHandler(this);
        accountList = (ArrayList<ContentValues>)database.getAllAccountsSummary();
        if(accountList.size()>0){
            int totalInAmount = 0;
            int totalOutAmount = 0;
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
            textview_deposit.setText(String.format(getString(R.string.textview_deposit), NumberFormat.getInstance().format(totalInAmount - totalOutAmount)));
        }
        else{
            textview_deposit.setText(String.format(getString(R.string.textview_deposit), 0));
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setLayoutWithOrientation();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        setLayoutWithOrientation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_logout:
                session.logoutUser();
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
