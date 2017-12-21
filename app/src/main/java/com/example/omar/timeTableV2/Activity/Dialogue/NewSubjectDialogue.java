package com.example.omar.timeTableV2.Activity.Dialogue;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.omar.timeTableV2.R;


public class NewSubjectDialogue extends Dialog{


    private EditText subjName;
    private Button   btnAddSubj;

    private NewSubject inter;
    private Activity   a;


    public NewSubjectDialogue(@NonNull Context context, Activity activity){

        super(context);
        a = activity;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialogue_new_subject);

        subjName = (EditText) findViewById(R.id.new_subject_name);
        btnAddSubj = (Button) findViewById(R.id.btn_add_subject);

        btnAddSubj.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                Log.i("test", "clicked add button");
                try {
                    inter.addNewSubject(subjName.getText().toString());
                } catch(NullPointerException e) {
                    subjName.clearFocus();
                }
                dismiss();
            }
        });
    }


    @Override
    public void onAttachedToWindow(){

        super.onAttachedToWindow();
        try {
            inter = (NewSubject) a;
        } catch(ClassCastException e) {
            Log.e("error", "inter failed to parse");
        }
    }


    public interface NewSubject{

        void addNewSubject(String name);
    }
}
