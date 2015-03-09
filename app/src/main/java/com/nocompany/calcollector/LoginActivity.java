package com.nocompany.calcollector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Pattern;


public class LoginActivity extends Activity {
    private final static String FILENAME = "UserCredential";
    private SessionManager session;
    private EditText edittext_username;
    private EditText edittext_password;
    private ArrayList<String> arrayListErrorMessage;

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
    }

    private void createUser(){
        String path  = getFileStreamPath(FILENAME).getAbsolutePath();
        File file = new File(path);
        if(!file.exists()) {
            String username = "ongchiahock";
            String password = "ongchiahock";
            String email = "ongchiahock@gmail.com";
            try {
                String string = username + CalCollectorVariables.USER_CREDENTIAL_DELIMITER + password + CalCollectorVariables.USER_CREDENTIAL_DELIMITER + email;
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
    }

    public void onLogin(View view){
        String username = edittext_username.getText().toString();
        String password = edittext_password.getText().toString();
        if(validateInput(username, password)){
            validateUser(username, password);
        }
        else{
            errorDialog();
        }
    }

    private boolean validateInput(String username, String password) {
        boolean isUsernameValid = false, isPasswordValid = false;
        arrayListErrorMessage = new ArrayList<String>();
        if(username.length() < 4 ){
            arrayListErrorMessage.add("Username length is too short.");
        }
        else{
            isUsernameValid = true;
        }
        if(password.length() < 4){
            arrayListErrorMessage.add("Password length is too short.");
        }
        else{
            isPasswordValid = true;
        }

        if(isUsernameValid && isPasswordValid){
            return true;
        }
        return false;
    }

    private void errorDialog(){
        String errorMessageString = "";
        for(int i = 0; i < arrayListErrorMessage.size(); i++){
            errorMessageString += (i+1)+") "+ arrayListErrorMessage.get(i) + "\n";
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        alertDialogBuilder.setTitle("Error Message");
        alertDialogBuilder
                .setMessage(errorMessageString)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void validateUser(String username, String password) {
        try{
            FileInputStream fis = openFileInput(FILENAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                String[] parameters = sb.toString().split(Pattern.quote(CalCollectorVariables.USER_CREDENTIAL_DELIMITER));
                if(parameters[0] != null && parameters[1] != null){
                    if(parameters[0].equals(username) && parameters[1].equals(password)){
                        session.createLoginSession(parameters[0], parameters[2]);
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    }
                    else{
                        arrayListErrorMessage = new ArrayList<String>();
                        arrayListErrorMessage.add("Username and password not match!");
                        errorDialog();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }
}
