package com.nocompany.calcollector;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class UserAddActivity extends ActionBarActivity {

    private EditText edittext_username;
    private EditText edittext_ic;
    private EditText edittext_contact_no;
    private EditText edittext_address_streetno1;
    private EditText edittext_address_streetno2;
    private EditText edittext_address_postcode;
    private EditText editext_address_city;
    private Spinner spinner_address_state;

    private TextView textview_username_validator;
    private TextView textview_ic_validator;
    private TextView textview_contact_no_validator;
    private TextView textview_address_streetno1_validator;
    private TextView textview_address_streetno2_validator;
    private TextView textview_address_postcode_validator;
    private TextView textview_address_city_validator;

    private Button button_reset_user;
    private Button button_save_user;

    private ArrayList<String> arrayListStates;
    private String username;
    private String icNo;
    private int usertype;
    private String contactNo;
    private String streetNo1;
    private String streetNo2;
    private String postcode;
    private String city;
    private String state;
    private boolean isFullAddress;

    private ArrayList<String> arrayListErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add);
        arrayListStates = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.array_malaysia_states)));
        setupLayoutVariable();
        setupLayoutFunction();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void setupLayoutVariable() {
        getSupportActionBar().setLogo(R.drawable.actionbar_10_users);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        edittext_username = (EditText)findViewById(R.id.edittext_username);
        edittext_ic = (EditText)findViewById(R.id.edittext_ic);
        edittext_contact_no = (EditText)findViewById(R.id.edittext_contact_no);
        edittext_address_streetno1 = (EditText)findViewById(R.id.edittext_address_streetno1);
        edittext_address_streetno2 = (EditText)findViewById(R.id.edittext_address_streetno2);
        edittext_address_postcode = (EditText)findViewById(R.id.edittext_address_postcode);
        editext_address_city = (EditText)findViewById(R.id.editext_address_city);
        spinner_address_state = (Spinner)findViewById(R.id.spinner_address_state);

        textview_username_validator = (TextView)findViewById(R.id.textview_username_validator);
        textview_ic_validator = (TextView)findViewById(R.id.textview_ic_validator);
        textview_contact_no_validator = (TextView)findViewById(R.id.textview_contact_no_validator);
        textview_address_streetno1_validator = (TextView)findViewById(R.id.textview_address_streetno1_validator);
        textview_address_streetno2_validator = (TextView)findViewById(R.id.textview_address_streetno2_validator);
        textview_address_postcode_validator = (TextView)findViewById(R.id.textview_address_postcode_validator);
        textview_address_city_validator = (TextView)findViewById(R.id.textview_address_city_validator);

        button_reset_user = (Button)findViewById(R.id.button_reset_user);
        button_save_user = (Button)findViewById(R.id.button_save_user);
    }

    private void setupLayoutFunction() {
        spinner_address_state.setOnItemSelectedListener(onItemSelectedListener);
        button_reset_user.setOnClickListener(onClickListener);
        button_save_user.setOnClickListener(onClickListener);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener =  new AdapterView.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            state = arrayListStates.get(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    };

    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.button_reset_user:
                    resetUserFields();
                    break;
                case R.id.button_save_user:
                    saveUser();
                    break;
            }
        }
    };

    private void resetUserFields(){
        edittext_username.setText("");
        edittext_ic.setText("");
        edittext_contact_no.setText("");
        edittext_address_streetno1.setText("");
        edittext_address_streetno2.setText("");
        edittext_address_postcode.setText("");
        editext_address_city.setText("");
        spinner_address_state.setSelection(arrayListStates.indexOf(getString(R.string.state_penang)));

        textview_username_validator.setBackground(null);
        textview_ic_validator.setBackground(null);
        textview_contact_no_validator.setBackground(null);
        textview_address_streetno1_validator.setBackground(null);
        textview_address_streetno2_validator.setBackground(null);
        textview_address_postcode_validator.setBackground(null);
        textview_address_city_validator.setBackground(null);

        edittext_username.requestFocus();
    }

    private void saveUser(){
        username = String.valueOf(edittext_username.getText()).trim();
        icNo = String.valueOf(edittext_ic.getText()).trim();
        contactNo = String.valueOf(edittext_contact_no.getText()).trim();
        streetNo1 = String.valueOf(edittext_address_streetno1.getText()).trim();
        streetNo2 = String.valueOf(edittext_address_streetno2.getText()).trim();
        postcode = String.valueOf(edittext_address_postcode.getText()).trim();
        city = String.valueOf(editext_address_city.getText()).trim();
        state = spinner_address_state.getSelectedItem().toString().trim();

        if(allValidation()){
            DatabaseHandler database = new DatabaseHandler(this);
            boolean isPersonExisted = database.isPersonExisted(icNo);
            if(isPersonExisted){
                arrayListErrorMessage = new ArrayList<String>();
                arrayListErrorMessage.add("Person with IC number: " + icNo  +" is already existed!");
                textview_ic_validator.setBackgroundResource(R.color.not_valid);
                errorDialog();
                return;
            }

            if(username.contains(CalCollectorVariables.USERNAME_DELIMITER)){
                username = username.substring(0, username.indexOf(CalCollectorVariables.USERNAME_DELIMITER));
                usertype = 2;
            }
            else{
                usertype = 0;
            }

            Person newPerson = new Person();
            newPerson.setName(username);
            newPerson.setIc(icNo);
            newPerson.setType(usertype);
            newPerson.setPhone(contactNo);
            newPerson.setName(username);
            if(isFullAddress){
                String address = streetNo1 + CalCollectorVariables.ADDRESS_DELIMITER +
                        streetNo2 + CalCollectorVariables.ADDRESS_DELIMITER +
                        postcode + CalCollectorVariables.ADDRESS_DELIMITER +
                        city + CalCollectorVariables.ADDRESS_DELIMITER  +
                        state;
                newPerson.setAddress(address);
            }
            int newPersonID = (int)database.addPerson(newPerson);
            if(newPersonID>0){
                Intent intent  = new Intent(UserAddActivity.this, UserDetailsActivity.class);
                intent.putExtra(CalCollectorVariables.INTENT_USER_ID, newPersonID);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }
        else{
            errorDialog();
        }
    }

    private boolean allValidation(){
        boolean isAllValidate = false;
        boolean isUsernameValid = false,
                isICNoValid = false,
                isContactNoValid = false,
                isAddressValid = false,
                isStreetNo1Valid = false,
                isStreetNo2Valid = false,
                isPostcodeValid = false,
                isCityValid = false;
        arrayListErrorMessage = new ArrayList<String>();

        if(username.length() > 5){
            isUsernameValid = true;
            textview_username_validator.setBackground(null);
        }
        else{
            textview_username_validator.setBackgroundResource(R.color.not_valid);
            arrayListErrorMessage.add("Username length must be more than 5 characters.");
        }

        if(icNo.length() == 12){
            isICNoValid = true;
            textview_ic_validator.setBackground(null);
        }
        else{
            textview_ic_validator.setBackgroundResource(R.color.not_valid);
            arrayListErrorMessage.add("IC number must be 12 number.");
        }

        if(contactNo.length() >= 8 && contactNo.length() <= 10){
            isContactNoValid = true;
            textview_contact_no_validator.setBackground(null);
        }
        else{
            textview_contact_no_validator.setBackgroundResource(R.color.not_valid);
            arrayListErrorMessage.add("Phone number length must be between 10 to 12 number.");
        }

        if(streetNo1.length() == 0 || streetNo2.length() == 0 || postcode.length() == 0 || city.length() == 0){
            if(streetNo1.length() == 0 && streetNo2.length() == 0 && postcode.length() == 0 && city.length() == 0){
                isAddressValid = true;
                textview_address_streetno1_validator.setBackground(null);
                textview_address_streetno2_validator.setBackground(null);
                textview_address_postcode_validator.setBackground(null);
                textview_address_city_validator.setBackground(null);
            }
            else{
                textview_address_streetno1_validator.setBackgroundResource(R.color.not_valid);
                textview_address_streetno2_validator.setBackgroundResource(R.color.not_valid);
                textview_address_postcode_validator.setBackgroundResource(R.color.not_valid);
                textview_address_city_validator.setBackgroundResource(R.color.not_valid);
                arrayListErrorMessage.add("Fill all address information or left all blank.");
            }
        }
        else{
            //Skip street no 1 & 2 because they are not possibly met if check condition length  more than 0.
            isStreetNo1Valid = true;
            isStreetNo2Valid = true;
            textview_address_streetno1_validator.setBackground(null);
            textview_address_streetno2_validator.setBackground(null);

            if(postcode.length() == 5){
                isPostcodeValid = true;
                textview_address_postcode_validator.setBackground(null);
            }
            else{
                textview_address_postcode_validator.setBackgroundResource(R.color.not_valid);
                arrayListErrorMessage.add("Postcode must be 5 number.\n");
            }
            if(city.length() > 2){
                isCityValid = true;
                textview_address_city_validator.setBackground(null);
            }
            else{
                textview_address_city_validator.setBackgroundResource(R.color.not_valid);
                arrayListErrorMessage.add("Malaysian shortest city name length is 3.\n");
            }

            if(isStreetNo1Valid && isStreetNo2Valid && isPostcodeValid && isCityValid){
                isAddressValid = true;
                isFullAddress = true;
            }
        }

        if(isUsernameValid && isICNoValid && isContactNoValid && isAddressValid){
            isAllValidate = true;
        }
        return isAllValidate;
    }

    private void errorDialog(){
        String errorMessageString = "";
        for(int i = 0; i < arrayListErrorMessage.size(); i++){
            errorMessageString += (i+1)+") "+ arrayListErrorMessage.get(i) + "\n";
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserAddActivity.this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
