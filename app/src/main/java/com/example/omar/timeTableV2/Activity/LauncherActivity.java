package com.example.omar.timeTableV2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.example.omar.timeTableV2.MiscData;
import com.example.omar.timeTableV2.R;


public class LauncherActivity extends AppCompatActivity{

    MiscData miscData = MiscData.getInstance();

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){

            String done = (String) msg.obj;

            switch(done){
                case "yes":
                    finish();
                    break;
                case "no":
                    startActivity(new Intent(LauncherActivity.this, TimetableSetup.class));
                    finish();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        routeActivity();
    }


    private void routeActivity(){

        final Message message = handler.obtainMessage();

        Runnable run = new Runnable(){
            @Override
            public void run(){

                if(miscData.isDone(LauncherActivity.this)){
                    message.obj = "yes";
                    handler.sendMessage(message);
                } else{
                    message.obj = "no";
                    handler.sendMessage(message);
                }

            }
        };

        Thread thread = new Thread(run);
        thread.run();
    }

}
