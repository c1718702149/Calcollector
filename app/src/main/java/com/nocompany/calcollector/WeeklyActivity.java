package com.nocompany.calcollector;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class WeeklyActivity extends ActionBarActivity {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    int mCurrentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly);
        setupLayoutVariable();
        setupLayoutFunction();
    }

    private void setupLayoutVariable() {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.actionbar_4_weekly);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.actionbar_title_weekly_fixed));
    }

    private void setupLayoutFunction() {
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);
    }

    ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i2) {}

        @Override
        public void onPageSelected(int i) {
            mCurrentPage = i;
            switch (i){
                case 0:
                    getSupportActionBar().setTitle(getString(R.string.actionbar_title_weekly_fixed));
                    break;
                case 1:
                    getSupportActionBar().setTitle(getString(R.string.actionbar_title_weekly_flexi));
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {}
    };

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        int mNum;
        int accountType;
        private DatabaseHandler database;
        ArrayList<ContentValues> accountList;

        private TextView textView_record_not_found;
        private LinearLayout linearLayout_accountList_title;
        private ListView listView_accountList;

        AccountListAdapter accountListAdapter;

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt(ARG_SECTION_NUMBER) : 1;
            switch (mNum){
                case 1:
                    accountType = CalCollectorVariables.ACCOUNT_WEEK_FIXED;
                    break;
                case 2:
                    accountType = CalCollectorVariables.ACCOUNT_WEEK_FLEX;
                    break;
            }
            database = new DatabaseHandler(getActivity());
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_weekly, container, false);
            textView_record_not_found = (TextView)rootView.findViewById(R.id.textView_record_not_found);
            linearLayout_accountList_title = (LinearLayout)rootView.findViewById(R.id.linearLayout_accountList_title);
            listView_accountList = (ListView)rootView.findViewById(R.id.listView_accountList);

            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();
            accountList = (ArrayList<ContentValues>)database.retrieveAccountType(accountType, false);
            if(accountList.size() > 0){
                accountListAdapter = new AccountListAdapter(getActivity().getApplication(), accountList);
                listView_accountList.setAdapter(accountListAdapter);
            }
            else{
                linearLayout_accountList_title.setVisibility(View.GONE);
                listView_accountList.setVisibility(View.GONE);
                textView_record_not_found.setVisibility(View.VISIBLE);
            }
        }
    }

}
