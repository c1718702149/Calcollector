package com.nocompany.calcollector;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class BlacklistedActivity extends ActionBarActivity {

    private DatabaseHandler database;
    private ArrayList<Person> persons;

    private TextView textView_record_not_found;
    private EditText editText_quickSearch;
    private LinearLayout linearLayout_userList_title;
    private ListView listView_personList;

    private BlacklistAdapter blacklistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklisted);
        database = new DatabaseHandler(this);
        setupLayoutVariable();
        setupLayoutEnvironment();
    }

    private void setupLayoutVariable() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.actionbar_8_blacklist);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        textView_record_not_found = (TextView)findViewById(R.id.textView_record_not_found);
        editText_quickSearch = (EditText)findViewById(R.id.editText_quickSearch);
        linearLayout_userList_title = (LinearLayout)findViewById(R.id.linearLayout_userList_title);
        listView_personList = (ListView)findViewById(R.id.listView_personList);
    }

    public void setupLayoutEnvironment() {
        persons = (ArrayList<Person>)database.getBlacklistedPerson();
        if(persons.size()>0){
            textView_record_not_found.setVisibility(View.GONE);
            editText_quickSearch.setVisibility(View.VISIBLE);
            linearLayout_userList_title.setVisibility(View.VISIBLE);
            listView_personList.setVisibility(View.VISIBLE);

            blacklistAdapter = new BlacklistAdapter(this, persons);
            listView_personList.setAdapter(blacklistAdapter);
            editText_quickSearch.addTextChangedListener(textWatcher);
        }
        else{
            textView_record_not_found.setVisibility(View.VISIBLE);
            editText_quickSearch.setVisibility(View.GONE);
            linearLayout_userList_title.setVisibility(View.GONE);
            listView_personList.setVisibility(View.GONE);
        }
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            BlacklistedActivity.this.blacklistAdapter.getFilter().filter(s.toString());
        }
        @Override
        public void afterTextChanged(Editable s) {}
    };
}
