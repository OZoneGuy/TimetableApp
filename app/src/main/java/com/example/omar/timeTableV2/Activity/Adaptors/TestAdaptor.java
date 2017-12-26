package com.example.omar.timeTableV2.Activity.Adaptors;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.omar.timeTableV2.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


@SuppressWarnings("unused")
public class TestAdaptor extends RecyclerView.Adapter<TestAdaptor.ViewHolder>{

    private List<String>  sessionNames;
    private List<Integer> timeUntilNext;
    private List<Date>    startTimes;
    private List<Date>    endTimes;
    private int           sessionCount;

    private Context context;


    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);


    public TestAdaptor(List<String> sessionNames, List<Date> startTimes, List<Date> endTimes,
                       List<Integer> timeUntilNext){

        this.sessionNames = sessionNames;
        this.startTimes = startTimes;
        this.endTimes = endTimes;
        this.timeUntilNext = timeUntilNext;

        sessionCount = sessionNames.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.adaptor_timetable_v2, parent, false);

        context = parent.getContext();

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position){

        holder.sessionName.setText(sessionNames.get(position));
        holder.startTime.setText(sdf.format(startTimes.get(position)));
//        holder.endTime.setText(sdf.format(endTimes.get(position)));

        setBottomMargin(holder.root, position);
        setHeight(holder.root, position);

        if(position == getCurrentSession()){
            holder.indicator.setVisibility(View.VISIBLE);
        }

        setLine(holder.line, position);

    }


    private void setLine(View v, int p){

        if(timeUntilNext.get(p) > 0){
            try {
                if(timeUntilNext.get(p - 1) > 0){
                    v.setBackground(context.getDrawable(R.drawable.line_full));
                } else{
                    v.setBackground(context.getDrawable(R.drawable.line_end));
                }
            } catch(IndexOutOfBoundsException e) {
                v.setBackground(context.getDrawable(R.drawable.line_full));
            }
        } else{
            if(p == sessionCount - 1){
                try {
                    if(timeUntilNext.get(p - 1) > 0){
                        v.setBackground(context.getDrawable(R.drawable.line_full));
                    } else{
                        v.setBackground(context.getDrawable(R.drawable.line_end));
                    }
                } catch(IndexOutOfBoundsException e) {
                    v.setBackground(context.getDrawable(R.drawable.line_full));
                }
            } else{
                try {
                    if(timeUntilNext.get(p - 1) > 0){
                        v.setBackground(context.getDrawable(R.drawable.line_start));
                    } else{
                        v.setBackground(context.getDrawable(R.drawable.line_middle));
                    }
                } catch(IndexOutOfBoundsException e) {
                    v.setBackground(context.getDrawable(R.drawable.line_start));
                }
            }
        }
    }


    private void setBottomMargin(View view, int position){

        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

        int leftMargin  = lp.leftMargin;
        int rightMargin = lp.rightMargin;
        int topMargin   = lp.topMargin;

        Resources r = context.getResources();

        int bottomMargin = position == sessionCount - 1 ? 8 : timeUntilNext.get(position) * 3;
        bottomMargin = (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, bottomMargin, r.getDisplayMetrics());

        if(position == 0){
            topMargin = (int) TypedValue
                    .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, r.getDisplayMetrics());
        }

        lp.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);


    }


    private void setHeight(View v, int position){

        long length = endTimes.get(position).getTime() - startTimes.get(position).getTime();
        length /= 60000;

        Resources r = context.getResources();
        length = length <= 10 ? 10 : length * 3;

        v.getLayoutParams().height = (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, length, r.getDisplayMetrics());
    }


    private int getCurrentSession(){

        Calendar c = Calendar.getInstance();

        for(int i = 0; i < endTimes.size(); i++){
            if(c.getTime().before(endTimes.get(i))){
                return i;
            }
        }
        return 0;
    }


    @Override
    public int getItemCount(){

        return sessionCount;
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        TextView sessionName, startTime, endTime;
        LinearLayout root;
        View         indicator;
        FrameLayout  line;


        ViewHolder(View itemView){

            super(itemView);

            sessionName = (TextView) itemView.findViewById(R.id.item_title);
            startTime = (TextView) itemView.findViewById(R.id.time_start);
            endTime = (TextView) itemView.findViewById(R.id.time_end);

            indicator = itemView.findViewById(R.id.current_ind);
            line = (FrameLayout) itemView.findViewById(R.id.item_line);

            root = (LinearLayout) itemView.findViewById(R.id.timetable2_root);

        }
    }
}
