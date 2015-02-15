package com.nocompany.calcollector;

//import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class BadDebtActivity extends ActionBarActivity {

    private DatabaseHandler database;
    ArrayList<ContentValues> accountList;

    private TextView textView_record_not_found;
    private LinearLayout linearLayout_accountList_title;
    private ListView listView_accountList;

    AccountListAdapter accountListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bad_debt);
        database = new DatabaseHandler(this);
        setupLayoutVariable();
        setupLayoutFunction();
    }

    private void setupLayoutVariable() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.actionbar_7_baddebt);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        textView_record_not_found = (TextView)findViewById(R.id.textView_record_not_found);
        linearLayout_accountList_title = (LinearLayout)findViewById(R.id.linearLayout_accountList_title);
        listView_accountList = (ListView)findViewById(R.id.listView_accountList);
    }

    private void setupLayoutFunction() {
        accountList = (ArrayList<ContentValues>)database.retrieveAccountType(CalCollectorVariables.ACCOUNT_DAY_FIXED, true);
        if(accountList.size() > 0){
            accountListAdapter = new AccountListAdapter(getApplication(), accountList);
            listView_accountList.setAdapter(accountListAdapter);
        }
        else{
            linearLayout_accountList_title.setVisibility(View.GONE);
            listView_accountList.setVisibility(View.GONE);
            textView_record_not_found.setVisibility(View.VISIBLE);
        }
    }
}
