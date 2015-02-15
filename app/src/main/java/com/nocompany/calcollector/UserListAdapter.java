package com.nocompany.calcollector;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class UserListAdapter extends BaseAdapter implements Filterable {
    private Application application;
    private LayoutInflater layoutInflater;
    private ArrayList<Person> persons;

    private DatabaseHandler databaseHandler;
    private ArrayList<Person> defaultPersons;

    public UserListAdapter(Application application, ArrayList<Person> persons){
        this.application = application;
        this.layoutInflater = (LayoutInflater) application.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.persons = persons;
        databaseHandler = new DatabaseHandler(application);
        this.defaultPersons = persons;
    }

    @Override
    public int getCount() {
        return persons.size();
    }

    @Override
    public Object getItem(int position) {
        return persons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Person person = persons.get(position);
        ViewHolder viewHolder = null;
        try{
            if(row == null){
                row = layoutInflater.inflate(R.layout.user_list_adapter, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.user_linearlayout = (LinearLayout)row.findViewById(R.id.user_linearlayout);
                viewHolder.line_no = (TextView)row.findViewById(R.id.line_no);
                viewHolder.user_name = (TextView)row.findViewById(R.id.user_name);
                viewHolder.user_contact = (TextView)row.findViewById(R.id.user_contact);
                viewHolder.blacklisted = (ImageView)row.findViewById(R.id.blacklisted);
                row.setTag(viewHolder);
            }
            else{
                viewHolder = (ViewHolder)row.getTag();
            }

            viewHolder.user_linearlayout.setOnClickListener(new UserOnClickListener(person.getId()));
            viewHolder.line_no.setText(String.valueOf(position+1));
            viewHolder.user_name.setText(person.getName());
            viewHolder.user_contact.setText(person.getPhone());
            if(person.isIs_blacklisted()){
                viewHolder.blacklisted.setImageResource(R.drawable.skeleton_on);
                viewHolder.blacklisted.setOnClickListener(null);
            }
            else {
                viewHolder.blacklisted.setImageResource(R.drawable.skeleton_off);
                viewHolder.blacklisted.setOnClickListener(new UserOnClickListener(person.getId()));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return row;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Person> filteredPersons = new ArrayList<Person>();
                constraint = constraint.toString().toLowerCase();
                for(Person person:defaultPersons){
                    String personName = person.getName();
                    if (personName.toLowerCase().contains(constraint.toString()))  {
                        filteredPersons.add(person);
                    }
                }
                results.count = filteredPersons.size();
                results.values = filteredPersons;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                persons = (ArrayList<Person>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    static class ViewHolder{
        private LinearLayout user_linearlayout;
        private TextView line_no;
        private TextView user_name;
        private TextView user_contact;
        private ImageView blacklisted;
    }

    class UserOnClickListener implements View.OnClickListener{
        int selectedPersonId = 0;

        UserOnClickListener(int selectedPersonId){
            this.selectedPersonId = selectedPersonId;
        };

        @Override
        public void onClick(final View v) {
            if(v.getId() == R.id.user_linearlayout){
                Intent intent  = new Intent(application, UserDetailsActivity.class);
                intent.putExtra(CalCollectorVariables.INTENT_USER_ID, selectedPersonId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                application.startActivity(intent);
            }
            else if(v.getId() == R.id.blacklisted){
                final Person person = databaseHandler.getPersonByID(selectedPersonId);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getRootView().getContext());
                alertDialogBuilder.setTitle("Blacklist");
                alertDialogBuilder
                        .setMessage("Add " + person.getName() + " to blacklist?")
                        .setCancelable(true)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                boolean isBlacklisted = databaseHandler.blacklistPersonAndAccounts(selectedPersonId);
                                if(isBlacklisted){
                                    ((ImageView)v).setImageResource(R.drawable.skeleton_on);
                                    v.setOnClickListener(null);
                                    Toast.makeText(application, person.getName() + " is blacklisted.", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(application, person.getName() + " do not have account yet.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }
    }
}
