package com.example.omar.timeTableV2.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.omar.timeTableV2.Activity.Adaptors.SetupTouchHelper;
import com.example.omar.timeTableV2.Activity.Adaptors.TimetableSetupAdaptor;
import com.example.omar.timeTableV2.Activity.Dialogue.NewSubjectDialogue;
import com.example.omar.timeTableV2.DBHandler;
import com.example.omar.timeTableV2.MiscData;
import com.example.omar.timeTableV2.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class TimetableSetup extends AppCompatActivity implements NewSubjectDialogue.NewSubject{

    List<String> days = Arrays
            .asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");
    private int          currentSpinnerSelection;
    private RecyclerView recyclerView;
    private Spinner      daySpinner;

    private TimetableSetupAdaptor timetableSetupAdaptor = new TimetableSetupAdaptor(this);
    private NewSubjectDialogue newSubjectDialogue;
    private ArrayAdapter       adapter;

    private DBHandler dbHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_setup);

        dbHandler = new DBHandler(this, null);
        newSubjectDialogue = new NewSubjectDialogue(this, this);

        Button  btnSave       = (Button) findViewById(R.id.btn_save_day);
        Button  btnNewSubject = (Button) findViewById(R.id.btn_new_subject);
        Toolbar toolbar       = (Toolbar) findViewById(R.id.stp_toolbar);

        //setup toolbar
        setSupportActionBar(toolbar);

        daySpinner = (Spinner) findViewById(R.id.spnr_set_up);


        //setup spinner adaptor

        for(String day : days){
            if(dbHandler.isDayDone(day)){
                Collections.replaceAll(days, day, day + " (Done)");
            }
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(adapter);

        updateCurrentAdaptor();

        //on item select listener
        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l){

                if(recyclerView.getAdapter().getItemCount() > 1 && currentSpinnerSelection != i){
                    AlertDialog alertDialog = new AlertDialog.Builder(TimetableSetup.this).create();
                    alertDialog.setMessage(
                            "Any unsaved data will be lost. Are you sure you want to change the day?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                                          new DialogInterface.OnClickListener(){
                                              @Override
                                              public void onClick(DialogInterface dialogInterface,
                                                                  int i){

                                                  timetableSetupAdaptor.refreshView();
                                                  currentSpinnerSelection = i;
                                                  updateCurrentAdaptor();
                                              }
                                          });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                                          new DialogInterface.OnClickListener(){
                                              @Override
                                              public void onClick(DialogInterface dialogInterface,
                                                                  int i){

                                                  daySpinner.setSelection(currentSpinnerSelection);
                                              }
                                          });
                    alertDialog.show();
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView){

            }
        });

        //set up recycle view
        recyclerView = (RecyclerView) findViewById(R.id.rscl_setup);
        recyclerView.setAdapter(timetableSetupAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        /*
         Touch helper allows user to drag and swipe to move and delete views respectively
         */
        //setup touch helper to recycler view
        ItemTouchHelper.Callback callback    = new SetupTouchHelper(timetableSetupAdaptor);
        ItemTouchHelper          touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        //save day to database
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                saveSessions();
                updateDaysList();

                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.root_setup), "Saved day", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        //add new subject
        btnNewSubject.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                newSubjectDialogue.show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_timetable_setup, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch(item.getItemId()){
            case R.id.save_table:
                /*todo: save finfish status in shared preferences and go to main activity
                  TODO: create a main activity
                */

                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setMessage("Are you done with the set up?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                                      new DialogInterface.OnClickListener(){
                                          @Override
                                          public void onClick(DialogInterface dialogInterface,
                                                              int i){

                                              MiscData.getInstance().doneSetup(TimetableSetup.this);
                                              startActivity(new Intent(TimetableSetup.this,
                                                                       Timetable.class));
                                              finish();
                                          }
                                      });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                                      new DialogInterface.OnClickListener(){
                                          @Override
                                          public void onClick(DialogInterface dialogInterface,
                                                              int i){

                                              Snackbar snackbar = Snackbar
                                                      .make(findViewById(R.id.root_setup),
                                                            "You said No!", Snackbar.LENGTH_SHORT);
                                              snackbar.show();
                                          }
                                      });

                alertDialog.show();
                return true;
            default:
                return false;
        }
    }


    void saveSessions(){

        String day = daySpinner.getSelectedItem().toString();

        if(Objects.equals(day.substring(day.length() - 6, day.length()), "(Done)")){
            day = day.substring(0, day.length() - 7);
        }

        dbHandler.clearDay(day);

        List<String> subjects   = timetableSetupAdaptor.getSubjectList();
        List<Date>   endTimes   = timetableSetupAdaptor.getEndTime();
        List<Date>   startTimes = timetableSetupAdaptor.getStartTime();

        for(int i = 0; i < subjects.size(); i++){
            dbHandler.addSession(day, subjects.get(i), startTimes.get(i), endTimes.get(i));
        }

    }


    void updateDaysList(){

        String day = daySpinner.getSelectedItem().toString();

        if(!Objects.equals(day.substring(day.length() - 6, day.length()), "(Done)")){
            Collections.replaceAll(days, daySpinner.getSelectedItem().toString(),
                                   daySpinner.getSelectedItem().toString() + " (Done)");
            adapter.notifyDataSetChanged();
        }


    }


    void updateCurrentAdaptor(){

        String day = daySpinner.getSelectedItem().toString();

        if(Objects.equals(day.substring(day.length() - 6, day.length()), "(Done)")){
            day = day.substring(0, day.length() - 7);
        }
        if(dbHandler.isDayDone(day)){
            List<String> names      = dbHandler.getSessionNames(day);
            List<Date>   startTimes = dbHandler.getStartTimes(day);
            List<Date>   endTimes   = dbHandler.getEndTimes(day);
            timetableSetupAdaptor.setLists(names, startTimes, endTimes);
        }
    }


    @Override
    public void addNewSubject(String name){

        dbHandler.addSubject(name);
        timetableSetupAdaptor.notifyDataSetChanged();

    }
}
