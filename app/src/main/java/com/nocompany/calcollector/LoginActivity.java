package com.nocompany.calcollector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class LoginActivity extends Activity {
    private final static String FILENAME = "UserCredential";
    private SessionManager session;
    private EditText edittext_username;
    private EditText edittext_password;
    private Button button_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupViewVariables();
        createUser();
        session = new SessionManager(getApplicationContext());
        if(session.isLoggedIn()){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void setupViewVariables() {
        ((TextView)findViewById(R.id.textview_loginIntro)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ginko.TTF"));
        edittext_username = (EditText)findViewById(R.id.edittext_username);
        edittext_password = (EditText)findViewById(R.id.edittext_password);
        button_login = (Button)findViewById(R.id.button_login);
    }

    /**
     * Create user credential
     * Remove after creation
     */
    private void createUser(){
        String username = "ongchiahock";
        String password = "ongchiahock";
        String email = "ongchiahock@gmail.com";

        try {
            String string = username+"|"+password + "|" + email;
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onLogin(View view){
        String username = edittext_username.getText().toString();
        String password = edittext_password.getText().toString();
        try {
            FileInputStream fis = openFileInput(FILENAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                String[] parameters = sb.toString().split("\\|");
                if(validatePassword()){
                    session.createLoginSession(parameters[0], parameters[2]);
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean validatePassword() {

        return true;
    }
}
