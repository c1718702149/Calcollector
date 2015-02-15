package com.nocompany.calcollector;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
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

public class UserActivity extends ActionBarActivity {

    private DatabaseHandler database;
    private ArrayList<Person> persons;

    private TextView textView_record_not_found;
    private EditText editText_quickSearch;
    private LinearLayout linearLayout_userList_title;
    private ListView listView_personList;

    private UserListAdapter userListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        database = new DatabaseHandler(this);
        setupLayoutVariable();
    }

    private void setupLayoutVariable() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.actionbar_10_users);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        textView_record_not_found = (TextView)findViewById(R.id.textView_record_not_found);
        editText_quickSearch = (EditText)findViewById(R.id.editText_quickSearch);
        linearLayout_userList_title = (LinearLayout)findViewById(R.id.linearLayout_userList_title);
        listView_personList = (ListView)findViewById(R.id.listView_personList);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupLayoutFunction();
    }

    private void setupLayoutFunction() {
        persons = (ArrayList<Person>)database.getAllPersons();
        if(persons.size()>0){
            textView_record_not_found.setVisibility(View.GONE);
            editText_quickSearch.setVisibility(View.VISIBLE);
            linearLayout_userList_title.setVisibility(View.VISIBLE);
            listView_personList.setVisibility(View.VISIBLE);

            userListAdapter = new UserListAdapter(getApplication(), persons);
            listView_personList.setAdapter(userListAdapter);
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
            UserActivity.this.userListAdapter.getFilter().filter(s.toString());
        }
        @Override
        public void afterTextChanged(Editable s) {}
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_addPerson){
            Intent intent = new Intent(UserActivity.this, UserAddActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
