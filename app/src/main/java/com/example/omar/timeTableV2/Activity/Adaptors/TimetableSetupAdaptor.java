package com.example.omar.timeTableV2.Activity.Adaptors;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.omar.timeTableV2.DBHandler;
import com.example.omar.timeTableV2.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class TimetableSetupAdaptor extends RecyclerView.Adapter<TimetableSetupAdaptor.ViewHolder>{

    private Context   context;
    private DBHandler dbHandler;
    private int       itemCount;
    private Spinner   subjectSpinner;
    private List<String> subjectList = new ArrayList<>();


    public TimetableSetupAdaptor(Context context){

        this.context = context;
        dbHandler = new DBHandler(context, null);
        itemCount = 0;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.adaptor_view_timetable_setup, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position){

        final int p = position;

        //setup spinner values
        final List<String> subjects = new ArrayList<>();
        subjects.add("Select...");
        subjects.addAll(dbHandler.getSubjects());
        ArrayAdapter adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,
                                                  subjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.subject.setAdapter(adapter);
        subjectSpinner = holder.subject;


        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l){

                if(i > 0){
                    if(p == (getItemCount() - 1)){
                        itemCount++;
                        notifyItemInserted(itemCount - 1);
                    }
                    try {
                        subjectList.remove(p);
                    } catch(IndexOutOfBoundsException e) {
                        subjectList.add(subjects.get(i));
                        return;
                    }
                    subjectList.add(subjects.get(i));

                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView){

            }
        });

    }


    @Override
    public int getItemCount(){

        return itemCount;
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        TextView startTime, endTime;
        Spinner subject;


        ViewHolder(View itemView){

            super(itemView);

            //connects views to variables
            startTime = (TextView) itemView.findViewById(R.id.start_time_text);
            endTime = (TextView) itemView.findViewById(R.id.end_time_text);
            subject = (Spinner) itemView.findViewById(R.id.subject_spinner);


            //*click listeners popup a time picker to choose session start and end times.
            //setup click listeners
            //start time
            startTime.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){

                    setTime(startTime);
                }
            });
            //end time
            endTime.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){

                    setTime(endTime);
                }
            });
        }


        //sets text view value with selected time
        //opens a time picker dialogue
        private void setTime(final TextView textView){

            //setup time
            final Calendar calendar = Calendar.getInstance();
            int            h        = calendar.get(Calendar.HOUR_OF_DAY);
            int            m        = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog;
            timePickerDialog = new TimePickerDialog(context,
                                                    new TimePickerDialog.OnTimeSetListener(){
                                                        @Override
                                                        public void onTimeSet(TimePicker timePicker,
                                                                              int hour, int minute){

                                                            //create simple date format to format date
                                                            SimpleDateFormat sdf = new SimpleDateFormat(
                                                                    "hh:mm a", Locale.US);

                                                            //set date time
                                                            //times are selected for session start/end
                                                            calendar.set(Calendar.HOUR_OF_DAY,
                                                                         hour);
                                                            calendar.set(Calendar.MINUTE, minute);

                                                            //text view value is set from sdf and calender time
                                                            textView.setText(
                                                                    sdf.format(calendar.getTime()));
                                                        }
                                                    }, h, m, true);
            timePickerDialog.show();
        }
    }
}
