package com.example.omar.timeTableV2.Activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.omar.timeTableV2.R;


public class TimetableSetup extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_setup);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_new_session);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
