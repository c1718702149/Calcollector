package com.nocompany.calcollector;

import android.app.AlertDialog;
import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AccountListAdapter extends BaseAdapter{
    private Application application;
    private LayoutInflater layoutInflater;

    private ArrayList<ContentValues> accounts;
    private ArrayList<ContentValues> defaultAccounts;

    AccountListAdapter(){}

    AccountListAdapter(Application application, ArrayList<ContentValues> accounts){
        this.application = application;
        layoutInflater = (LayoutInflater) application.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.accounts = accounts;
        this.defaultAccounts = accounts;
    }

    @Override
    public int getCount() {
        return accounts.size();
    }

    @Override
    public Object getItem(int position) {
        return accounts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ContentValues account = accounts.get(position);
        ViewHolder viewHolder = null;
        try{
            if(row == null){
                row = layoutInflater.inflate(R.layout.account_list_adapter, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.account_linearlayout = (LinearLayout)row.findViewById(R.id.account_linearlayout);
                viewHolder.line_no = (TextView)row.findViewById(R.id.line_no);
                viewHolder.account_updated_date = (TextView)row.findViewById(R.id.account_updated_date);
                viewHolder.account_owner = (TextView)row.findViewById(R.id.account_owner);
                viewHolder.account_total_amount = (TextView)row.findViewById(R.id.account_total_amount);
                viewHolder.account_status = (TextView)row.findViewById(R.id.account_status);
                row.setTag(viewHolder);
            }
            else{
                viewHolder = (ViewHolder)row.getTag();
            }
            viewHolder.account_linearlayout.setOnClickListener(new AccountOnClickListener(account.getAsInteger(Account.TableAccount.COLUMN_NAME_ID), account.getAsString(Person.TablePerson.COLUMN_NAME_USERNAME)));
            viewHolder.line_no.setText(String.valueOf(position+1));
            viewHolder.account_updated_date.setText(formatDatetime(account.getAsString(Account.TableAccount.COLUMN_NAME_UPDATED_AT)));
            viewHolder.account_owner.setText(account.getAsString(Person.TablePerson.COLUMN_NAME_USERNAME));
            viewHolder.account_total_amount.setText(account.getAsString("total_amount"));
            switch (account.getAsInteger(Account.TableAccount.COLUMN_NAME_STATUS)){
                case CalCollectorVariables.ACCOUNT_HEADER_STATUS_UNPAID:
                    viewHolder.account_status.setBackgroundResource(R.color.account_unpaid);
                    break;
                case CalCollectorVariables.ACCOUNT_HEADER_STATUS_PAID:
                    viewHolder.account_status.setBackgroundResource(R.color.account_paid);
                    break;
                case CalCollectorVariables.ACCOUNT_HEADER_STATUS_BADDEBT:
                    viewHolder.account_status.setBackgroundResource(R.color.account_baddebt);
                    break;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return row;
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
            Intent intent  = new Intent(application, AccountDetailsActivity.class);
            intent.putExtra(CalCollectorVariables.INTENT_ACCOUNT_ID, selectedAccountId);
            intent.putExtra(CalCollectorVariables.INTENT_USER_NAME, owner);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            application.startActivity(intent);
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

    static class ViewHolder{
        private LinearLayout account_linearlayout;
        private TextView line_no;
        private TextView account_updated_date;
        private TextView account_owner;
        private TextView account_total_amount;
        private TextView account_status;
    }
}
