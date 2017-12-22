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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class TimetableSetupAdaptor extends RecyclerView.Adapter<TimetableSetupAdaptor.ViewHolder>{

    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);
    private Context   context;
    private DBHandler dbHandler;
    private int       itemCount;
    private List<String> subjectList = new ArrayList<>();
    private List<Date>   startTimes  = new ArrayList<>();
    private List<Date>   endTimes    = new ArrayList<>();


    public TimetableSetupAdaptor(Context context){

        this.context = context;
        dbHandler = new DBHandler(context, null);
        itemCount = 1;
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

        try {
            holder.subject.setSelection(subjects.indexOf(subjectList.get(position)));
            holder.startTime.setText(sdf.format(startTimes.get(position)));
            holder.endTime.setText(sdf.format(endTimes.get(position)));
        } catch(IndexOutOfBoundsException ignore) {

        }

        //insert session name into list when selected
        holder.subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l){

                if(i > 0){
                    if(p == (getItemCount() - 1)){
                        itemCount++;
                        notifyItemInserted(itemCount - 1);
                    }
                    try {
                        subjectList.remove(p);
                    } catch(IndexOutOfBoundsException ignore) {
                    }
                    subjectList.add(subjects.get(i));

                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView){

            }
        });

        holder.startTime.setText(sdf.format(Calendar.getInstance().getTime()));
        holder.endTime.setText(sdf.format(Calendar.getInstance().getTime()));

        //click listeners popup a time picker to choose session start and end times.
        //setup click listeners

        //start time
        holder.startTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                setTime(holder.startTime, p, startTimes);
            }
        });

        //end time
        holder.endTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                setTime(holder.endTime, p, endTimes);
            }
        });
    }


    //sets text view value with selected time
    //opens a time picker dialogue
    private void setTime(final TextView textView, final int position, final List<Date> list){

        //setup time
        final Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(sdf.parse(textView.getText().toString()));
        } catch(ParseException ignore) {
        }

        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute){

                //set date time
                //times are selected for session start/end
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);

                //text view value is set from sdf and calender time
                textView.setText(sdf.format(calendar.getTime()));

                Date time = calendar.getTime();

                try {
                    list.remove(position);
                } catch(IndexOutOfBoundsException e) {
                    list.add(time);
                    return;
                }
                list.add(position, time);

            }
        }, h, m, false);
        timePickerDialog.show();
    }


    @Override
    public int getItemCount(){

        return itemCount;
    }


    void removeSession(int position){

        try {
            subjectList.remove(position);
            startTimes.remove(position);
            endTimes.remove(position);
        } catch(IndexOutOfBoundsException ignore) {

        }
        notifyItemRemoved(position);

        itemCount--;
    }


    void moveSession(int current, int target){

        try {
            Collections.swap(subjectList, current, target);
            Collections.swap(startTimes, current, target);
            Collections.swap(endTimes, current, target);
        } catch(IndexOutOfBoundsException ignore) {

        }
        notifyItemMoved(current, target);
    }


    public void refreshView(){

        itemCount = 1;
        subjectList.clear();
        endTimes.clear();
        startTimes.clear();

        notifyDataSetChanged();

    }


    public List<String> getSubjectList(){

        return subjectList;
    }


    public List<Date> getStartTime(){

        return startTimes;
    }


    public List<Date> getEndTime(){

        return endTimes;
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

        }
    }
}
