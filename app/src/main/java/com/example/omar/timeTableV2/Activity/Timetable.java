package com.example.omar.timeTableV2.Activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.omar.timeTableV2.Activity.Adaptors.TimetableAdaptor;
import com.example.omar.timeTableV2.DBHandler;
import com.example.omar.timeTableV2.R;

import java.util.Date;
import java.util.List;


public class Timetable extends AppCompatActivity{


    private SectionsPagerAdapter mSectionsPagerAdapter;


    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout
                .addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        tabLayout.removeAllTabs();

        for(int i = 1; i <= mSectionsPagerAdapter.getCount(); i++){
            tabLayout.addTab(tabLayout.newTab().setText(getDay(i)));
        }

    }


    String getDay(int p){

        switch(p){
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
            default:
                return null;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.menu_timetable, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_settings){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static class PlaceholderFragment extends Fragment{


        private static final String ARG_SECTION_NUMBER = "section_number";
        private RecyclerView     recyclerView;
        private TimetableAdaptor timetableAdaptor;
        private DBHandler        dbHandler;

        private List<String>  sessionNames;
        private List<Date>    sessionStart;
        private List<Date>    sessionEnd;
        private List<Integer> timeUntilNext;


        public PlaceholderFragment(){

        }


        public static PlaceholderFragment newInstance(int sectionNumber){

            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle              args     = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){

            View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);

            String day = getDay(getArguments().getInt(ARG_SECTION_NUMBER));

            dbHandler = new DBHandler(getContext(), null);

            //get sessions details
            sessionNames = dbHandler.getSessionNames(day);
            sessionStart = dbHandler.getStartTimes(day);
            sessionEnd = dbHandler.getEndTimes(day);
            timeUntilNext = dbHandler.getMinutesUntilNext(day);

            //initialise views and variables
            recyclerView = (RecyclerView) rootView.findViewById(R.id.timetable);
            timetableAdaptor = new TimetableAdaptor(sessionNames, sessionStart, sessionEnd,
                                                    timeUntilNext);

            //connect adaptor to recycler view
            recyclerView.setAdapter(timetableAdaptor);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            return rootView;
        }


        String getDay(int p){

            switch(p){
                case 1:
                    return "Sunday";
                case 2:
                    return "Monday";
                case 3:
                    return "Tuesday";
                case 4:
                    return "Wednesday";
                case 5:
                    return "Thursday";
                case 6:
                    return "Friday";
                case 7:
                    return "Saturday";
                default:
                    return null;
            }
        }

    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter{

        SectionsPagerAdapter(FragmentManager fm){

            super(fm);
        }


        @Override
        public Fragment getItem(int position){

            return PlaceholderFragment.newInstance(position + 1);
        }


        @Override
        public int getCount(){

            return 7;
        }
    }
}
